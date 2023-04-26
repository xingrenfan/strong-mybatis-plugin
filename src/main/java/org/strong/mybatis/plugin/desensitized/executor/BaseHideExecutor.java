package org.strong.mybatis.plugin.desensitized.executor;

import java.util.Objects;

/**
 * 说明: 执行器基础方法
 *
 * @author: Glendon.Li
 * @date: 2023-03-09 11:21
 * @version: V1.0.0
 **/
public class BaseHideExecutor {
    /**
     * 返回源值
     *
     * @param value     值
     * @param fillValue 填值
     * @return {@link Boolean}
     */
    protected Boolean returnSourceValue(String value, char fillValue) {
        return Objects.isNull(value) || value.length() == 0 || Objects.isNull(fillValue);
    }
}