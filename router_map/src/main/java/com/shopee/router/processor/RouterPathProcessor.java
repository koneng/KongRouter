package com.shopee.router.processor;

import com.google.auto.service.AutoService;
import com.shopee.router.annotation.RouterPath;
import com.shopee.router.Constants;
import com.shopee.router.interfaces.IRouterPathMap;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;


@AutoService(value = Processor.class)
public class RouterPathProcessor extends AbstractProcessor {

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
        Set<? extends Element> rootElements = roundEnvironment.getElementsAnnotatedWith(RouterPath.class);
        Map<String, String> map = new HashMap<>();
        for(Element element : rootElements) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement executableElement = (ExecutableElement) element;
                String methodName = executableElement.getSimpleName().toString();
                RouterPath routerPath = executableElement.getAnnotation(RouterPath.class);
                String path = routerPath.path();
                map.put(path, methodName);
            }
        }
        generatedRoot(map);
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        annotationTypes.add(RouterPath.class.getName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void generatedRoot(Map<String, String> rootMap) {
        //创建参数类型 Map<String path, String methodName>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(String.class)
        );

        //创建属性 Map<String path, String methodName> routers = new HashMap<> ();
        FieldSpec fieldSpec = FieldSpec.builder(parameterizedTypeName, "routes")
                .initializer("new $T<>()", ClassName.get(HashMap.class))
                .addModifiers(PRIVATE)
                .build();


        //生成参数 Map<String path, String methodName> routes
        ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "routes").build();

        //生成函数 public Map<String path, String methodName> loadInfo()
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadInfo")
                .returns(parameter.type)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC);
                //.addParameter(parameter);

        //生成函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            methodBuilder.addStatement("routes.put($S, $S)", entry.getKey(), entry.getValue());
        }
        methodBuilder.addStatement("return routes");

        //生成类
        String className = Constants.ROUTER_PATH_MAP_NAME;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC)
                .addSuperinterface(IRouterPathMap.class)
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
