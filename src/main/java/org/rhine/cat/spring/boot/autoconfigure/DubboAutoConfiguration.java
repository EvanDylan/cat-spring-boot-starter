package org.rhine.cat.spring.boot.autoconfigure;

import com.alibaba.dubbo.config.AbstractConfig;
import org.rhine.cat.spring.boot.internal.dubbo.DubboCat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(AbstractConfig.class)
public class DubboAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        DubboCat.enable();
    }

}
