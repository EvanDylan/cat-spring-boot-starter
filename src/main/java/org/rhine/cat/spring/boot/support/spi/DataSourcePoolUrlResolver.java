package org.rhine.cat.spring.boot.support.spi;

import javax.sql.DataSource;

/**
 * 适配不同数据源
 */
public interface DataSourcePoolUrlResolver {

    String getDataSourceUrl(DataSource dataSource);

}
