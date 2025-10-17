package com.simplespring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 参数解析器接口，定义参数解析的规范
 * 
 * ParameterResolver 负责将 HTTP 请求中的数据解析并绑定到控制器方法的参数上。
 * 不同类型的参数需要不同的解析策略，例如：
 * - 基本类型参数从请求参数中获取
 * - HttpServletRequest/HttpServletResponse 直接注入
 * - 自定义对象参数需要进行数据绑定
 * 
 * 实现类应该专注于处理特定类型的参数解析，遵循单一职责原则。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public interface ParameterResolver {

  /**
   * 检查是否支持解析指定类型的参数
   * 
   * @param parameterType 参数类型
   * @return 如果支持解析返回 true，否则返回 false
   */
  boolean supportsParameter(Class<?> parameterType);

  /**
   * 解析参数值
   * 
   * @param parameterType 参数类型
   * @param parameterName 参数名称（如果可获取）
   * @param request       HTTP 请求对象
   * @param response      HTTP 响应对象
   * @return 解析后的参数值
   * @throws Exception 如果解析失败
   */
  Object resolveParameter(Class<?> parameterType, String parameterName,
      HttpServletRequest request, HttpServletResponse response) throws Exception;
}
