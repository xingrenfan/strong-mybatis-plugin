package org.strong.plugin.mybatis.secure.executor;

import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * 说明: 加解密基础算法
 *
 * @author: Glendon.Li
 * @date: 2023-03-11 13:11
 * @version: V1.0.0
 **/
class BaseSecureExecutor {

    /**
     * 做解密
     *
     * @param crypto     加密
     * @param ciphertext 密文
     * @return {@link String}
     */
    protected String doDecrypt(SymmetricCrypto crypto, String ciphertext) {
        return crypto.decryptStr(ciphertext);
    }

    /**
     * 做base64加密
     *
     * @param crypto 加密
     * @param text   文本
     * @return {@link String}
     */
    protected String doBase64Encrypt(SymmetricCrypto crypto, String text) {
        return crypto.encryptBase64(text);
    }

    /**
     * 做Hex（16进制）加密
     *
     * @param crypto 加密
     * @param text   文本
     * @return {@link String}
     */
    protected String doHexEncrypt(SymmetricCrypto crypto, String text) {
        return crypto.encryptHex(text);
    }
}
