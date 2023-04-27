package org.strong.plugin.mybatis.interceptor;


import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strong.plugin.mybatis.secure.ISecureDefinition;
import org.strong.plugin.mybatis.secure.executor.DefaultNullExecutor;
import org.strong.plugin.mybatis.annotation.FieldSecure;
import org.strong.plugin.mybatis.properties.SecurePluginProperties;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 说明: Mybatis拦截器 - 更新数据恢复
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        }
)
public class SecureResetInterceptor extends BaseInterceptor implements Interceptor {
    /**
     * 日志记录器
     */
    private final Logger logger = LoggerFactory.getLogger(SecureResetInterceptor.class.getName());

    @Resource
    private SecurePluginProperties securePluginProperties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 执行结果
        Object result = invocation.proceed();
        Object[] resultObject = invocation.getArgs();
        if (Objects.nonNull(resultObject) && Objects.nonNull(resultObject[1])) {
            // 恢复更新时加密的数据
            resetModifyData(resultObject[1]);
        }
        return result;
    }

    /**
     * 重新修改数据
     *
     * @param resultObject 结果对象
     */
    private void resetModifyData(Object resultObject) throws Exception {
        if (resultObject instanceof Collection) {
            Collection cValues = (Collection) resultObject;
            for (Object cValue : cValues) {
                resetModifyData(cValue);
            }
        } else {
            // 解密
            secureHandler(resultObject, resultObject.getClass());
        }
    }


    /**
     * 安全处理程序
     *
     * @param object 操作对象
     * @param oClass 对象类型
     * @throws Exception 异常
     */
    private void secureHandler(Object object, Class<?> oClass) throws Exception {
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
                    stringDecryptHandler(declaredField, object, value, annotation);
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
                    secureHandler(oneObject, oneObject.getClass());
                }
            } else {
                // 其他类型递归处理
                secureHandler(value, value.getClass());
            }
        }
    }

    /**
     * 字符串解密处理程序
     *
     * @param declaredField 声明字段
     * @param object        操作对象
     * @param value         原始值
     * @param annotation    注释
     * @throws Exception 异常
     */
    private void stringDecryptHandler(Field declaredField, Object object, Object value, FieldSecure annotation) throws Exception {
        // 字段脱敏注释
        if (Objects.nonNull(annotation)) {
            // 脱敏执行器
            Class<? extends ISecureDefinition> desensitize = DefaultNullExecutor.class.equals(annotation.executor()) ? securePluginProperties.getSecure().getDefaultExecutor() : annotation.executor();
            ISecureDefinition secureDefinition = desensitize.newInstance();
            logger.debug("secure execute source value:{}", value);
            Object secureValue = value;
            // 更新加密
            secureValue = secureDefinition.decrypt(
                    Objects.toString(value, ""),
                    // 密钥如果为空使用配置中密钥
                    StrUtil.isBlank(annotation.cryptoKey()) ? securePluginProperties.getSecure().getCryptoKey() : annotation.cryptoKey()
            );
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
