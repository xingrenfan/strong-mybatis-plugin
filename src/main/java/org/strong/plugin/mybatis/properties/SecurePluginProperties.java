package org.strong.plugin.mybatis.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.strong.plugin.mybatis.exception.SecurePluginException;
import org.strong.plugin.mybatis.secure.ISecureDefinition;
import org.strong.plugin.mybatis.secure.executor.AesBase64SecureExecutor;

import java.util.Objects;


/**
 * 说明: 加密配置
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
@Configuration
@ConfigurationProperties(prefix = "plugin.mybatis")
public class SecurePluginProperties {

    /**
     * 数据库数据加密配置
     */
    private DatabaseSecure secure;

    public DatabaseSecure getSecure() {
        if (Objects.isNull(secure)) {
            return new DatabaseSecure();
        }
        return secure;
    }

    public void setSecure(DatabaseSecure secure) {
        this.secure = secure;
    }

    public static class DatabaseSecure {
        /**
         * 日志记录器
         */
        private final Logger logger = LoggerFactory.getLogger(DatabaseSecure.class);

        /**
         * 加密密钥
         */
        private String cryptoKey;

        private String defaultExecutor;

        public String getCryptoKey() {
            return cryptoKey;
        }

        public void setCryptoKey(String cryptoKey) {
            this.cryptoKey = cryptoKey;
        }

        public Class<? extends ISecureDefinition> getDefaultExecutor() {
            if (Objects.isNull(defaultExecutor)) {
                // 默认使用AES
                return AesBase64SecureExecutor.class;
            }
            try {
                return (Class<? extends ISecureDefinition>) Class.forName(defaultExecutor);
            } catch (ClassNotFoundException e) {
                logger.error("The encryption and decryption executor to load failed.", e);
                throw new SecurePluginException("加解密执行器加载失败");
            }
        }

        public void setDefaultExecutor(String defaultExecutor) {
            this.defaultExecutor = defaultExecutor;
        }
    }

}
