package pers.orange;

import java.io.IOException;
import java.lang.reflect.Type;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.mapstruct.Mapper;

/**
 * @author orange add
 */
@SupportedAnnotationTypes("pers.orange.To") // 对被@To注解的类进行扫描
@SupportedSourceVersion( SourceVersion.RELEASE_11 ) // 支持的JDK版本
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
            messager.printMessage( Diagnostic.Kind.NOTE, element.getSimpleName().toString());
            To annotation = element.getAnnotation( To.class );
            TypeMirror sourceTypeMirror = element.asType(); // 获得源对象类型
            TypeMirror targetTypeMirror = getTargetTypeMirror( annotation ); // 获得目标对象类型
            createMapperFile( sourceTypeMirror, targetTypeMirror );
        }
        return false;
    }

    private TypeMirror getTargetTypeMirror(To annotation){
        TypeMirror typeMirror = null;
        try {
            annotation.target();
        }
        catch ( MirroredTypeException e ) {
            typeMirror = e.getTypeMirror();
        }
        return typeMirror;
    }

    private void createMapperFile(TypeMirror sourceTypeMirror,TypeMirror targetTypeMirror){
        String sourceName = getSimpleName(  sourceTypeMirror );
        String targetName = getSimpleName(targetTypeMirror );
        String mapperFileName = sourceName+"To"+targetName+"Mapper";
        String packageName = getPackageName( sourceTypeMirror );
        // 使用JavaPoet框架,简化Java文件的生成,也可以直接使用字符串拼接来实现
        TypeSpec typeSpec = TypeSpec.interfaceBuilder( mapperFileName )
            .addModifiers( Modifier.PUBLIC )
            .addAnnotation( Mapper.class )
            .addSuperinterface( ParameterizedTypeName.get( ClassName.get( CommonMapper.class ),ClassName.get( sourceTypeMirror ),ClassName.get( targetTypeMirror ) ))
            .build();
        JavaFile javaFile = JavaFile.builder( packageName, typeSpec)
            .build();
        try {
            javaFile.writeTo( filer );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private String getSimpleName(TypeMirror typeMirror){
        String mirrorName = typeMirror.toString();
        return mirrorName.substring( mirrorName.lastIndexOf( "." )+1 );
    }

    private String getPackageName(TypeMirror typeMirror){
        String mirrorName = typeMirror.toString();
        return mirrorName.substring( 0,mirrorName.lastIndexOf( "." ) );
    }
}
