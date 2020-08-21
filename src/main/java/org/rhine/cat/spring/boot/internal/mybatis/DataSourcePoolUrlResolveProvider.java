package org.rhine.cat.spring.boot.internal.mybatis;

import javax.sql.DataSource;

/**
 * 适配不同数据源
 */
public interface DataSourcePoolUrlResolveProvider {

    String getDataSourceUrl(DataSource dataSource);

}
