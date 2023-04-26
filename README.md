# strong-mybatis-plugin

## 介绍

基于mybatis对持久化数据CRUD操作时进行加密、解密、脱敏等。不影响DAO对象数据的正常使用。再也不必为加解密频发写Util了让代码自由的飞翔。

1. 数据写入、更新时完成加密操作。程序中对象依然保持原数据（未加密）
2. 查询字段带加密注解，自动对加密数据解密
3. 查询结果自动脱敏

### 依赖

+ [x] `springboot` 2.6.2
+ [x] `mybatis` 3.5.10
+ [x] `jdk` 1.8+
+ [x] `hutool-core` 5.8.15
+ [x] `hutool-crypto` 5.8.15

## 安装教程

### 使用说明

#### 添加依赖

```xml

<dependency>
    <groupId>cn.mybatis.plugin</groupId>
    <artifactId>mybatis-secure-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### 开启插件

```java
// 开启插件注解
@EnableSecurePlugin
@SpringBootApplication
public class CryptoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CryptoApplication.class, args);
    }
}
```

#### 代码中使用

##### 字段加解密

1. `@FieldSecure`注解放到要加密解密类的字段上，如：

```
    @FieldSecure
    private String password;

```

2. @FieldSecure 注解默认使用AES算法，如果想自定义秘钥有两种方式：

① 全局配置密钥yml文件(程序优先使用FieldSecure注解中配置的密钥、加解密执行器)

```
plugin:
  mybatis:
    secure:
      cryptoKey: mykeykeymykeykey
      defaultExecutor: org.strong.mybatis.plugin.secure.executor.AesHexSecureExecutor
```

② 自定义注解秘钥

```
    @FieldSecure(cryptoKey = "qwertyuiop")
    private String password;
```

*注意：自定义注解秘钥优先级高于全局秘钥*

3. 如果你想使用MD5加密(md5结果不会解密)

```
    @FieldSecure(cryptoKey = "qwertyuiop", executor = Md5SecureExecutor.class)
    private String password;
```

4. 内置加密执行器：

+ [x] `AES-Base64 AesBase64SecureExecutor.class`
+ [x] `AES-Hex AesHexSecureExecutor.class`
+ [x] `DES-Base64 DesBase64SecureExecutor.class`
+ [x] `DES-Hex DesHexSecureExecutor.class`
+ [x] `MD5 Md5SecureExecutor.class`
+ [x] `RC4 Rc4SecureExecutor.class`
+ [x] `DESEDE-Base64 DesedeBase64SecureExecutor.class`
+ [x] `DESEDE-Hex DesedeHexSecureExecutor.class`

5. 自定义加密、解密执行器只需要实现`ISecureDefinition`接口，再注解中指定即可

```
@Slf4j
public class MySecureExecutor implements ISecureDefinition {

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
```

##### 查询结果内容脱敏

1. `@FieldDesensitize`注解放到需要脱敏的字段上，如：

```
    @FieldDesensitize(fillChar = "*", executor = PhoneHideExecutor.class)
    private String phone;

```

2. 内置脱敏执行器：

+ [x] 地址脱敏 `AddressHideExecutor.class`
+ [x] 车牌照 `CarLicenseHideExecutor.class`
+ [x] 中文名 `ChineseNameHideHideExecutor.class`
+ [x] 固话 `FixedPhoneHideExecutor.class`
+ [x] 密码 `PasswordHideExecutor.class`
+ [x] 手机号 `PhoneHideExecutor.class`
+ [x] 邮箱 `EmailHideExecutor.class`
+ [x] 身份证 `IdCardHideExecutor.class`

3. 自定义脱敏执行器只需要实现`IDesensitizedDefinition`接口，再注解中指定即可

```
@Slf4j
public class EmailHideExecutor implements IDesensitizedDefinition {

    @Override
    public String execute(String value, String fillChar) {
        return DesensitizedHelper.hideBetween(value, 1, StrUtil.indexOf(value, '@'), fillChar);
    }
}
```

# 开源协议

本程序使用MIT开源协议，若需要使用或者修改请联系本人获取授权。
