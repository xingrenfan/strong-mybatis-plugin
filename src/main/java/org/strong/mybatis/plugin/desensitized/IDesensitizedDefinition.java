package org.strong.mybatis.plugin.desensitized;

/**
 * 说明: 脱敏执行器定义
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
public interface IDesensitizedDefinition {

    /**
     * 执行脱敏处理
     *
     * @param value    要脱敏的值
     * @param fillChar 填充的符号
     * @return {@link String}
     */
    String execute(String value, char fillChar);
}
