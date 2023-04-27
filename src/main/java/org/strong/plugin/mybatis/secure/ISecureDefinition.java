package org.strong.plugin.mybatis.secure;

/**
 * 说明: 加密执行器
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
public interface ISecureDefinition {

    /**
     * 加密
     *
     * @param value 加密前的值
     * @param key   秘钥
     * @return 加密后的值
     * @throws Exception 异常
     */
    String encrypt(String value, String key) throws Exception;

    /**
     * 解密
     *
     * @param value 解密前的值
     * @param key   秘钥
     * @return 解密后的值
     * @throws Exception 异常
     */
    String decrypt(String value, String key) throws Exception;
}
