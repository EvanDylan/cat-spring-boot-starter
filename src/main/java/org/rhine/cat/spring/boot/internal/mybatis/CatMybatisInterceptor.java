package org.rhine.cat.spring.boot.internal.mybatis;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.rhine.cat.spring.boot.common.CatConstants;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.rhine.cat.spring.boot.support.DataSourceUrlResolverSupport;

import javax.sql.DataSource;
import java.text.DateFormat;
import java.util.*;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})})
public class CatMybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //begin cat transaction
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String methodName = this.getMethodName(mappedStatement);
        Transaction t = Cat.newTransaction(CatConstants.TYPE_SQL, methodName);
        String dataSourceUrl = getSQLDatabaseUrlByStatement(mappedStatement);
        Cat.logEvent("SQL.Database", dataSourceUrl);
        Cat.logEvent("SQL.Method", mappedStatement.getSqlCommandType().name().toLowerCase(),
                Message.SUCCESS, getSql(invocation, mappedStatement));
        try {
            Object returnValue = invocation.proceed();
            t.setStatus(Transaction.SUCCESS);
            return returnValue;
        } catch (Throwable e) {
            Cat.logError(e);
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    private String getSQLDatabaseUrlByStatement(MappedStatement mappedStatement) {
        Configuration configuration = mappedStatement.getConfiguration();
        Environment environment = configuration.getEnvironment();
        DataSource dataSource = environment.getDataSource();
        return DataSourceUrlResolverSupport.resolve(dataSource);
    }

    private String getMethodName(MappedStatement mappedStatement) {
        String[] strArr = mappedStatement.getId().split("\\.");
        return strArr[strArr.length - 2] + "." + strArr[strArr.length - 1];
    }

    private String getSql(Invocation invocation, MappedStatement mappedStatement) {
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        return showSql(configuration, boundSql);
    }

    private static String getParameterValue(Object obj) {
        StringBuilder retStringBuilder = new StringBuilder();
        if (obj instanceof String) {
            retStringBuilder.append("'").append(obj).append("'");
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            retStringBuilder.append("'").append(formatter.format(new Date())).append("'");
        } else {
            retStringBuilder.append("'").append(obj == null ? "" : obj).append("'");
        }
        return retStringBuilder.toString();
    }

    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        StringBuilder sqlBuilder = new StringBuilder(sql);
        if (parameterMappings.size() > 0 && parameterObject != null) {
            int start = sqlBuilder.indexOf("?");
            int end = start + 1;

            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sqlBuilder.replace(start, end, getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sqlBuilder.replace(start, end, getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sqlBuilder.replace(start, end, getParameterValue(obj));
                    }

                    start = sqlBuilder.indexOf("?");
                    end = start + 1;
                }
            }
        }
        return sqlBuilder.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}