package org.rhine.cat.spring.boot.annotation;

import com.dianping.cat.Cat;
import org.rhine.cat.spring.boot.support.EnvResolverSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;

public class TraceConfigurationImportSelector implements ImportSelector, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(TraceConfigurationImportSelector.class);

    private Environment environment;

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        MultiValueMap<String, Object> attributes = annotationMetadata.getAllAnnotationAttributes(
                EnableCat.class.getName());
        EnvEnum[] configEnvEnums = attributes == null ? null
                : (EnvEnum[]) attributes.getFirst("env");
        EnvEnum currentEnv = EnvResolverSupport.resolve(environment);
        logger.info("当前环境为{}", currentEnv == null ? "unknown" : currentEnv.getName());
        // 如果没有配置或者读取不到当前的环境，默认启用
        boolean disable = true;
        if (configEnvEnums == null || configEnvEnums.length == 0 || currentEnv == null) {
            logger.info("默认启用Cat调用链追踪");
            disable = false;
        }
        // 根据配置决定是否启用
        if (configEnvEnums != null && configEnvEnums.length > 0 && currentEnv != null) {
            logger.info("仅在{}环境启用Cat调用链追踪", join(configEnvEnums, ","));
            for (EnvEnum configEnvEnum : configEnvEnums) {
                if (configEnvEnum.getName().equalsIgnoreCase(currentEnv.getName())) {
                    disable = false;
                    break;
                }
            }
        }
        if (disable) {
            Cat.disable();
        } else {
            Cat.enable();
        }
        return new String[0];
    }

    private String join(EnvEnum[] configEnvEnums, String separator) {
        boolean first = true;
        StringBuilder sb = new StringBuilder(128);

        for (EnvEnum configEnvEnum : configEnvEnums) {
            if (first) {
                sb.append(configEnvEnum.getName());
                first = false;
            } else {
                sb.append(separator).append(configEnvEnum.getName());
            }
        }
        return sb.toString();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
