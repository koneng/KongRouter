package com.shopee.router.processor;

import com.google.auto.service.AutoService;
import com.shopee.router.Constants;
import com.shopee.router.annotation.RouterField;
import com.shopee.router.annotation.RouterTarget;
import com.shopee.router.interfaces.IRouterMap;
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
import java.util.Iterator;
import java.util.List;
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
import javax.lang.model.element.VariableElement;

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
        Map<String, String> fullNameMap = new HashMap<>();
        Map<String, Map<String, String>> pathFieldMap = new HashMap<>();
        for(Element element : rootElements) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                String fullName = typeElement.getQualifiedName().toString();
                RouterTarget target = typeElement.getAnnotation(RouterTarget.class);
                String path = target.path();
                fullNameMap.put(path, fullName);
                List<Element> elements = (List<Element>) element.getEnclosedElements();
                Map<String, String> subMap = new HashMap<>();
                for(Element e : elements) {
                    if(e instanceof VariableElement) {
                        VariableElement variableElement = (VariableElement) e;
                        RouterField routerField = variableElement.getAnnotation(RouterField.class);
                        if(routerField != null) {
                            String key = routerField.value();
                            String fieldName = variableElement.getSimpleName().toString();
                            subMap.put(key, fieldName);
                        }
                    }
                }
                pathFieldMap.put(path, subMap);
            }
        }
        generatedRoot(fullNameMap, pathFieldMap);
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        annotationTypes.add(RouterTarget.class.getName());
        annotationTypes.add(RouterField.class.getName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void generatedRoot(Map<String, String> fullNameMap, Map<String, Map<String, String>> pathFieldMap) {

        //创建参数类型 Map<String, Class activity>
        ParameterizedTypeName classTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Class.class)
        );

        //创建属性 Map<String, Class activity> routers;
        FieldSpec classFieldSpec = FieldSpec.builder(classTypeName, "mActivityRoutes")
                .initializer("new $T<>()", ClassName.get(HashMap.class))
                .addModifiers(PRIVATE)
                .build();

        //生成参数 Map<String,Class activity> routes = new HashMap<> ();
        ParameterSpec parameter = ParameterSpec.builder(classTypeName, "mActivityRoutes").build();

        //生成函数 public Map<String,Class> loadInfo()
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadPathClassInfo")
                .returns(parameter.type)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC);
        //.addParameter(parameter);

        //生成函数体
        for (Map.Entry<String, String> entry : fullNameMap.entrySet()) {
            String fullName = entry.getValue();
            int index = fullName.lastIndexOf(".");
            int count = fullName.length();
            String packetName = fullName.substring(0, index);
            String simpleName = fullName.substring(index + 1, count);
            methodBuilder.addStatement("mActivityRoutes.put($S, $T.class)", entry.getKey(), ClassName.get(packetName, simpleName));
        }
        methodBuilder.addStatement("return mActivityRoutes");


        //-----------------------------------------------------------------

        //创建参数类型 Map<String path, Map<String, String>>
        ParameterizedTypeName fieldTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(String.class)
                )
        );

        //创建属性 Map<String path, Map<String, String>> mFieldRoutes = new HashMap<> ();
        FieldSpec fieldSpec = FieldSpec.builder(fieldTypeName, "mFieldRoutes")
                .initializer("new $T<>()", ClassName.get(HashMap.class))
                .addModifiers(PRIVATE)
                .build();


        //生成参数 Map<String field, String fieldName> routes
        ParameterSpec fieldParameter = ParameterSpec.builder(fieldTypeName, "mFieldRoutes").build();

        //生成函数 public Map<String field, String fieldName> loadInfo()
        MethodSpec.Builder fieldMethodBuilder = MethodSpec.methodBuilder("loadPathFieldInfo")
                .returns(fieldParameter.type)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC);
                //.addParameter(parameter);

        //生成函数体
        int index = 0;
        for (Map.Entry<String, Map<String, String>> entry : pathFieldMap.entrySet()) {
            String path = entry.getKey();
            Map<String, String> map = entry.getValue();
            int size = map.size();
            if(size > 0) {
                fieldMethodBuilder.addStatement("Map<String, String> map_$L = new $T<>()", index, ClassName.get(HashMap.class));
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String fieldName = map.get(key);
                    fieldMethodBuilder.addStatement("map_$L.put($S, $S)", index, key, fieldName);
                }
                fieldMethodBuilder.addStatement("mFieldRoutes.put($S, map_$L)", path, index);
                index++;
            }
        }
        fieldMethodBuilder.addStatement("return mFieldRoutes");

        //生成类
        String className = Constants.ROUTER_MAP_NAME;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC)
                .addSuperinterface(IRouterMap.class)
                .addField(fieldSpec)
                .addField(classFieldSpec)
                .addMethod(fieldMethodBuilder.build())
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
