package com.readingshare.common.doclet;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SimpleDocTreeVisitor;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

/**
 * JavadocをJSON形式で出力するカスタムDoclet。
 *
 * このDocletは、Javaソースコードのドキュメンテーションコメントを
 * 解析し、構造化されたJSON形式で出力します。
 */
public class JsonDoclet implements Doclet {

    private static final String OUTPUT_FILE_OPTION = "-outputFile";
    private String outputFile = "javadoc.json";
    private Reporter reporter;
    private DocTrees docTrees;
    // private DocletEnvironment environment;

    @Override
    public void init(Locale locale, Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public String getName() {
        return "JsonDoclet";
    }

    @Override
    public Set<? extends Doclet.Option> getSupportedOptions() {
        return Set.of(new OutputFileOption());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        try {
            // this.environment = environment;
            this.docTrees = environment.getDocTrees();
            // すべてのクラスを取得
            Set<TypeElement> classes = ElementFilter.typesIn(environment.getIncludedElements())
                    .stream()
                    .filter(e -> e.getKind() == ElementKind.CLASS || e.getKind() == ElementKind.INTERFACE)
                    .collect(Collectors.toSet());

            // JSONデータを構築
            Map<String, Object> documentation = new HashMap<>();
            documentation.put("generatedAt", new Date().toString());
            documentation.put("classes", classes.stream()
                    .map(clazz -> processClass(clazz))
                    .collect(Collectors.toList()));

            // JSONファイルに出力
            JsonMapper mapper = new JsonMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(outputFile), documentation);

            reporter.print(Diagnostic.Kind.NOTE, "JSON documentation generated: " + outputFile);
            return true;

        } catch (Exception e) {
            reporter.print(Diagnostic.Kind.ERROR, "Error generating JSON documentation: " + e.getMessage());
            return false;
        }
    }

    /**
     * クラス要素を処理してJSON用のマップを返す
     */
    private Map<String, Object> processClass(TypeElement clazz) {
        Map<String, Object> classMap = new HashMap<>();
        classMap.put("name", clazz.getSimpleName().toString());
        DocCommentTree tree = docTrees.getDocCommentTree(clazz);
        // String docComment = environment.getElementUtils().getDocComment(clazz);
        // DEBUG: dump raw Javadoc for AuthController
        // System.out.println(clazz.getSimpleName().toString());
        // if (clazz.getSimpleName().toString().equals("AuthController")) {
        // System.out.println(docComment);
        // }
        if (tree != null) {
            // summary
            classMap.put("description", docTreeListToString(tree.getFirstSentence()));

            JsonDocTreeVisitor visitor = new JsonDocTreeVisitor();
            for (DocTree tag : tree.getBlockTags()) {
                tag.accept(visitor, classMap);
            }
        } else {
            // If no Javadoc comment, use the class name as a fallback description.
            classMap.put("description", clazz.getSimpleName().toString());
        }
        List<Map<String, Object>> methods = new ArrayList<>();
        for (ExecutableElement method : ElementFilter.methodsIn(clazz.getEnclosedElements())) {
            methods.add(processMethod(method));
        }
        classMap.put("methods", methods);
        return classMap;
    }

    /**
     * メソッド要素を処理してJSON用のマップを返す
     */
    private Map<String, Object> processMethod(ExecutableElement method) {
        Map<String, Object> methodMap = new HashMap<>();
        methodMap.put("name", method.getSimpleName().toString());

        // summary
        DocCommentTree tree = docTrees.getDocCommentTree(method);
        if (tree != null) {
            methodMap.put("description", docTreeListToString(tree.getFirstSentence()));
        } else {
            // If no Javadoc comment, use the method signature as a fallback description.
            methodMap.put("description", method.getSimpleName().toString() + "()");
        }

        // Always create a params list, which will be empty if the method has no
        // parameters.
        List<Map<String, Object>> paramsList = new ArrayList<>();
        List<? extends VariableElement> methodParameters = method.getParameters();
        for (VariableElement p : methodParameters) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("name", p.getSimpleName().toString());
            String typeFqn = p.asType().toString();
            paramMap.put("type", typeFqn.replaceAll("([a-zA-Z0-9_]+\\.)+", ""));
            paramsList.add(paramMap);
        }
        methodMap.put("params", paramsList);

