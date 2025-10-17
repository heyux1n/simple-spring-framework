package com.simplespring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 参数解析器组合类
 * 
 * 管理多个参数解析器，并提供统一的参数解析接口。
 * 使用责任链模式，依次尝试各个解析器直到找到合适的解析器。
 * 
 * 主要功能：
 * 1. 管理参数解析器列表
 * 2. 为控制器方法解析所有参数
 * 3. 支持添加自定义参数解析器
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class ParameterResolverComposite {

  /**
   * 参数解析器列表
   */
  private final List<ParameterResolver> resolvers = new ArrayList<ParameterResolver>();

  /**
   * 构造函数，初始化默认的参数解析器
   */
  public ParameterResolverComposite() {
    // 添加默认的参数解析器
    addResolver(new ServletParameterResolver());
    addResolver(new BasicTypeParameterResolver());
  }

  /**
   * 添加参数解析器
   * 
   * @param resolver 参数解析器
   */
  public void addResolver(ParameterResolver resolver) {
    if (resolver != null) {
      resolvers.add(resolver);
    }
  }

  /**
   * 为控制器方法解析所有参数
   * 
   * @param handlerMethod 处理器方法
   * @param request       HTTP 请求对象
   * @param response      HTTP 响应对象
   * @return 解析后的参数数组
   * @throws Exception 如果参数解析失败
   */
  public Object[] resolveParameters(HandlerMethod handlerMethod,
      HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    Method method = handlerMethod.getMethod();
    Class<?>[] parameterTypes = method.getParameterTypes();

    if (parameterTypes.length == 0) {
      return new Object[0];
    }

    Object[] args = new Object[parameterTypes.length];

    for (int i = 0; i < parameterTypes.length; i++) {
      Class<?> parameterType = parameterTypes[i];
      String parameterName = getParameterName(method, i);

      Object parameterValue = resolveParameter(parameterType, parameterName, request, response);
      args[i] = parameterValue;
    }

    return args;
  }

  /**
   * 解析单个参数
   * 
   * @param parameterType 参数类型
   * @param parameterName 参数名称
   * @param request       HTTP 请求对象
   * @param response      HTTP 响应对象
   * @return 解析后的参数值
   * @throws Exception 如果参数解析失败
   */
  private Object resolveParameter(Class<?> parameterType, String parameterName,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    for (ParameterResolver resolver : resolvers) {
      if (resolver.supportsParameter(parameterType)) {
        return resolver.resolveParameter(parameterType, parameterName, request, response);
      }
    }

    throw new IllegalArgumentException("没有找到支持参数类型 " + parameterType + " 的解析器");
  }

  /**
   * 获取参数名称
   * 
   * 在 JDK 1.7 中，无法通过反射直接获取参数名称，
   * 这里使用简单的命名规则：param0, param1, param2...
   * 
   * 在实际项目中，可以通过以下方式获取参数名称：
   * 1. 使用 @RequestParam 注解指定参数名
   * 2. 编译时保留参数名信息（-parameters 选项）
   * 3. 使用字节码分析工具
   * 
   * @param method         方法对象
   * @param parameterIndex 参数索引
   * @return 参数名称
   */
  private String getParameterName(Method method, int parameterIndex) {
    // 简单的参数命名规则
    return "param" + parameterIndex;
  }

  /**
   * 检查是否支持指定类型的参数
   * 
   * @param parameterType 参数类型
   * @return 如果支持返回 true，否则返回 false
   */
  public boolean supportsParameter(Class<?> parameterType) {
    for (ParameterResolver resolver : resolvers) {
      if (resolver.supportsParameter(parameterType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 获取解析器数量
   * 
   * @return 解析器数量
   */
  public int getResolverCount() {
    return resolvers.size();
  }
}
