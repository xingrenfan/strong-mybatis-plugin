package org.strong.plugin.mybatis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.strong.plugin.mybatis.interceptor.DesensitizeInterceptor;
import org.strong.plugin.mybatis.interceptor.SecureInterceptor;
import org.strong.plugin.mybatis.interceptor.SecureResetInterceptor;
import org.strong.plugin.mybatis.properties.SecurePluginProperties;

/**
 * 说明: 插件开启时自动配置类
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
@Configuration
@EnableConfigurationProperties(SecurePluginProperties.class)
public class SecurePluginAutoConfiguration implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(SecurePluginAutoConfiguration.class);

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    @ConditionalOnMissingBean
    public SecureInterceptor cryptoInterceptor() {
        return new SecureInterceptor();
    }

    @Bean
    @Order
    @ConditionalOnMissingBean
    public SecureResetInterceptor cryptoResetInterceptor() {
        return new SecureResetInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public DesensitizeInterceptor desensitizeInterceptor() {
        return new DesensitizeInterceptor();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info(
                "MYBATIS PLUGIN INFORMATION：\n{}\n{}\n{}",
                "==================================================",
                "XingRenFan MYBATIS PLUGIN\n@copy License MIT 2022",
                "=================================================="
        );
    }
}
