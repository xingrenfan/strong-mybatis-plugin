package org.strong.plugin.mybatis.secure.executor;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.RC4;
import org.strong.plugin.mybatis.secure.ISecureDefinition;

/**
 * 说明: RC4(ARCFOUR)加解密实现
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
public class Rc4SecureExecutor implements ISecureDefinition {

    @Override
    public String encrypt(String value, String key) throws Exception {
        // RC4
        RC4 rc4 = SecureUtil.rc4(key);
        return rc4.encryptBase64(value);
    }

    @Override
    public String decrypt(String value, String key) {
        // RC4
        RC4 rc4 = SecureUtil.rc4(key);
        return rc4.decrypt(value);
    }

}
