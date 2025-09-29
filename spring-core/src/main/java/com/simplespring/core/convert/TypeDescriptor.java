package com.simplespring.core.convert;

import com.simplespring.core.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型描述符
 * 提供类型的详细信息，包括泛型信息
 * 
 * @author SimpleSpring Framework
 */
public class TypeDescriptor {

    private final Class<?> type;
    private final Type genericType;
    private final Object source;

    /**
     * 构造函数
     * 
     * @param type 类型
     */
    public TypeDescriptor(Class<?> type) {
        this(type, type, null);
    }

    /**
     * 构造函数
     * 
     * @param type 类型
     * @param genericType 泛型类型
     * @param source 源对象（字段或方法参数等）
     */
    private TypeDescriptor(Class<?> type, Type genericType, Object source) {
        this.type = type;
        this.genericType = genericType;
        this.source = source;
    }

    /**
     * 获取类型
     * 
     * @return 类型
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * 获取泛型类型
     * 
     * @return 泛型类型
     */
    public Type getGenericType() {
        return genericType;
    }

    /**
     * 获取源对象
     * 
     * @return 源对象
     */
    public Object getSource() {
        return source;
    }

    /**
     * 检查是否为基本类型
     * 
     * @return 如果是基本类型返回 true，否则返回 false
     */
    public boolean isPrimitive() {
        return ClassUtils.isPrimitiveType(type);
    }

    /**
     * 检查是否为数组类型
     * 
     * @return 如果是数组类型返回 true，否则返回 false
     */
    public boolean isArray() {
        return type != null && type.isArray();
    }

    /**
     * 检查是否为集合类型
     * 
     * @return 如果是集合类型返回 true，否则返回 false
     */
    public boolean isCollection() {
        return type != null && java.util.Collection.class.isAssignableFrom(type);
    }

    /**
     * 检查是否为 Map 类型
     * 
     * @return 如果是 Map 类型返回 true，否则返回 false
     */
    public boolean isMap() {
        return type != null && java.util.Map.class.isAssignableFrom(type);
    }

    /**
     * 获取元素类型（用于数组和集合）
     * 
     * @return 元素类型描述符
     */
    public TypeDescriptor getElementTypeDescriptor() {
        if (isArray()) {
            return new TypeDescriptor(type.getComponentType());
        }
        
        if (isCollection() && genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypes = paramType.getActualTypeArguments();
            if (actualTypes.length > 0 && actualTypes[0] instanceof Class) {
                return new TypeDescriptor((Class<?>) actualTypes[0]);
            }
        }
        
        return null;
    }

    /**
     * 获取 Map 的键类型描述符
     * 
     * @return 键类型描述符
     */
    public TypeDescriptor getMapKeyTypeDescriptor() {
        if (isMap() && genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypes = paramType.getActualTypeArguments();
            if (actualTypes.length > 0 && actualTypes[0] instanceof Class) {
                return new TypeDescriptor((Class<?>) actualTypes[0]);
            }
        }
        
        return null;
    }

    /**
     * 获取 Map 的值类型描述符
     * 
     * @return 值类型描述符
     */
    public TypeDescriptor getMapValueTypeDescriptor() {
        if (isMap() && genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypes = paramType.getActualTypeArguments();
            if (actualTypes.length > 1 && actualTypes[1] instanceof Class) {
                return new TypeDescriptor((Class<?>) actualTypes[1]);
            }
        }
        
        return null;
    }

    /**
     * 从字段创建类型描述符
     * 
     * @param field 字段
     * @return 类型描述符
     */
    public static TypeDescriptor forField(Field field) {
        if (field == null) {
            return null;
        }
        return new TypeDescriptor(field.getType(), field.getGenericType(), field);
    }

    /**
     * 从方法参数创建类型描述符
     * 
     * @param method 方法
     * @param parameterIndex 参数索引
     * @return 类型描述符
     */
    public static TypeDescriptor forMethodParameter(Method method, int parameterIndex) {
        if (method == null || parameterIndex < 0 || parameterIndex >= method.getParameterTypes().length) {
            return null;
        }
        
        Class<?> paramType = method.getParameterTypes()[parameterIndex];
        Type genericType = method.getGenericParameterTypes()[parameterIndex];
        
        return new TypeDescriptor(paramType, genericType, method);
    }

    /**
     * 从方法返回值创建类型描述符
     * 
     * @param method 方法
     * @return 类型描述符
     */
    public static TypeDescriptor forMethodReturnType(Method method) {
        if (method == null) {
            return null;
        }
        return new TypeDescriptor(method.getReturnType(), method.getGenericReturnType(), method);
    }

    /**
     * 从对象创建类型描述符
     * 
     * @param object 对象
     * @return 类型描述符
     */
    public static TypeDescriptor forObject(Object object) {
        if (object == null) {
            return null;
        }
        return new TypeDescriptor(object.getClass());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        TypeDescriptor that = (TypeDescriptor) obj;
        return type.equals(that.type) && 
               (genericType != null ? genericType.equals(that.genericType) : that.genericType == null);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (genericType != null ? genericType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TypeDescriptor{" +
               "type=" + type.getName() +
               ", genericType=" + genericType +
               '}';
    }
}
