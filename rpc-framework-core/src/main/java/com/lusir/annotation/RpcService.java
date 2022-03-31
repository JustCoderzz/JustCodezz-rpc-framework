package com.lusir.annotation;

import java.lang.annotation.*;

/**
 * @author lusir
 * @date 2022/3/31 - 15:44
 **/
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcService {

    String version() default "";

    String group() default  "";
}
