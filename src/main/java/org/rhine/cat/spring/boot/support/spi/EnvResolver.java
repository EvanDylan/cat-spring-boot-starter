package org.rhine.cat.spring.boot.support.spi;

import org.rhine.cat.spring.boot.annotation.EnvEnum;
import org.springframework.core.env.Environment;

/**
 * 解析当前环境
 */
public interface EnvResolver {

    EnvEnum getCurrentEnv(Environment environment);

}
