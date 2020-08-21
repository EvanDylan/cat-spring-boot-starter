package org.rhine.cat.spring.boot.annotation;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.rhine.cat.spring.boot.common.CatConstants;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Method;

public class CatTransactionAnnotationProcessor implements BeanPostProcessor, PriorityOrdered {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (targetClass.isAnnotationPresent(CatTransaction.class)) {
            ProxyFactory factory = new ProxyFactory();
            factory.setTarget(bean);
            factory.addAdvice(new Advice());
            return factory.getProxy();
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private static class Advice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            Object target = invocation.getThis();
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
            if (targetClass == null) {
                targetClass = target.getClass();
            }
            Transaction transaction = Cat.newTransaction(CatConstants.INNER_SERVICE,
                        targetClass.getSimpleName() + "." + method.getName());
            try {
                return invocation.proceed();
            } finally {
                if (transaction != null) {
                    transaction.complete();
                }
            }
        }
    }
}
