package org.rhine.cat.spring.boot.support;

import org.rhine.cat.spring.boot.annotation.EnvEnum;
import org.rhine.cat.spring.boot.support.spi.EnvResolver;
import org.springframework.core.env.Environment;

import java.util.Iterator;
import java.util.ServiceLoader;

public class EnvResolverSupport {

    public static EnvEnum resolve(Environment environment) {
        Iterator<EnvResolver> iterator = ServiceLoader.load(EnvResolver.class).iterator();
        while (iterator.hasNext()) {
            EnvResolver envResolver = iterator.next();
            EnvEnum envEnum = envResolver.getCurrentEnv(environment);
            if (envEnum != null) {
                return envEnum;
            }
        }
        return null;
    }

}
