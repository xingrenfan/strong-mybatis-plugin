package org.strong.plugin.mybatis.desensitized.executor;

import cn.hutool.core.text.CharSequenceUtil;
import org.strong.plugin.mybatis.desensitized.IDesensitizedDefinition;

/**
 * 说明: 身份证号脱敏
 * 【身份证号】前3位 和后4位
 *
 * @author: Glendon.Li
 * @date: 2023-03-09 20:25
 * @version: V1.0.0
 **/
public class IdCardHideExecutor extends BaseHideExecutor implements IDesensitizedDefinition {
    @Override
    public String execute(String value, char fillChar) {
        if (returnSourceValue(value, fillChar)) {
            return value;
        }
        return CharSequenceUtil.replace(value, 3, value.length() - 4, fillChar);
    }
}
