package src.summer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface Validate {
    String controllerVerb() default "get";
    String errorPage();
}
