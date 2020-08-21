package org.rhine.cat.spring.boot.internal.mybatis;

import javax.sql.DataSource;

public class TomcatDataSourcePoolUrlResolveProvider implements DataSourcePoolUrlResolveProvider {

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
