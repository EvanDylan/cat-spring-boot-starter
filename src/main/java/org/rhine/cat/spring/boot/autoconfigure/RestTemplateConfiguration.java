package org.rhine.cat.spring.boot.autoconfigure;

import org.rhine.cat.spring.boot.internal.http.RestTemplateTraceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Configuration
@ConditionalOnBean(RestTemplate.class)
public class RestTemplateConfiguration {

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        restTemplate.setInterceptors(Collections.singletonList(new RestTemplateTraceInterceptor()));
    }
}
