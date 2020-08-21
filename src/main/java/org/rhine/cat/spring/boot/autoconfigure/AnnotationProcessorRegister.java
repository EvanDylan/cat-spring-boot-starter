package org.rhine.cat.spring.boot.autoconfigure;


import org.rhine.cat.spring.boot.annotation.CatTransactionAnnotationProcessor;
import org.rhine.cat.spring.boot.annotation.MetricForCountAnnotationProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class AnnotationProcessorRegister implements ImportBeanDefinitionRegistrar {

    private final BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
       register(MetricForCountAnnotationProcessor.class, registry);
       register(CatTransactionAnnotationProcessor.class, registry);
    }

    private void register(Class<?> beanClass, BeanDefinitionRegistry registry) {
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
        String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
        registry.registerBeanDefinition(beanName, beanDefinition);
    }
}
