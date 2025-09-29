package com.simplespring.core.convert;

import com.simplespring.core.util.ClassUtils;
import com.simplespring.core.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认类型转换器实现
 * 支持基本类型之间的转换，兼容 JDK 1.7
 * 
 * @author SimpleSpring Framework
 */
public class DefaultTypeConverter implements TypeConverter {

    /** 字符串到基本类型的转换器映射 */
    private static final Map<Class<?>, StringToTypeConverter> stringConverters = new HashMap<Class<?>, StringToTypeConverter>();

    static {
        // 初始化字符串转换器
        stringConverters.put(boolean.class, new StringToBooleanConverter());
        stringConverters.put(Boolean.class, new StringToBooleanConverter());
        stringConverters.put(byte.class, new StringToByteConverter());
        stringConverters.put(Byte.class, new StringToByteConverter());
        stringConverters.put(short.class, new StringToShortConverter());
        stringConverters.put(Short.class, new StringToShortConverter());
        stringConverters.put(int.class, new StringToIntegerConverter());
        stringConverters.put(Integer.class, new StringToIntegerConverter());
        stringConverters.put(long.class, new StringToLongConverter());
        stringConverters.put(Long.class, new StringToLongConverter());
        stringConverters.put(float.class, new StringToFloatConverter());
        stringConverters.put(Float.class, new StringToFloatConverter());
        stringConverters.put(double.class, new StringToDoubleConverter());
        stringConverters.put(Double.class, new StringToDoubleConverter());
        stringConverters.put(char.class, new StringToCharacterConverter());
        stringConverters.put(Character.class, new StringToCharacterConverter());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {
        if (value == null) {
            if (requiredType.isPrimitive()) {
                throw new TypeMismatchException(null, requiredType);
            }
            return null;
        }

        // 如果类型已经匹配，直接返回
        if (ClassUtils.isAssignable(value.getClass(), requiredType)) {
            return (T) value;
        }

        // 处理字符串到其他类型的转换
        if (value instanceof String) {
            return convertFromString((String) value, requiredType);
        }

        // 处理基本类型和包装类型之间的转换
        if (requiredType.isPrimitive() || ClassUtils.isPrimitiveWrapper(requiredType)) {
            return convertPrimitive(value, requiredType);
        }

        // 处理字符串转换
        if (requiredType == String.class) {
            return (T) value.toString();
        }

        throw new TypeMismatchException(value, requiredType);
    }

    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        if (sourceType == null || targetType == null) {
            return false;
        }

        // 相同类型或兼容类型
        if (ClassUtils.isAssignable(sourceType, targetType)) {
            return true;
        }

        // 字符串到基本类型的转换
        if (sourceType == String.class && stringConverters.containsKey(targetType)) {
            return true;
        }

        // 任何类型到字符串的转换
        if (targetType == String.class) {
            return true;
        }

        // 基本类型和包装类型之间的转换
        if ((sourceType.isPrimitive() || ClassUtils.isPrimitiveWrapper(sourceType)) &&
            (targetType.isPrimitive() || ClassUtils.isPrimitiveWrapper(targetType))) {
            return isCompatiblePrimitive(sourceType, targetType);
        }

        return false;
    }

