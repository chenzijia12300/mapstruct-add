package pers.orange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author orange add
 */
@Retention( RetentionPolicy.SOURCE )
@Target( ElementType.FIELD )
public @interface ToField {

    String target();

}
