package org.strong.mybatis.plugin.desensitized.executor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import org.strong.mybatis.plugin.desensitized.IDesensitizedDefinition;

import java.util.regex.Pattern;

/**
 * 说明: 默认脱敏执行器
 *
 * @author: Glendon.Li
 * @date: 2022/07/05
 * @version: <p>
 * 1.0.0 Glendon.Li 2022/07/05
 * </p>
 **/
public class DefaultHideHideExecutor extends BaseHideExecutor implements IDesensitizedDefinition {
    /**
     * 手机号码匹配
     */
    public static final String PHONE_REG = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";
    /**
     * 邮箱email
     */
    public static final String EMAIL_REG = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /**
     * 银行卡卡号位数
     */
    public final static String BANK_CARD_NUMBER = "^\\d{16}|\\d{19}$";
    /**
     * 身份证号码位数限制
     */
    public final static String ID_CARD = "^\\d{15}|(\\d{17}[0-9,x,X])$";

    @Override
    public String execute(String value, char fillChar) {
        if (returnSourceValue(value, fillChar)) {
            return value;
        }
        if (Pattern.matches(PHONE_REG, value)) {
            // 【手机号码】前三位，后4位，其他隐藏，比如135****2210
            return CharSequenceUtil.replace(value, 3, 7, fillChar);
        } else if (Pattern.matches(EMAIL_REG, value)) {
            // 【电子邮箱】邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，比如：d**@126.com
            return CharSequenceUtil.replace(value, 1, StrUtil.indexOf(value, '@'), fillChar);
        } else if (Pattern.matches(BANK_CARD_NUMBER, value)) {
            // 银行卡号脱敏 eg: 1101 **** **** **** 3256
            return CharSequenceUtil.replace(value, 4, value.length() - 4, fillChar);
        } else if (Pattern.matches(ID_CARD, value)) {
            // 【身份证号】前3位 和后4位
            return CharSequenceUtil.replace(value, 3, value.length() - 4, fillChar);
        }
        // 最后1位
        return CharSequenceUtil.replace(value, 0, value.length() - 1, fillChar);
    }
}
