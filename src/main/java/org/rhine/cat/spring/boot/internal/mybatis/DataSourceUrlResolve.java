package org.rhine.cat.spring.boot.internal.mybatis;

import com.dianping.cat.util.StringUtils;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.ServiceLoader;

public class DataSourceUrlResolve {

    private static final String DEFAULT_URL = "jdbc:mysql://UUUUUKnown:3306/%s?useUnicode=true";

    public static String resolve(DataSource dataSource) {
        Iterator<DataSourcePoolUrlResolveProvider> iterator = ServiceLoader.load(DataSourcePoolUrlResolveProvider.class).iterator();
        while (iterator.hasNext()) {
            DataSourcePoolUrlResolveProvider adaptor = iterator.next();
            String url = adaptor.getDataSourceUrl(dataSource);
            if (StringUtils.isNotEmpty(url)) {
                return url;
            }
        }
        return DEFAULT_URL;
    }
}
