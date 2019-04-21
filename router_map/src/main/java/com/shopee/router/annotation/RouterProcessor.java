package com.shopee.router.annotation;

import com.google.auto.service.AutoService;
import com.shopee.router.annotation.interfaces.Constants;
import com.shopee.router.annotation.interfaces.IRouterMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;


@AutoService(value = Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private String mModuleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 获取用户配置的[moduleName]
       /* Map<String, String> options = processingEnv.getOptions();
        if (options != null) {
            mModuleName = options.get(Constants.KEY_MODULE_NAME);
        }*/
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> rootElements = roundEnvironment.getElementsAnnotatedWith(RouterTarget.class);
        Map<String, String> map = new HashMap<>();
        for(Element element : rootElements) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                String fullName = typeElement.getQualifiedName().toString();
                RouterTarget target = typeElement.getAnnotation(RouterTarget.class);
                String path = target.path();
                map.put(path, fullName);
            }
        }
        generatedRoot(map);
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        annotationTypes.add(RouterTarget.class.getName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    private void generatedRoot(Map<String, String> rootMap) {
        //创建参数类型 Map<String, Class activity>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Class.class)
        );

        //创建属性 Map<String, Class activity> routers;
        FieldSpec fieldSpec = FieldSpec.builder(parameterizedTypeName, "routes")
                .initializer("new $T<>()", ClassName.get(HashMap.class))
                .addModifiers(PRIVATE)
                .build();


        //生成参数 Map<String,Class activity> routes
        ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "routes").build();

        //生成函数 public void loadInfo(Map<String,Class activity> routes)
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadInfo")
                .returns(parameter.type)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC);
                //.addParameter(parameter);

        //生成函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            String fullName = entry.getValue();
            int index = fullName.lastIndexOf(".");
            int count = fullName.length();
            String packetName = fullName.substring(0, index);
            String simpleName = fullName.substring(index + 1, count);
            methodBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(packetName, simpleName));
        }
        methodBuilder.addStatement("return routes");

        //生成类
        String className = Constants.ROUTER_MAP_NAME;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC)
                .addSuperinterface(IRouterMap.class)
                .addField(fieldSpec)
                .addMethod(methodBuilder.build())
                .build();

        try {
            //生成java文件，File如果生成在Router module会造成找不到Annotation，生成不了路由map
            JavaFile.builder(Constants.ROUTER_MAP_PACKAGE_NAME, typeSpec).build().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
