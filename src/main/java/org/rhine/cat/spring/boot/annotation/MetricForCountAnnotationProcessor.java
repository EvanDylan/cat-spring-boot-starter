package org.rhine.cat.spring.boot.annotation;

import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

public class MetricForCountAnnotationProcessor implements BeanPostProcessor, PriorityOrdered {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        Map<Method, MetricForCount> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                new MethodIntrospector.MetadataLookup<MetricForCount>() {
                    @Override
                    public MetricForCount inspect(Method method) {
                        return AnnotationUtils.getAnnotation(method, MetricForCount.class);
                    }
                });
        if (CollectionUtils.isEmpty(annotatedMethods)) {
            return bean;
        } else {
            for (Map.Entry<Method, MetricForCount> entry : annotatedMethods.entrySet()) {
                return processMetricForCount(bean, entry.getKey(), entry.getValue());
            }
        }
        return bean;
    }

    private Object processMetricForCount(Object bean, Method method, MetricForCount metricForCount) {
        if (StringUtils.isEmpty(metricForCount.name())) {
            logger.warn("@MetricForCount annotation on " + method.getName() + " name can't be null or empty");
            return bean;
        }
        if (metricForCount.count() <= 0) {
            logger.warn("@MetricForCount annotation on " + method.getName() + " value can't be zero or negative");
            return bean;
        }
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(bean);
        factory.addAdvice(new MetricLoggerAdvice(metricForCount));
        return factory.getProxy();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private static class MetricLoggerAdvice implements AfterReturningAdvice {

        private MetricForCount metricForCount;

        @Override
        public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
            AnnotationMethodMatcher matcher = new AnnotationMethodMatcher(MetricForCount.class);
            if (matcher.matches(method, target.getClass())) {
                Cat.logMetricForCount(metricForCount.name(), metricForCount.count());
            }
        }

        public MetricLoggerAdvice(MetricForCount metricForCount) {
            this.metricForCount = metricForCount;
        }
    }
}
