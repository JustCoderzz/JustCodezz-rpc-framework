package com.lusir.annotation;

import java.lang.annotation.*;

/**
 * @author lusir
 * @date 2022/3/31 - 15:46
 **/
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {
    String version() default "";


    String group() default "";
}
