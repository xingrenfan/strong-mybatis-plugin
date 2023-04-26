package org.strong.mybatis.plugin.utils;

import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.DESede;
import cn.hutool.crypto.symmetric.RC4;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

class SecureExecutorTest {

    private String key = "mykeykeymykeykey";

    @Test
    void aesTest() {
        String name = "Glendon";
        AES aes = SecureUtil.aes(SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), key.getBytes(Charset.defaultCharset())).getEncoded());
        String result = aes.encryptHex(name);
        System.out.println(result);
        System.out.println(aes.encryptHex(name));
        System.out.println(aes.encryptHex(name));
        System.out.println(aes.decryptStr(result));
    }

    @Test
    void desTest() {
        String name = "Glendon";
        DES aes = SecureUtil.des(SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue(), key.getBytes(Charset.defaultCharset())).getEncoded());
        String result = aes.encryptHex(name);
        System.out.println(result);
        System.out.println(aes.encryptHex(name));
        System.out.println(aes.encryptHex(name));
        System.out.println(aes.decryptStr(result));
    }

    @Test
    void md5Test() {
        SecureUtil.md5("Glendon");
    }

    @Test
    void desedeTest() {
        String name = "Glendon";
        key = "mykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykeymykeykey";
        DESede desede = SecureUtil.desede(SecureUtil.generateKey(SymmetricAlgorithm.DESede.getValue(), key.getBytes(Charset.defaultCharset())).getEncoded());
        String result = desede.encryptHex(name);
        System.out.println(result);
        System.out.println(desede.decryptStr(result));
    }

    @Test
    void rc4Test() {
        // RC4
        String name = "Glendon";
        RC4 rc4 = SecureUtil.rc4(key);
        String result = rc4.encryptHex(name);
        System.out.println(result);
        System.out.println(rc4.decrypt(result));
    }
}