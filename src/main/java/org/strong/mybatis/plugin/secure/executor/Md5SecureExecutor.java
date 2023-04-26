package org.strong.mybatis.plugin.secure.executor;


import cn.hutool.crypto.SecureUtil;
import org.strong.mybatis.plugin.secure.ISecureDefinition;

/**
 * 说明: MD5摘要
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
public class Md5SecureExecutor implements ISecureDefinition {

    @Override
    public String encrypt(String value, String key) throws Exception {
        return SecureUtil.md5(value);
    }

    @Override
    public String decrypt(String value, String key) {
        // MD5不支持解密，原样返回
        return value;
    }

}
