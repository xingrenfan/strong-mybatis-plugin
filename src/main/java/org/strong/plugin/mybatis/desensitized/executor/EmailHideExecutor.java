package org.strong.plugin.mybatis.desensitized.executor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import org.strong.plugin.mybatis.desensitized.IDesensitizedDefinition;

/**
 * 说明: 邮箱脱敏
 * 【电子邮箱】邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，比如：d**@126.com
 *
 * @author: Glendon.Li
 * @date: 2023-03-09 20:25
 * @version: V1.0.0
 **/
public class EmailHideExecutor extends BaseHideExecutor implements IDesensitizedDefinition {
    @Override
    public String execute(String value, char fillChar) {
        if (returnSourceValue(value, fillChar)) {
            return value;
        }
        return CharSequenceUtil.replace(value, 1, StrUtil.indexOf(value, '@'), fillChar);
    }
}
