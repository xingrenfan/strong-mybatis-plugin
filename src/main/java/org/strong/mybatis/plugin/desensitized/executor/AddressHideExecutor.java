package org.strong.mybatis.plugin.desensitized.executor;

import cn.hutool.core.text.CharSequenceUtil;
import org.strong.mybatis.plugin.desensitized.IDesensitizedDefinition;

/**
 * 说明: 地址脱敏
 * 北京市海淀区****
 *
 * @author: Glendon.Li
 * @date: 2023-03-09 20:25
 * @version: V1.0.0
 **/
public class AddressHideExecutor extends BaseHideExecutor implements IDesensitizedDefinition {
    @Override
    public String execute(String value, char fillChar) {
        if (returnSourceValue(value, fillChar)) {
            return value;
        }
        return CharSequenceUtil.replace(value, 6, value.length(), fillChar);
    }
}
