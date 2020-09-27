package reptiles.paramcheck.annotation;

import reptiles.paramcheck.handler.ErrorResultHandler;

import java.lang.annotation.*;

/**
 * @author 大叔
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParamCheck {

    String[] value() default {};

    Class<ErrorResultHandler> handlerClass() default ErrorResultHandler.class;

}
