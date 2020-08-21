package org.rhine.cat.spring.boot.annotation;

import java.lang.annotation.*;

/**
 * 业务指标统计。
 * 比如实时商品订下单量的统计、支付订单的统计。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MetricForCount {

    /**
     * 统计的名称
     */
    String name() default "";

    /**
     * 统计次数，默认为1。
     */
    int count() default 1;
}
