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
@SupportedAnnotationTypes("pers.orange.To")
@SupportedSourceVersion( SourceVersion.RELEASE_11 )
public class MappingProcessor extends AbstractProcessor {

    public static final String COMMON_MAPPER_CLASS_NAME = "pers.orange.CommonMapper";

    private Messager messager;

    private Types typeUtils;

    private Elements elementUtils;

    private Filer filer;

    private TypeMirror commonMapperType;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init( processingEnv );
        this.messager = processingEnv.getMessager();
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.commonMapperType = elementUtils.getTypeElement( COMMON_MAPPER_CLASS_NAME ).asType();
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage( Diagnostic.Kind.NOTE,"***MapStruct-add start***" );
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith( To.class );
        for ( Element element : elements ) {
            messager.printMessage( Diagnostic.Kind.NOTE, element.getSimpleName().toString());
            To annotation = element.getAnnotation( To.class );
            TypeMirror sourceTypeMirror = element.asType();
            TypeMirror targetTypeMirror = getTargetTypeMirror( annotation );
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
