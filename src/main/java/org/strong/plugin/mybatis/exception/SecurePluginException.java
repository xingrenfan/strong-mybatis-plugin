package org.strong.plugin.mybatis.exception;

/**
 * 说明: 加解密异常
 *
 * @author: Glendon.Li
 * @date: 2022/07/05 16:51
 * @version: V1.0.0
 **/
public class SecurePluginException extends RuntimeException {
    private static final long serialVersionUID = 3880206998166270765L;

    public SecurePluginException() {
    }

    public SecurePluginException(String message) {
        super(message);
    }

    public SecurePluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
