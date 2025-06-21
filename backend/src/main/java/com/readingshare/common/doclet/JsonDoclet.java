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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.DocTrees;

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
            // すべてのクラスを取得
            Set<TypeElement> classes = ElementFilter.typesIn(environment.getIncludedElements())
                    .stream()
                    .filter(e -> e.getKind() == ElementKind.CLASS || e.getKind() == ElementKind.INTERFACE)
                    .collect(Collectors.toSet());

            // JSONデータを構築
            Map<String, Object> documentation = new HashMap<>();
            documentation.put("generatedAt", new Date().toString());
            documentation.put("classes", classes.stream()
                    .map(clazz -> processClass(clazz, environment))
                    .collect(Collectors.toList()));

            // JSONファイルに出力
            ObjectMapper mapper = new ObjectMapper();
            // mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(outputFile), documentation);

            reporter.print(Diagnostic.Kind.NOTE, "JSON documentation generated: " + outputFile);
            return true;

        } catch (Exception e) {
            reporter.print(Diagnostic.Kind.ERROR, "Error generating JSON documentation: " + e.getMessage());
            return false;
        }
    }

    /**
     * クラス情報を処理してMapに変換する。
     */
    private Map<String, Object> processClass(TypeElement classElement, DocletEnvironment environment) {
        Map<String, Object> classInfo = new HashMap<>();

        // 基本情報
        classInfo.put("name", classElement.getSimpleName().toString());
        // simple class name
        classInfo.put("simpleName", classElement.getSimpleName().toString());
        classInfo.put("qualifiedName", classElement.getQualifiedName().toString());
        classInfo.put("kind", classElement.getKind().toString());
        classInfo.put("package", getPackageName(classElement));

        // ドキュメンテーションコメント
        String docComment = environment.getElementUtils().getDocComment(classElement);
        if (docComment != null && !docComment.trim().isEmpty()) {
            classInfo.put("documentation", docComment.trim());
        }
        // DocTree APIでカスタムタグを収集
        DocTrees docTrees = environment.getDocTrees();
        DocCommentTree classTree = docTrees.getDocCommentTree(classElement);
        if (classTree != null) {
            // 標準タグとしてのauthorを抽出
            List<String> authors = new ArrayList<>();
            classTree.getBlockTags().stream()
                    .filter(t -> t.getKind() == DocTree.Kind.AUTHOR)
                    .map(t -> ((AuthorTree) t).getName().toString())
                    .forEach(authors::add);
            if (!authors.isEmpty()) {
                classInfo.put("authors", authors);
            }
            // カスタムタグ収集 (@componentIdName など)
            Map<String, List<String>> customTags = new HashMap<>();
            for (DocTree tag : classTree.getBlockTags()) {
                if (tag instanceof UnknownBlockTagTree) {
                    UnknownBlockTagTree ut = (UnknownBlockTagTree) tag;
                    String name = ut.getTagName();
                    String val = ut.getContent().stream().map(DocTree::toString)
                            .collect(Collectors.joining(" ")).trim();
                    customTags.computeIfAbsent(name, k -> new ArrayList<>()).add(val);
                }
            }
            if (!customTags.isEmpty()) {
                classInfo.put("customTags", customTags);
            }
        }

        // スーパークラス
        TypeMirror superclass = classElement.getSuperclass();
        if (superclass != null) {
            classInfo.put("superclass", superclass.toString());
        }

        // インターフェース
        List<String> interfaces = classElement.getInterfaces().stream()
                .map(TypeMirror::toString)
                .collect(Collectors.toList());
        if (!interfaces.isEmpty()) {
            classInfo.put("interfaces", interfaces);
        }

        // アノテーション
        List<String> annotations = classElement.getAnnotationMirrors().stream()
                .map(am -> am.getAnnotationType().toString())
                .collect(Collectors.toList());
        if (!annotations.isEmpty()) {
            classInfo.put("annotations", annotations);
        }

        // メソッド
        List<ExecutableElement> methods = ElementFilter.methodsIn(classElement.getEnclosedElements());
        if (!methods.isEmpty()) {
            classInfo.put("methods", methods.stream()
                    .map(method -> processMethod(method, environment))
                    .collect(Collectors.toList()));
        }

        // フィールド
        List<VariableElement> fields = ElementFilter.fieldsIn(classElement.getEnclosedElements());
        if (!fields.isEmpty()) {
            classInfo.put("fields", fields.stream()
                    .map(field -> processField(field, environment))
                    .collect(Collectors.toList()));
        }

        return classInfo;
    }

    /**
     * TypeMirrorから簡易名を取得する。
     */
    private String getSimpleName(TypeMirror tm) {
        if (tm instanceof DeclaredType) {
            return ((DeclaredType) tm).asElement().getSimpleName().toString();
        }
        String name = tm.toString();
        return name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : name;
    }

    /**
     * メソッド情報を処理してMapに変換する。
     */
    private Map<String, Object> processMethod(ExecutableElement method, DocletEnvironment environment) {
        Map<String, Object> methodInfo = new HashMap<>();

        methodInfo.put("name", method.getSimpleName().toString());
        methodInfo.put("returnType", method.getReturnType().toString());
        methodInfo.put("simpleReturnType", getSimpleName(method.getReturnType()));
        methodInfo.put("modifiers", method.getModifiers().stream()
                .map(Modifier::toString)
                .collect(Collectors.toList()));

        // Javadoc ツリーを使ってサマリーを抽出
        DocTrees docTrees = environment.getDocTrees();
        DocCommentTree commentTree = docTrees.getDocCommentTree(method);

        // DocTree APIでメソッドレベルのカスタムタグを収集
        if (commentTree != null) {
            // 標準タグとしてのauthorを抽出
            List<String> authors = new ArrayList<>();
            commentTree.getBlockTags().stream()
                    .filter(t -> t.getKind() == DocTree.Kind.AUTHOR)
                    .map(t -> ((AuthorTree) t).getName().toString())
                    .forEach(authors::add);
            if (!authors.isEmpty()) {
                methodInfo.put("authors", authors);
            }
            // カスタムタグ収集
            Map<String, List<String>> customTags = new HashMap<>();
            for (DocTree tag : commentTree.getBlockTags()) {
                if (tag instanceof UnknownBlockTagTree) {
                    UnknownBlockTagTree ut = (UnknownBlockTagTree) tag;
                    String name = ut.getTagName();
                    String val = ut.getContent().stream().map(DocTree::toString)
                            .collect(Collectors.joining(" ")).trim();
                    customTags.computeIfAbsent(name, k -> new ArrayList<>()).add(val);
                }
            }
            if (!customTags.isEmpty()) {
                methodInfo.put("customTags", customTags);
            }
        }

        // throw タグのドキュメントを取得
        Map<String, String> throwsDocs = new HashMap<>();
        if (commentTree != null) {
            for (DocTree tag : commentTree.getBlockTags()) {
                if (tag.getKind() == DocTree.Kind.THROWS) {
                    ThrowsTree tTree = (ThrowsTree) tag;
                    String excName = tTree.getExceptionName().toString();
                    String excDesc = tTree.getDescription().stream()
                            .map(DocTree::toString)
                            .collect(Collectors.joining(" ")).trim();
                    throwsDocs.put(excName, excDesc);
                }
            }
        }

        // パラメータドキュメントを DocTree APIで取得
        Map<String, String> paramDocs = new HashMap<>();
        if (commentTree != null) {
            for (DocTree tag : commentTree.getBlockTags()) {
                if (tag.getKind() == DocTree.Kind.PARAM) {
                    ParamTree p = (ParamTree) tag;
                    String paramName = p.getName().getName().toString();
                    String paramDesc = p.getDescription().stream()
                            .map(DocTree::toString)
                            .collect(Collectors.joining(" ")).trim();
                    paramDocs.put(paramName, paramDesc);
                }
            }
        }

        // パラメータ
        List<? extends VariableElement> parameters = method.getParameters();
        if (!parameters.isEmpty()) {
            methodInfo.put("parameters", parameters.stream()
                    .map(param -> {
                        Map<String, Object> map = new HashMap<>();
                        TypeMirror tm = param.asType();
                        map.put("type", tm.toString());
                        map.put("simpleType", getSimpleName(tm));
                        map.put("name", param.getSimpleName().toString());
                        String pDoc = paramDocs.get(param.getSimpleName().toString());
                        if (pDoc != null && !pDoc.isEmpty()) {
                            map.put("documentation", pDoc);
                        }
                        return map;
                    })
                    .collect(Collectors.toList()));
        }

        // 例外: 宣言された throws と Javadoc の @throws をマージ
        List<Map<String, Object>> exceptionsList = new ArrayList<>();
        // 宣言された例外
        for (TypeMirror tm : method.getThrownTypes()) {
            Map<String, Object> em = new HashMap<>();
            em.put("type", tm.toString());
            em.put("simpleType", getSimpleName(tm));
            String exDoc = throwsDocs.get(getSimpleName(tm));
            if (exDoc != null)
                em.put("documentation", exDoc);
            exceptionsList.add(em);
        }
        // Javadoc の @throws タグのみ存在する例外
        for (Map.Entry<String, String> entry : throwsDocs.entrySet()) {
            String key = entry.getKey();
            boolean declared = method.getThrownTypes().stream()
                    .anyMatch(tm -> tm.toString().endsWith(key));
            if (!declared) {
                Map<String, Object> em = new HashMap<>();
                em.put("type", key);
                String simple = key.contains(".") ? key.substring(key.lastIndexOf('.') + 1) : key;
                em.put("simpleType", simple);
                em.put("documentation", entry.getValue());
                exceptionsList.add(em);
            }
        }
        if (!exceptionsList.isEmpty()) {
            methodInfo.put("exceptions", exceptionsList);
        }

        // アノテーション
        List<String> annotations = method.getAnnotationMirrors().stream()
                .map(am -> am.getAnnotationType().toString())
                .collect(Collectors.toList());
        if (!annotations.isEmpty()) {
            methodInfo.put("annotations", annotations);
        }

        return methodInfo;
    }

    /**
     * フィールド情報を処理してMapに変換する。
     */
    private Map<String, Object> processField(VariableElement field, DocletEnvironment environment) {
        Map<String, Object> fieldInfo = new HashMap<>();

        fieldInfo.put("name", field.getSimpleName().toString());
        fieldInfo.put("type", field.asType().toString());
        fieldInfo.put("modifiers", field.getModifiers().stream()
                .map(Modifier::toString)
                .collect(Collectors.toList()));

        // ドキュメンテーションコメント
        String docComment = environment.getElementUtils().getDocComment(field);
        if (docComment != null && !docComment.trim().isEmpty()) {
            fieldInfo.put("documentation", docComment.trim());
        }

        // アノテーション
        List<String> annotations = field.getAnnotationMirrors().stream()
                .map(am -> am.getAnnotationType().toString())
                .collect(Collectors.toList());
        if (!annotations.isEmpty()) {
            fieldInfo.put("annotations", annotations);
        }

        return fieldInfo;
    }

    /**
     * パッケージ名を取得する。
     */
    private String getPackageName(TypeElement element) {
        Element enclosing = element.getEnclosingElement();
        if (enclosing instanceof PackageElement) {
            return ((PackageElement) enclosing).getQualifiedName().toString();
        }
        return ""; // デフォルトパッケージ
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
}
