package polly.moduleloader.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Module {
    Provide[] provides() default {};
    Require[] requires() default {}; 
    boolean startUp() default false;
}
