package org.strong.mybatis.plugin.annotation;

import org.strong.mybatis.plugin.SecurePluginAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明: 启用安全插件
 *
 * @author: Glendon.Li
 * @date: 2022/07/05 09:19
 * @version: V1.0.0
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SecurePluginAutoConfiguration.class)
@Documented
public @interface EnableSecurePlugin {
}
