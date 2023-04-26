package org.strong.mybatis.plugin.annotation;

import org.strong.mybatis.plugin.desensitized.IDesensitizedDefinition;
import org.strong.mybatis.plugin.desensitized.executor.DefaultHideHideExecutor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明: 字段脱敏注解
 *
 * @author: Glendon.Li
 * @date: 2022/07/04
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FieldDesensitize {

    /**
     * 填充值 默认使用"*"填充
     *
     * @return
     */
    char fillChar() default '*';

    /**
     * 脱敏执行器
     *
     * @return {@link Class}<{@link ?} {@link extends} {@link IDesensitizedDefinition}>
     */
    Class<? extends IDesensitizedDefinition> executor() default DefaultHideHideExecutor.class;
}
