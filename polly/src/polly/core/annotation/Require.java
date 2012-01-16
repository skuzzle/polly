package polly.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Require {
    Class<?> component() default None.class;
    int state()          default -1;
}
