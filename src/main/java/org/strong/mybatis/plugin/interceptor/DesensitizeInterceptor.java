package org.strong.mybatis.plugin.interceptor;


import org.strong.mybatis.plugin.annotation.FieldDesensitize;
import org.strong.mybatis.plugin.desensitized.IDesensitizedDefinition;
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
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 说明: Mybatis脱敏拦截器
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
@Component
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class DesensitizeInterceptor extends BaseInterceptor implements Interceptor {
    /**
     * 日志记录器
     */
    private final Logger logger = LoggerFactory.getLogger(DesensitizeInterceptor.class.getName());

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取注释参数
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        Executor executor = (Executor) invocation.getTarget();

        CacheKey cacheKey;
        BoundSql boundSql;
        if (args.length == 4) {
            //4 个参数时
            boundSql = mappedStatement.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
        // 结果处理程序
        ResultHandler<Object> resultHandler = (ResultHandler) args[3];
        List<Object> resultList = executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        for (Object object : resultList) {
            if (Objects.isNull(object)) {
                // 字段空 - 跳过
                continue;
            }
            // 非空过滤并且脱敏字段信息
            filterDesensitized(object, object.getClass());
        }
        return resultList;
    }

    /**
     * 过滤并且脱敏信息
     *
     * @param object 对象
     * @param oClass o类
     * @throws IllegalAccessException 非法访问异常
     * @throws InstantiationException 实例化异常
     */
    private void filterDesensitized(Object object, Class<?> oClass) throws IllegalAccessException, InstantiationException {
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
                FieldDesensitize annotation = declaredField.getAnnotation(FieldDesensitize.class);
                if (Objects.nonNull(annotation)) {
                    // 需要脱敏字段
                    fieldValueDesensitized(declaredField, object, value, annotation);
                }
            } else if (value instanceof Collection) {
                Collection collectionValue = (Collection) value;
                // 循环处理集合
                for (Object oneObject : collectionValue) {
                    if (isEndFilter(oneObject)) {
                        //默认集合内类型一致
                        break;
                    }
                    // 递归处理
                    filterDesensitized(oneObject, oneObject.getClass());
                }
            } else {
                // 其他类型递归处理
                filterDesensitized(value, value.getClass());
            }
        }

    }

    /**
     * 字段值不敏感
     * 字段值脱敏
     *
     * @param field      字段(属性)
     * @param object     操作的对象
     * @param value      字段值
     * @param annotation 注释对象
     * @throws IllegalAccessException 非法访问异常
     * @throws InstantiationException 实例化异常
     */
    private void fieldValueDesensitized(Field field, Object object, Object value, FieldDesensitize annotation) throws IllegalAccessException, InstantiationException {
        // 字段脱敏注释
        if (Objects.nonNull(annotation)) {
            // 脱敏执行器
            Class<? extends IDesensitizedDefinition> desensitize = annotation.executor();
            IDesensitizedDefinition iDesensitizedDefinition = desensitize.newInstance();
            logger.debug("desensitized source value:{}", value);
            String desensitizedValue = iDesensitizedDefinition.execute(Objects.toString(value, ""), annotation.fillChar());
            logger.debug("desensitized end value:{}", desensitizedValue);
            // 重写脱敏之后的值
            setFieldValue(field, object, desensitizedValue);
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