    /**
     * 从字符串转换到指定类型
     * 
     * @param value 字符串值
     * @param requiredType 目标类型
     * @param <T> 目标类型的泛型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    private <T> T convertFromString(String value, Class<T> requiredType) {
        if (requiredType == String.class) {
            return (T) value;
        }

        StringToTypeConverter converter = stringConverters.get(requiredType);
        if (converter != null) {
            try {
                return (T) converter.convert(value);
            } catch (Exception ex) {
                throw new TypeMismatchException(value, requiredType, ex);
            }
        }

        throw new TypeMismatchException(value, requiredType);
    }

    /**
     * 基本类型转换
     * 
     * @param value 源值
     * @param requiredType 目标类型
     * @param <T> 目标类型的泛型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    private <T> T convertPrimitive(Object value, Class<T> requiredType) {
        // 处理数字类型之间的转换
        if (value instanceof Number) {
            Number number = (Number) value;
            
            if (requiredType == int.class || requiredType == Integer.class) {
                return (T) Integer.valueOf(number.intValue());
            } else if (requiredType == long.class || requiredType == Long.class) {
                return (T) Long.valueOf(number.longValue());
            } else if (requiredType == double.class || requiredType == Double.class) {
                return (T) Double.valueOf(number.doubleValue());
            } else if (requiredType == float.class || requiredType == Float.class) {
                return (T) Float.valueOf(number.floatValue());
            } else if (requiredType == short.class || requiredType == Short.class) {
                return (T) Short.valueOf(number.shortValue());
            } else if (requiredType == byte.class || requiredType == Byte.class) {
                return (T) Byte.valueOf(number.byteValue());
            }
        }

        // 处理布尔类型转换
        if (value instanceof Boolean && (requiredType == boolean.class || requiredType == Boolean.class)) {
            return (T) value;
        }

        // 处理字符类型转换
        if (value instanceof Character && (requiredType == char.class || requiredType == Character.class)) {
            return (T) value;
        }

        throw new TypeMismatchException(value, requiredType);
    }

    /**
     * 检查基本类型是否兼容
     * 
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 如果兼容返回 true，否则返回 false
     */
    private boolean isCompatiblePrimitive(Class<?> sourceType, Class<?> targetType) {
        // 获取基本类型
        Class<?> sourcePrimitive = sourceType.isPrimitive() ? sourceType : getPrimitiveType(sourceType);
        Class<?> targetPrimitive = targetType.isPrimitive() ? targetType : getPrimitiveType(targetType);

        if (sourcePrimitive == null || targetPrimitive == null) {
            return false;
        }

        // 数字类型之间可以转换
        if (isNumericType(sourcePrimitive) && isNumericType(targetPrimitive)) {
            return true;
        }

        // 相同类型
        return sourcePrimitive == targetPrimitive;
    }

    /**
     * 获取包装类型对应的基本类型
     * 
     * @param wrapperType 包装类型
     * @return 对应的基本类型
     */
    private Class<?> getPrimitiveType(Class<?> wrapperType) {
        if (wrapperType == Boolean.class) return boolean.class;
        if (wrapperType == Byte.class) return byte.class;
        if (wrapperType == Character.class) return char.class;
        if (wrapperType == Short.class) return short.class;
        if (wrapperType == Integer.class) return int.class;
        if (wrapperType == Long.class) return long.class;
        if (wrapperType == Float.class) return float.class;
        if (wrapperType == Double.class) return double.class;
        return null;
    }

    /**
     * 检查是否为数字类型
     * 
     * @param type 类型
     * @return 如果是数字类型返回 true，否则返回 false
     */
    private boolean isNumericType(Class<?> type) {
        return type == byte.class || type == short.class || type == int.class || 
               type == long.class || type == float.class || type == double.class;
    }

    // 字符串转换器接口
    private interface StringToTypeConverter {
        Object convert(String value);
    }

    // 具体的字符串转换器实现
    private static class StringToBooleanConverter implements StringToTypeConverter {
        @Override
        public Object convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return Boolean.valueOf("true".equalsIgnoreCase(value.trim()) || "1".equals(value.trim()));
        }
    }

    private static class StringToByteConverter implements StringToTypeConverter {
        @Override
        public Object convert(String value) {
            return StringUtils.hasText(value) ? Byte.valueOf(value.trim()) : null;
        }
    }

    private static class StringToShortConverter implements StringToTypeConverter {
        @Override
        public Object convert(String value) {
            return StringUtils.hasText(value) ? Short.valueOf(value.trim()) : null;
        }
    }

    private static class StringToIntegerConverter implements StringToTypeConverter {
        @Override
        public Object convert(String value) {
            return StringUtils.hasText(value) ? Integer.valueOf(value.trim()) : null;
        }
    }

    private static class StringToLongConverter implements StringToTypeConverter {
        @Override
        public Object convert(String value) {
            return StringUtils.hasText(value) ? Long.valueOf(value.trim()) : null;
        }
    }

    private static class StringToFloatConverter implements StringToTypeConverter {
        @Override
        public Object convert(String value) {
            return StringUtils.hasText(value) ? Float.valueOf(value.trim()) : null;
        }
    }

    private static class StringToDoubleConverter implements StringToTypeConverter {
        @Override
        public Object convert(String value) {
            return StringUtils.hasText(value) ? Double.valueOf(value.trim()) : null;
        }
    }

    private static class StringToCharacterConverter implements StringToTypeConverter {
        @Override
        public Object convert(String value) {
            if (StringUtils.hasText(value)) {
                String trimmed = value.trim();
                if (trimmed.length() == 1) {
                    return Character.valueOf(trimmed.charAt(0));
                }
                throw new IllegalArgumentException("String must be exactly one character long");
            }
            return null;
        }
    }
}
