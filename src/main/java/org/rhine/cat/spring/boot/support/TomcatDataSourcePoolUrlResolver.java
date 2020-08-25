package org.rhine.cat.spring.boot.support;

import org.rhine.cat.spring.boot.support.spi.DataSourcePoolUrlResolver;

import javax.sql.DataSource;

public class TomcatDataSourcePoolUrlResolver implements DataSourcePoolUrlResolver {

    private static final String CLASS_NAME = "org.apache.tomcat.jdbc.pool.DataSource";

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
        if (SUPPORT && dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            return ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getUrl();
        }
        return null;
    }
}
