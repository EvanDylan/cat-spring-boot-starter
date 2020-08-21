package org.rhine.cat.spring.boot.annotation;

import com.dianping.cat.Cat;
import org.rhine.cat.spring.boot.properties.ApplicationProperties;
import org.rhine.cat.spring.boot.properties.CatProperties;
import org.rhine.cat.spring.boot.autoconfigure.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
        DubboAutoConfiguration.class,
        ServletFilterConfiguration.class,
        MybatisAutoConfiguration.class,
        DefaultCatClientProvider.class,
        ApplicationProperties.class,
        CatProperties.class,
        AnnotationProcessorRegister.class
})
@Configuration
public class CatAutoConfiguration implements InitializingBean {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private CatProperties catProperties;


    @Override
    public void afterPropertiesSet() {
        DefaultCatClientProvider.setApplicationProperties(applicationProperties);
        DefaultCatClientProvider.setCatProperties(catProperties);
        Cat.initialize();
    }

}
