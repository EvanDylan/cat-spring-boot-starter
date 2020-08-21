package org.rhine.cat.spring.boot.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({TraceConfigurationImportSelector.class, CatAutoConfiguration.class})
@Documented
public @interface EnableCat {

    /**
     * 设定在哪些环境开启监控，默认所有环境都开启，也可以单独设定为以下组合中的一种或多种
     * 1. mit
     * 2. test
     * 3. uat
     * 4. prod
     */
    EnvEnum[] env() default {};

}