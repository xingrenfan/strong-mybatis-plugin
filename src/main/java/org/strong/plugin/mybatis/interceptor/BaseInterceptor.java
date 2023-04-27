package org.strong.plugin.mybatis.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 说明: 基础拦截器服务
 *
 * @author: Glendon.Li
 * @date: 2023-03-09 14:45
 * @version: V1.0.0
 **/
public abstract class BaseInterceptor {
    /**
     * 聚合父类属性字段
     *
     * @param oClass 操作类
     * @param fields 字段
     * @return {@link List}<{@link Field}>
     */
    protected List<Field> mergeField(Class<?> oClass, List<Field> fields) {
        if (Objects.isNull(fields)) {
            fields = new ArrayList<>();
        }
        // 父类
        Class<?> superclass = oClass.getSuperclass();
        if (Objects.nonNull(superclass) && !superclass.equals(Object.class) && superclass.getDeclaredFields().length > 0) {
            // 递归处理父类属性字段
            mergeField(superclass, fields);
        }
        for (Field declaredField : oClass.getDeclaredFields()) {
            int modifiers = declaredField.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isVolatile(modifiers) || Modifier.isSynchronized(modifiers)) {
                continue;
            }
            fields.add(declaredField);
        }
        return fields;
    }

    /**
     * 结束处理过滤器
     *
     * @param object 判断对象
     * @return
     */
    protected boolean isEndFilter(Object object) {
        return Objects.isNull(object) || object instanceof CharSequence || object instanceof Number || object instanceof Collection || object instanceof Date || object instanceof ChronoLocalDate;
    }

    /**
     * 获取对象中字段的属性值
     *
     * @param field  字段
     * @param object 操作对象
     * @return {@link Object}
     * @throws IllegalAccessException 非法访问异常
     */
    protected Object getFieldValue(Field field, Object object) throws IllegalAccessException {
        // 记录原属性可访问标记
        boolean accessible = field.isAccessible();
        // 手动指定可访问
        field.setAccessible(true);
        // 获取对象值
        Object value = field.get(object);
        // 恢复访问标记
        field.setAccessible(accessible);
        return value;
    }

    /**
     * 设置字段值
     *
     * @param field  字段
     * @param object 操作对象
     * @param value  填充值
     * @throws IllegalAccessException 非法访问异常
     */
    protected void setFieldValue(Field field, Object object, Object value) throws IllegalAccessException {
        // 记录原属性可访问标记
        boolean accessible = field.isAccessible();
        // 手动指定可访问
        field.setAccessible(true);
        // 写字段值
        field.set(object, value);
        // 恢复访问标记
        field.setAccessible(accessible);
    }

}
