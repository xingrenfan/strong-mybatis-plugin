package org.strong.mybatis.plugin.desensitized.executor;

import cn.hutool.core.text.CharSequenceUtil;
import org.strong.mybatis.plugin.desensitized.IDesensitizedDefinition;

/**
 * 说明: 汽车拍照
 * 陕A12345D -》 陕A1****D
 *
 * @author: Glendon.Li
 * @date: 2023-03-09 20:35
 * @version: V1.0.0
 **/
public class CarLicenseHideExecutor extends BaseHideExecutor implements IDesensitizedDefinition {
    @Override
    public String execute(String value, char fillChar) {
        if (returnSourceValue(value, fillChar)) {
            return value;
        }
        return CharSequenceUtil.replace(value, 3, value.length() - 1, fillChar);
    }
}
