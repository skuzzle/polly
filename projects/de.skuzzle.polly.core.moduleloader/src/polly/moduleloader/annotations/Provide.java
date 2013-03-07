package polly.moduleloader.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Provide {
    Class<?> component() default None.class;
    int state()          default -1;
}