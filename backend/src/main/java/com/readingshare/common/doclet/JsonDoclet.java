package com.readingshare.common.doclet;

import java.io.File;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
     * メソッド情報を処理してMapに変換する。
     */
    private Map<String, Object> processMethod(ExecutableElement method, DocletEnvironment environment) {
        Map<String, Object> methodInfo = new HashMap<>();

        methodInfo.put("name", method.getSimpleName().toString());
        methodInfo.put("returnType", method.getReturnType().toString());
        methodInfo.put("modifiers", method.getModifiers().stream()
                .map(Modifier::toString)
                .collect(Collectors.toList()));

        // ドキュメンテーションコメント
        String docComment = environment.getElementUtils().getDocComment(method);
        if (docComment != null && !docComment.trim().isEmpty()) {
            methodInfo.put("documentation", docComment.trim());
        }

        // パラメータドキュメントを取得
        Map<String, String> paramDocs = new HashMap<>();
        if (docComment != null) {
            for (String line : docComment.trim().split("\\r?\\n")) {
                line = line.trim();
                if (line.startsWith("@param")) {
                    String[] parts = line.split("\\s+", 3);
                    if (parts.length == 3) {
                        paramDocs.put(parts[1], parts[2]);
                    }
                }
            }
        }

        // パラメータ
        List<? extends VariableElement> parameters = method.getParameters();
        if (!parameters.isEmpty()) {
            methodInfo.put("parameters", parameters.stream()
                    .map(param -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", param.getSimpleName().toString());
                        map.put("type", param.asType().toString());
                        String pDoc = paramDocs.get(param.getSimpleName().toString());
                        if (pDoc != null && !pDoc.isEmpty()) {
                            map.put("documentation", pDoc);
                        }
                        return map;
                    })
                    .collect(Collectors.toList()));
        }

        // 例外
        List<? extends TypeMirror> thrownTypes = method.getThrownTypes();
        if (!thrownTypes.isEmpty()) {
            methodInfo.put("exceptions", thrownTypes.stream()
                    .map(TypeMirror::toString)
                    .collect(Collectors.toList()));
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
