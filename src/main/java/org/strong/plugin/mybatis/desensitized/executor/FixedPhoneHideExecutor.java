package org.strong.plugin.mybatis.desensitized.executor;

import cn.hutool.core.text.CharSequenceUtil;
import org.strong.plugin.mybatis.desensitized.IDesensitizedDefinition;

/**
 * 说明: 固话脱敏
 * 【固定电话】 前四位，后两位
 *
 * @author: Glendon.Li
 * @date: 2023-03-09 20:25
 * @version: V1.0.0
 **/
public class FixedPhoneHideExecutor extends BaseHideExecutor implements IDesensitizedDefinition {
    @Override
    public String execute(String value, char fillChar) {
        if (returnSourceValue(value, fillChar)) {
            return value;
        }
        return CharSequenceUtil.replace(value, 4, value.length() - 2, fillChar);
    }
}
