package org.rhine.cat.spring.boot.annotation;

/**
 * 环境枚举
 */
public enum EnvEnum {

    MIT("mit"),

    TEST("test"),

    UAT("uat"),

    PROD("prod");

    String name;

    EnvEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static EnvEnum parseByValue(String name) {
        for (EnvEnum envEnum : EnvEnum.values()) {
            if (envEnum.name.equalsIgnoreCase(name)) {
                return envEnum;
            }
        }
        return null;
    }
}
