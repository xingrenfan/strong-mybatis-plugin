package org.strong.mybatis.plugin.secure.executor;

import org.strong.mybatis.plugin.secure.ISecureDefinition;

/**
 * 说明: 默认空执行器
 *
 * @author: Glendon.Li
 * @date: 2023-04-26 16:00
 * @version: V1.0.0
 **/
public class DefaultNullExecutor implements ISecureDefinition {
    @Override
    public String encrypt(String value, String key) throws Exception {
        return null;
    }

    @Override
    public String decrypt(String value, String key) throws Exception {
        return null;
    }
}
