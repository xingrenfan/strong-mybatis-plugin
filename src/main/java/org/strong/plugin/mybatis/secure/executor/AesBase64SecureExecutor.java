package org.strong.plugin.mybatis.secure.executor;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import org.strong.plugin.mybatis.secure.ISecureDefinition;

import java.nio.charset.Charset;

/**
 * 说明: AES BASE64 加解密实现
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
public class AesBase64SecureExecutor extends BaseSecureExecutor implements ISecureDefinition {

    @Override
    public String encrypt(String value, String key) throws Exception {
        return doBase64Encrypt(
                SecureUtil.aes(SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), key.getBytes(Charset.defaultCharset())).getEncoded()),
                value
        );
    }

    @Override
    public String decrypt(String value, String key) {
        return doDecrypt(
                SecureUtil.aes(SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), key.getBytes(Charset.defaultCharset())).getEncoded()),
                value
        );
    }

}
