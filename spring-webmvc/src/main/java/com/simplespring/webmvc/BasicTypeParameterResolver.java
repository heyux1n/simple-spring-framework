package com.simplespring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基本类型参数解析器
 * 
 * 负责解析基本数据类型的参数，包括：
 * - String
 * - int/Integer
 * - long/Long
 * - boolean/Boolean
 * - double/Double
 * - float/Float
 * 
 * 参数值从 HTTP 请求参数中获取，支持自动类型转换。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class BasicTypeParameterResolver implements ParameterResolver {

  @Override
  public boolean supportsParameter(Class<?> parameterType) {
    return parameterType == String.class ||
        parameterType == int.class || parameterType == Integer.class ||
        parameterType == long.class || parameterType == Long.class ||
        parameterType == boolean.class || parameterType == Boolean.class ||
        parameterType == double.class || parameterType == Double.class ||
        parameterType == float.class || parameterType == Float.class;
  }

  @Override
  public Object resolveParameter(Class<?> parameterType, String parameterName,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    String parameterValue = request.getParameter(parameterName);

    // 如果参数值为空，返回默认值
    if (parameterValue == null || parameterValue.trim().isEmpty()) {
      return getDefaultValue(parameterType);
    }

    // 根据参数类型进行转换
    return convertValue(parameterValue.trim(), parameterType);
  }

  /**
   * 将字符串值转换为指定类型
   * 
   * @param value      字符串值
   * @param targetType 目标类型
   * @return 转换后的值
   * @throws Exception 如果转换失败
   */
  private Object convertValue(String value, Class<?> targetType) throws Exception {
    try {
      if (targetType == String.class) {
        return value;
      } else if (targetType == int.class || targetType == Integer.class) {
        return Integer.parseInt(value);
      } else if (targetType == long.class || targetType == Long.class) {
        return Long.parseLong(value);
      } else if (targetType == boolean.class || targetType == Boolean.class) {
        return Boolean.parseBoolean(value);
      } else if (targetType == double.class || targetType == Double.class) {
        return Double.parseDouble(value);
      } else if (targetType == float.class || targetType == Float.class) {
        return Float.parseFloat(value);
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("无法将参数值 '" + value + "' 转换为类型 " + targetType.getSimpleName(), e);
    }

    throw new IllegalArgumentException("不支持的参数类型: " + targetType);
  }

  /**
   * 获取指定类型的默认值
   * 
   * @param parameterType 参数类型
   * @return 默认值
   */
  private Object getDefaultValue(Class<?> parameterType) {
    if (parameterType == String.class) {
      return null;
    } else if (parameterType == int.class) {
      return 0;
    } else if (parameterType == Integer.class) {
      return null;
    } else if (parameterType == long.class) {
      return 0L;
    } else if (parameterType == Long.class) {
      return null;
    } else if (parameterType == boolean.class) {
      return false;
    } else if (parameterType == Boolean.class) {
      return null;
    } else if (parameterType == double.class) {
      return 0.0;
    } else if (parameterType == Double.class) {
      return null;
    } else if (parameterType == float.class) {
      return 0.0f;
    } else if (parameterType == Float.class) {
      return null;
    }

    return null;
  }
}
