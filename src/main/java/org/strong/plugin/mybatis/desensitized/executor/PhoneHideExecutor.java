package org.strong.plugin.mybatis.desensitized.executor;

import cn.hutool.core.text.CharSequenceUtil;
import org.strong.plugin.mybatis.desensitized.IDesensitizedDefinition;

/**
 * 说明: 手机号脱敏
 * 【手机号码】前三位，后4位，其他隐藏，比如135****2210
 *
 * @author: Glendon.Li
 * @date: 2023-03-09 20:25
 * @version: V1.0.0
 **/
public class PhoneHideExecutor extends BaseHideExecutor implements IDesensitizedDefinition {
    @Override
    public String execute(String value, char fillChar) {
        if (returnSourceValue(value, fillChar)) {
            return value;
        }
        return CharSequenceUtil.replace(value, 3, 7, fillChar);
    }
}
