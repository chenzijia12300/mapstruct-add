package pers.orange.field;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import org.mapstruct.Mapping;

/**
 * @author orange add
 */
public class MappingAnnotationMirror {

    private String source;

    private String target;

    public MappingAnnotationMirror(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public AnnotationSpec getSpec(){
        AnnotationSpec annotationSpec = AnnotationSpec
            .builder( Mapping.class )
            .addMember( "source", "$S", source )
            .addMember( "target","$S",  target )
            .build();
        return annotationSpec;
    }
}
