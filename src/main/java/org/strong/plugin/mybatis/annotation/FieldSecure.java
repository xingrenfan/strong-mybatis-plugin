package org.strong.plugin.mybatis.annotation;


import org.strong.plugin.mybatis.secure.ISecureDefinition;
import org.strong.plugin.mybatis.secure.executor.DefaultNullExecutor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明: 字段加解密注解
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FieldSecure {

    /**
     * 加密密钥
     *
     * @return {@link String}
     */
    String cryptoKey() default "";

    /**
     * 加密解密执行器器 默认使用SEA加解密
     *
     * @return {@link Class}<{@link ?} {@link extends} {@link ISecureDefinition}>
     */
    Class<? extends ISecureDefinition> executor() default DefaultNullExecutor.class;

}