        // Handle return type from signature for non-void methods.
        // The description will be added later by the visitor if an @return tag exists.
        TypeMirror returnType = method.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            Map<String, Object> returnInfo = new HashMap<>();
            String typeFqn = returnType.toString();
            returnInfo.put("type", typeFqn.replaceAll("([a-zA-Z0-9_]+\\.)+", ""));
            methodMap.put("return", returnInfo);
        }

        // Process Javadoc comments to add descriptions and other tags.
        if (tree != null) {
            Map<String, VariableElement> parameterElements = method.getParameters().stream()
                    .collect(Collectors.toMap(p -> p.getSimpleName().toString(), p -> p, (p1, p2) -> p1));
            JsonDocTreeVisitor visitor = new JsonDocTreeVisitor(parameterElements);
            for (DocTree tag : tree.getBlockTags()) {
                tag.accept(visitor, methodMap);
            }

            // If a void method has an @return tag, the visitor will have created a "return"
            // map.
            // We just need to add the "void" type to it.
            if (returnType.getKind() == TypeKind.VOID && methodMap.containsKey("return")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> returnInfo = (Map<String, Object>) methodMap.get("return");
                returnInfo.put("type", "void");
            }
        }
        return methodMap;
    }

    private String docTreeListToString(List<? extends DocTree> list) {
        return list.stream().map(t -> {
            switch (t.getKind()) {
                case TEXT:
                    return ((TextTree) t).getBody();
                case LITERAL:
                    return ((LiteralTree) t).getBody().getBody();
                default:
                    return t.toString();
            }
        }).collect(Collectors.joining());
    }

    private class JsonDocTreeVisitor extends SimpleDocTreeVisitor<Void, Map<String, Object>> {

        private final Map<String, VariableElement> parameterElements;

        public JsonDocTreeVisitor() {
            this.parameterElements = null;
        }

        public JsonDocTreeVisitor(Map<String, VariableElement> parameterElements) {
            this.parameterElements = parameterElements;
        }

        @Override
        public Void visitAuthor(AuthorTree node, Map<String, Object> p) {
            @SuppressWarnings("unchecked")
            List<String> authors = (List<String>) p.computeIfAbsent("author", k -> new ArrayList<>());
            authors.add(docTreeListToString(node.getName()).trim());
            return null;
        }

        @Override
        public Void visitThrows(ThrowsTree node, Map<String, Object> p) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> throwsList = (List<Map<String, String>>) p.computeIfAbsent("throws",
                    k -> new ArrayList<>());
            Map<String, String> thr = new HashMap<>();
            thr.put("exception", node.getExceptionName().toString());
            thr.put("description",
                    docTreeListToString(node.getDescription()).trim());
            throwsList.add(thr);
            return null;
        }

        @Override
        public Void visitUnknownBlockTag(UnknownBlockTagTree node, Map<String, Object> p) {
            String tagName = node.getTagName();
            String content = docTreeListToString(node.getContent()).trim();
            Object existing = p.get(tagName);
            if (existing == null) {
                p.put(tagName, content);
            } else if (existing instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) existing;
                list.add(content);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(existing);
                list.add(content);
                p.put(tagName, list);
            }
            return null;
        }

        @Override
        public Void visitParam(ParamTree node, Map<String, Object> p) {
            if (parameterElements == null) {
                return null;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> params = (List<Map<String, Object>>) p.get("params");
            if (params == null) {
                return null;
            }

            String paramName = node.getName().toString();
            for (Map<String, Object> param : params) {
                if (param.get("name").equals(paramName)) {
                    param.put("description", docTreeListToString(node.getDescription()).trim());
                    break;
                }
            }
            return null;
        }

        @Override
        public Void visitReturn(ReturnTree node, Map<String, Object> p) {
            @SuppressWarnings("unchecked")
            Map<String, Object> returnInfo = (Map<String, Object>) p.computeIfAbsent("return",
                    k -> new HashMap<>());
            String desc = docTreeListToString(node.getDescription()).trim();
            returnInfo.put("description", desc);
            return null;
        }
    }

    /**
     * 出力ファイルオプションの実装。
     */
    private class OutputFileOption implements Doclet.Option {
        @Override
        public int getArgumentCount() {
            return 1;
        }

        @Override
        public String getDescription() {
            return "出力JSONファイルのパス";
        }

        @Override
        public Doclet.Option.Kind getKind() {
            return Doclet.Option.Kind.STANDARD;
        }

        @Override
        public List<String> getNames() {
            return List.of(OUTPUT_FILE_OPTION);
        }

        @Override
        public String getParameters() {
            return "<file>";
        }

        @Override
        public boolean process(String option, List<String> arguments) {
            if (arguments.size() == 1) {
                outputFile = arguments.get(0);
                return true;
            }
            return false;
        }
    }

    /**
     * デバッグ用のmainメソッド。
     *
     * @param args
     */
    public static void main(String[] args) {
        DocumentationTool javadoc = ToolProvider.getSystemDocumentationTool();
        int result = javadoc.run(System.in, System.out, System.err,
                "-doclet", JsonDoclet.class.getName(),
                "-sourcepath", "backend/src/main/java",
                "-subpackages", "com.readingshare",
                "-outputFile", "backend/target/javadoc-debug.json");
        if (result == 0) {
            System.out.println("Doclet executed successfully. Output: backend/target/javadoc-debug.json");
        } else {
            System.err.println("Doclet execution failed with code: " + result);
        }
    }
}
