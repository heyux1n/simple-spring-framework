package com.simplespring.webmvc;

import com.simplespring.core.annotation.RequestMethod;

/**
 * 处理器映射接口，定义处理器映射的核心方法
 * 
 * HandlerMapping 负责将 HTTP 请求映射到相应的处理器方法。
 * 它是 Spring MVC 框架中的核心组件之一，用于建立请求 URL 和控制器方法之间的映射关系。
 * 
 * 主要功能：
 * 1. 根据请求路径和 HTTP 方法查找匹配的处理器
 * 2. 管理请求映射信息的注册和查询
 * 3. 支持不同的映射策略（注解驱动、配置驱动等）
 * 
 * 实现类应该在应用启动时扫描控制器类，构建请求映射表，
 * 并在运行时快速查找匹配的处理器方法。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public interface HandlerMapping {

  /**
   * 根据请求路径和 HTTP 方法获取处理器执行链
   * 
   * @param requestPath   请求路径，如 "/users/123"
   * @param requestMethod HTTP 方法，如 GET、POST 等
   * @return 匹配的处理器执行链，如果没有找到匹配的处理器则返回 null
   */
  HandlerExecutionChain getHandler(String requestPath, RequestMethod requestMethod);

  /**
   * 注册请求映射信息
   * 
   * @param mappingInfo 请求映射信息
   */
  void registerMapping(RequestMappingInfo mappingInfo);

  /**
   * 获取所有已注册的请求映射信息
   * 
   * @return 所有请求映射信息的数组
   */
  RequestMappingInfo[] getAllMappings();

  /**
   * 检查是否存在指定路径和方法的映射
   * 
   * @param requestPath   请求路径
   * @param requestMethod HTTP 方法
   * @return 如果存在映射返回 true，否则返回 false
   */
  boolean hasMapping(String requestPath, RequestMethod requestMethod);
}
