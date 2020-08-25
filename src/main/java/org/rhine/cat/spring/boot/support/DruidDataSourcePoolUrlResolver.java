package org.rhine.cat.spring.boot.support;

import com.alibaba.druid.pool.DruidDataSource;
import org.rhine.cat.spring.boot.support.spi.DataSourcePoolUrlResolver;

import javax.sql.DataSource;

public class DruidDataSourcePoolUrlResolver implements DataSourcePoolUrlResolver {

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
