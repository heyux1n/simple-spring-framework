package com.simplespring.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射操作工具类
 * 提供反射操作的便捷方法，兼容 JDK 1.7
 * 
 * @author SimpleSpring Framework
 */
public class ReflectionUtils {

    /**
     * 在指定类中查找指定名称的字段
     * 
     * @param clazz 目标类
     * @param name 字段名称
     * @return 找到的字段，如果未找到返回 null
     */
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /**
     * 在指定类中查找指定名称和类型的字段
     * 
     * @param clazz 目标类
     * @param name 字段名称
     * @param type 字段类型
     * @return 找到的字段，如果未找到返回 null
     */
    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (StringUtils.isEmpty(name) && type == null) {
            throw new IllegalArgumentException("Either name or type of the field must be specified");
        }

        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) &&
                    (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 在指定类中查找指定名称的方法
     * 
     * @param clazz 目标类
     * @param name 方法名称
     * @return 找到的方法，如果未找到返回 null
     */
    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, new Class<?>[0]);
    }

    /**
     * 在指定类中查找指定名称和参数类型的方法
     * 
     * @param clazz 目标类
     * @param name 方法名称
     * @param paramTypes 参数类型数组
     * @return 找到的方法，如果未找到返回 null
     */
    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Method name must not be null");
        }

        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (Method method : methods) {
                if (name.equals(method.getName()) && 
                    (paramTypes == null || parametersMatch(method.getParameterTypes(), paramTypes))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 使字段可访问
     * 
     * @param field 字段
     */
    public static void makeAccessible(Field field) {
        if (field == null) {
            return;
        }
        if ((!Modifier.isPublic(field.getModifiers()) ||
             !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
             Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 使方法可访问
     * 
     * @param method 方法
     */
    public static void makeAccessible(Method method) {
        if (method == null) {
            return;
        }
        if ((!Modifier.isPublic(method.getModifiers()) ||
             !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 使构造函数可访问
     * 
     * @param constructor 构造函数
     */
    public static void makeAccessible(Constructor<?> constructor) {
        if (constructor == null) {
            return;
        }
        if ((!Modifier.isPublic(constructor.getModifiers()) ||
             !Modifier.isPublic(constructor.getDeclaringClass().getModifiers())) && !constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
    }

    /**
     * 调用方法
     * 
     * @param method 方法
     * @param target 目标对象
     * @param args 参数
     * @return 方法返回值
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    /**
     * 获取字段值
     * 
     * @param field 字段
     * @param target 目标对象
     * @return 字段值
     */
    public static Object getField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * 设置字段值
     * 
     * @param field 字段
     * @param target 目标对象
     * @param value 要设置的值
     */
    public static void setField(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * 获取类的所有字段（包括父类）
     * 
     * @param clazz 目标类
     * @return 字段列表
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] declaredFields = searchType.getDeclaredFields();
            for (Field field : declaredFields) {
                fields.add(field);
            }
            searchType = searchType.getSuperclass();
        }
        return fields;
    }

    /**
     * 获取类的所有方法（包括父类）
     * 
     * @param clazz 目标类
     * @return 方法列表
     */
    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<Method>();
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] declaredMethods = searchType.getDeclaredMethods();
            for (Method method : declaredMethods) {
                methods.add(method);
            }
            searchType = searchType.getSuperclass();
        }
        return methods;
    }

    /**
     * 处理反射异常
     * 
     * @param ex 异常
     */
    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * 处理调用目标异常
     * 
     * @param ex 调用目标异常
     */
    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    /**
     * 重新抛出运行时异常
     * 
     * @param ex 异常
     */
    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * 检查方法参数是否匹配
     * 
     * @param declaredTypes 声明的参数类型
     * @param actualTypes 实际参数类型
     * @return 如果匹配返回 true，否则返回 false
     */
    private static boolean parametersMatch(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length != actualTypes.length) {
            return false;
        }
        for (int i = 0; i < declaredTypes.length; i++) {
            if (declaredTypes[i] != actualTypes[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 未声明的可抛出异常包装类
     */
    public static class UndeclaredThrowableException extends RuntimeException {
        public UndeclaredThrowableException(Throwable undeclaredThrowable) {
            super(undeclaredThrowable);
        }
    }
}
