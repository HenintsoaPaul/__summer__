package src.summer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a controller method that need authorization check before invocation.
 * Before invocation, the framework will attempt to get authorization data variable names from the web.xml file. Then, it
 * will attempt to get the data values from the Session.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface Authorized {
    int roleLevel() default 0;
}
