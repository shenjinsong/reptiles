package reptiles.config;

import java.lang.annotation.*;

/**
 * @author 大叔
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParamCheck {

    String[] value() default {};

}
