package org.rhine.cat.spring.boot.support;

import com.dianping.cat.util.StringUtils;
import org.rhine.cat.spring.boot.support.spi.DataSourcePoolUrlResolver;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.ServiceLoader;

public class DataSourceUrlResolverSupport {

    private static final String DEFAULT_URL = "jdbc:mysql://UUUUUKnown:3306/%s?useUnicode=true";

    public static String resolve(DataSource dataSource) {
        Iterator<DataSourcePoolUrlResolver> iterator = ServiceLoader.load(DataSourcePoolUrlResolver.class).iterator();
        while (iterator.hasNext()) {
            DataSourcePoolUrlResolver adaptor = iterator.next();
            String url = adaptor.getDataSourceUrl(dataSource);
            if (StringUtils.isNotEmpty(url)) {
                return url;
            }
        }
        return DEFAULT_URL;
    }
}
