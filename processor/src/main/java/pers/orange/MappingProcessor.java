package pers.orange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pers.orange.field.MappingAnnotationMirror;

/**
 * @author orange add
 */
@SupportedAnnotationTypes("pers.orange.To") // 对被@To注解的类进行扫描
@SupportedSourceVersion(SourceVersion.RELEASE_11) // 支持的JDK版本
// 继承AbstractProcessor类,实现process方法
public class MappingProcessor extends AbstractProcessor {
    private Messager messager; // 编译输出日志
    private Filer filer; // 文件输出

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init( processingEnv );
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith( To.class ); // 获得被@To注解修饰的元素集合
        for ( Element element : elements ) {
            messager.printMessage( Diagnostic.Kind.NOTE, element.getSimpleName().toString() );
            To annotation = element.getAnnotation( To.class );
            TypeMirror sourceTypeMirror = element.asType(); // 获得源对象类型
            TypeMirror targetTypeMirror = getTargetTypeMirror( annotation ); // 获得目标对象类型
            List<AnnotationSpec> fieldAnnotation = getFieldAnnotation( element.getEnclosedElements() );
            createMapperFile( sourceTypeMirror, targetTypeMirror, fieldAnnotation );
        }
        return false;
    }

    private TypeMirror getTargetTypeMirror(To annotation) {
        TypeMirror typeMirror = null;
        try {
            annotation.target();
        }
        catch ( MirroredTypeException e ) {
            typeMirror = e.getTypeMirror();
        }
        return typeMirror;
    }

    private void createMapperFile(TypeMirror sourceTypeMirror, TypeMirror targetTypeMirror,
                                  List<AnnotationSpec> fieldAnnotationSpec) {
        String sourceName = getSimpleName( sourceTypeMirror );
        String targetName = getSimpleName( targetTypeMirror );
        String mapperFileName = sourceName + "To" + targetName + "Mapper";
        String packageName = getPackageName( sourceTypeMirror );
        // 使用JavaPoet框架,简化Java文件的生成,也可以直接使用字符串拼接来实现
        TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder( mapperFileName )
            .addModifiers( Modifier.PUBLIC )
            .addAnnotation( Mapper.class )
            .addSuperinterface( ParameterizedTypeName.get(
                ClassName.get( CommonMapper.class ),
                ClassName.get( sourceTypeMirror ),
                ClassName.get( targetTypeMirror )
            ) );

        if ( !fieldAnnotationSpec.isEmpty() ) {
            typeSpecBuilder.addMethods( createMappingMethodSpec(
                sourceTypeMirror,
                targetTypeMirror,
                fieldAnnotationSpec
            ) );
        }

        JavaFile javaFile = JavaFile.builder( packageName, typeSpecBuilder.build() )
            .build();
        try {
            javaFile.writeTo( filer );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private String getSimpleName(TypeMirror typeMirror) {
        String mirrorName = typeMirror.toString();
        return mirrorName.substring( mirrorName.lastIndexOf( "." ) + 1 );
    }

    private String getPackageName(TypeMirror typeMirror) {
        String mirrorName = typeMirror.toString();
        return mirrorName.substring( 0, mirrorName.lastIndexOf( "." ) );
    }


    private List<MethodSpec> createMappingMethodSpec(TypeMirror sourceTypeMirror, TypeMirror targetTypeMirror,
                                                     List<AnnotationSpec> annotationSpecs) {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        MethodSpec createMethodSpec = MethodSpec.methodBuilder( "to" )
            .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
            .returns( ClassName.get( targetTypeMirror ) )
            .addParameter( TypeName.get( sourceTypeMirror ), "source" )
            .addAnnotations( annotationSpecs )
            .build();
        ParameterSpec mappingTargetParameterSpec = ParameterSpec.builder( TypeName.get( targetTypeMirror ), "target" )
            .addAnnotation( MappingTarget.class )
            .build();
        MethodSpec updateMethodSpec = MethodSpec.methodBuilder( "to" )
            .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
            .returns( TypeName.VOID )
            .addParameter( TypeName.get( sourceTypeMirror ), "source" )
            .addParameter( mappingTargetParameterSpec )
            .addAnnotations( annotationSpecs )
            .build();
        methodSpecs.add( createMethodSpec );
        methodSpecs.add( updateMethodSpec );
        return methodSpecs;
    }

    private List<AnnotationSpec> getFieldAnnotation(List<? extends Element> elements) {
        List<AnnotationSpec> annotationSpecs = new ArrayList<>();
        for ( Element element : elements ) {
            ToField toField = null;
            if ( element.getKind() != ElementKind.FIELD ||
                ( toField = element.getAnnotation( ToField.class ) ) == null ) {
                continue;
            }
            String fieldSourceName = element.getSimpleName().toString();
            String fieldTargetName = toField.target();
            MappingAnnotationMirror mappingAnnotationMirror = new MappingAnnotationMirror(
                fieldSourceName,
                fieldTargetName
            );
            annotationSpecs.add( mappingAnnotationMirror.getSpec() );
        }
        return annotationSpecs;
    }
}
