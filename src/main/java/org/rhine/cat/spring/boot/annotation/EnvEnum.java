package org.rhine.cat.spring.boot.annotation;

/**
 * 环境枚举
 */
public enum EnvEnum {

    MIT("mit", "50"),

    TEST("test", "20"),

    UAT("uat", "30"),

    PROD("prod", "40");

    String name;

    String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    EnvEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static EnvEnum parseByValue(String value) {
        for (EnvEnum envEnum : EnvEnum.values()) {
            if (envEnum.value.equalsIgnoreCase(value)) {
                return envEnum;
            }
        }
        return null;
    }
}
