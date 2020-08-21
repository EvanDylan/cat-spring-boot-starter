package org.rhine.cat.spring.boot.autoconfigure;

import org.rhine.cat.spring.boot.internal.mybatis.CatMybatisInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
public class MybatisAutoConfiguration {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactories;

    @PostConstruct
    public void afterPropertiesSet() {
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactories) {
            if (sqlSessionFactory != null) {
               sqlSessionFactory.getConfiguration().addInterceptor(new CatMybatisInterceptor());
            }
        }
    }
}
