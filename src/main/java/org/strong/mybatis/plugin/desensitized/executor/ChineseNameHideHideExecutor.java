package org.strong.mybatis.plugin.desensitized.executor;

import cn.hutool.core.text.CharSequenceUtil;
import org.strong.mybatis.plugin.desensitized.IDesensitizedDefinition;

/**
 * 说明: 中文名脱敏执行器
 * 李**
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
public class ChineseNameHideHideExecutor extends BaseHideExecutor implements IDesensitizedDefinition {
    @Override
    public String execute(String value, char fillChar) {
        if (returnSourceValue(value, fillChar)) {
            return value;
        }
        return CharSequenceUtil.replace(value, 1, value.length(), fillChar);
    }
}
