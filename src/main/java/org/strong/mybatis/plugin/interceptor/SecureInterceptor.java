package org.strong.mybatis.plugin.interceptor;


import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strong.mybatis.plugin.secure.ISecureDefinition;
import org.strong.mybatis.plugin.secure.executor.DefaultNullExecutor;
import org.strong.mybatis.plugin.annotation.FieldSecure;
import org.strong.mybatis.plugin.properties.SecurePluginProperties;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 说明: Mybatis拦截器 - 加解密
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        }
)
public class SecureInterceptor extends BaseInterceptor implements Interceptor {
    /**
     * 日志记录器
     */
    private final Logger logger = LoggerFactory.getLogger(SecureInterceptor.class.getName());

    /**
     * 更新方法
     */
    private static final String UPDATE_METHOD = "update";

    /**
     * 查询方法
     */
    private static final String QUERY_METHOD = "query";

    @Resource
    private SecurePluginProperties securePluginProperties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 执行方法 只针对update和query做处理
        Method method = invocation.getMethod();
        switch (method.getName()) {
            case UPDATE_METHOD:
                return updateEncryptHandle(invocation, method.getName());
            case QUERY_METHOD:
                return selectDecryptHandle(invocation, method.getName());
            default:
                return invocation.proceed();
        }

    }

    /**
     * 选择解密处理
     * 查询解密处理
     *
     * @param invocation 调用
     * @param methodName 执行方法
     * @return {@link Object}
     * @throws Throwable throwable
     */
    private Object selectDecryptHandle(Invocation invocation, String methodName) throws Throwable {
        // 获取注释参数
        Object[] args = invocation.getArgs();
        Object parameter = args[1];
        // 查询条件需要做加密处理,
        if (Objects.nonNull(parameter)) {
            secureHandler(parameter, parameter.getClass(), UPDATE_METHOD);
        }
        // 继续获取参数
        MappedStatement mappedStatement = (MappedStatement) args[0];
        RowBounds rowBounds = (RowBounds) args[2];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if (args.length == 4) {
            //4 个参数时
            boundSql = mappedStatement.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
        ResultHandler<Object> resultHandler = (ResultHandler) args[3];
        List<Object> resultList = executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        for (Object result : resultList) {
            // 查询结果循环处理
            secureValueHandler(result, result.getClass(), methodName);
        }
        return resultList;
    }

    /**
     * 更新加密处理
     *
     * @param invocation 调用
     * @param methodName 执行方法
     * @return {@link Object}
     * @throws Throwable throwable
     */
    private Object updateEncryptHandle(Invocation invocation, String methodName) throws Throwable {
        // 获取对象
        Object object = invocation.getArgs()[1];
        //处理对象数据
        if (object instanceof Map) {
            Map<Object, Object> paramMap = (Map) object;
            // 去重(确保相同数据只处理一次)并且循环处理
            for (Object paramValue : paramMap.entrySet().stream().map(Map.Entry::getValue).distinct().collect(Collectors.toList())) {
                if (Objects.nonNull(paramValue)) {
                    // 值加密处理
                    secureValueHandler(paramValue, paramValue.getClass(), methodName);
                }
            }
        } else {
            if (Objects.nonNull(object)) {
                // 值加密处理
                secureValueHandler(object, object.getClass(), methodName);
            }
        }
        return invocation.proceed();
    }

    /**
     * 递归处理值
     *
     * @param object     操作对象
     * @param oClass     对象类型
     * @param methodName 方法名称
     */
    private void secureValueHandler(Object object, Class<?> oClass, String methodName) throws Exception {
        if (object instanceof Collection) {
            Collection cValues = (Collection) object;
            for (Object cValue : cValues) {
                secureValueHandler(cValue, cValue.getClass(), methodName);
            }
        } else {
            secureHandler(object, oClass, methodName);
        }
    }

    /**
     * 安全处理程序
     *
     * @param object     操作对象
     * @param oClass     对象类型
     * @param methodName 方法
     * @throws Exception 异常
     */
    private void secureHandler(Object object, Class<?> oClass, String methodName) throws Exception {
        if (isEndFilter(object)) {
            //过滤 - 不做任何处理
            return;
        }
        // 聚合对象和父对象所有字段
        List<Field> fields = mergeField(oClass, null);
        for (Field declaredField : fields) {
            if (Modifier.isStatic(declaredField.getModifiers())) {
                //静态属性直接跳过
                continue;
            }
            Object value = getFieldValue(declaredField, object);
            if (Objects.isNull(value)) {
                continue;
            } else if (value instanceof Number) {
                continue;
            } else if (value instanceof String) {
                // 获取字段注解
                FieldSecure annotation = declaredField.getAnnotation(FieldSecure.class);
                if (Objects.nonNull(annotation)) {
                    // 加解密
                    stringSecureHandler(declaredField, object, value, annotation, methodName);
                }
            } else if (value instanceof Collection) {
                Collection collectionValue = (Collection) value;
                // 集合类型循环处理
                for (Object oneObject : collectionValue) {
                    if (isEndFilter(oneObject)) {
                        //过滤 - 不做任何处理
                        break;
                    }
                    // 递归处理
                    secureHandler(oneObject, oneObject.getClass(), methodName);
                }
            } else {
                // 其他类型递归处理
                secureHandler(value, value.getClass(), methodName);
            }
        }
    }

    /**
     * 字符串安全处理程序
     *
     * @param declaredField 声明字段
     * @param object        操作对象
     * @param value         原始值
     * @param annotation    注释
     * @param methodName    方法
     */
    private void stringSecureHandler(Field declaredField, Object object, Object value, FieldSecure annotation, String methodName) throws Exception {
        // 字段脱敏注释
        if (Objects.nonNull(annotation)) {
            // 脱敏执行器
            Class<? extends ISecureDefinition> desensitize = DefaultNullExecutor.class.equals(annotation.executor()) ? securePluginProperties.getSecure().getDefaultExecutor() : annotation.executor();
            ISecureDefinition secureDefinition = desensitize.newInstance();
            logger.debug("secure execute source value:{}", value);
            Object secureValue = value;
            if (UPDATE_METHOD.equals(methodName)) {
                // 更新加密
                secureValue = secureDefinition.encrypt(
                        Objects.toString(value, ""),
                        // 密钥如果为空使用配置中密钥
                        StrUtil.isBlank(annotation.cryptoKey()) ? securePluginProperties.getSecure().getCryptoKey() : annotation.cryptoKey()
                );
            } else if (QUERY_METHOD.equals(methodName)) {
                // 查询解密
                secureValue = secureDefinition.decrypt(
                        Objects.toString(value, ""),
                        // 密钥如果为空使用配置中密钥
                        StrUtil.isBlank(annotation.cryptoKey()) ? securePluginProperties.getSecure().getCryptoKey() : annotation.cryptoKey()
                );
            }
            logger.debug("secure execute end value:{}", secureValue);
            // 重写脱敏之后的值
            setFieldValue(declaredField, object, secureValue);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
