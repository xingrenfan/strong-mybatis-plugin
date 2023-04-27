package org.strong.plugin.mybatis.desensitized.executor;

import cn.hutool.core.text.CharSequenceUtil;
import org.strong.plugin.mybatis.desensitized.IDesensitizedDefinition;

/**
 * 说明: 密码脱敏
 * 【密码】密码的全部字符都用*代替，比如：******
 *
 * @author: Glendon.Li
 * @date: 2023-03-09 20:25
 * @version: V1.0.0
 **/
public class PasswordHideExecutor extends BaseHideExecutor implements IDesensitizedDefinition {
    @Override
    public String execute(String value, char fillChar) {
        if (returnSourceValue(value, fillChar)) {
            return value;
        }
        return CharSequenceUtil.replace(value, 0, value.length(), fillChar);
    }
}
