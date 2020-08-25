package org.rhine.cat.spring.boot.support;

import org.rhine.cat.spring.boot.annotation.EnvEnum;
import org.rhine.cat.spring.boot.support.spi.EnvResolver;
import org.springframework.core.env.Environment;

public class DefaultProfileEnvResolver implements EnvResolver {

    @Override
    public EnvEnum getCurrentEnv(Environment environment) {
        if (environment != null) {
            String[] activeProfiles = environment.getActiveProfiles();
            if (activeProfiles != null && activeProfiles.length > 0) {
                String activeProfile = activeProfiles[0];
                return EnvEnum.parseByValue(activeProfile);
            }
        }
        return null;
    }
}
