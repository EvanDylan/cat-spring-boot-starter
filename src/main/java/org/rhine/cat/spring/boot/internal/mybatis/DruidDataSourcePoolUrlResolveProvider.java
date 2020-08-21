package org.rhine.cat.spring.boot.internal.mybatis;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

public class DruidDataSourcePoolUrlResolveProvider implements DataSourcePoolUrlResolveProvider {

    private static final String CLASS_NAME = "com.alibaba.druid.pool.DruidDataSource";

    private static boolean SUPPORT = false;

    static {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(CLASS_NAME);
            SUPPORT = true;
        } catch (ClassNotFoundException e) {
            SUPPORT = false;
        }
    }

    @Override
    public String getDataSourceUrl(DataSource dataSource) {
        if (SUPPORT && dataSource instanceof DruidDataSource) {
            return ((DruidDataSource) dataSource).getUrl();
        }
        return null;
    }

}
