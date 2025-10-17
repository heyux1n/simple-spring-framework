package com.simplespring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 视图解析器接口，定义视图解析的规范
 * 
 * ViewResolver 负责将控制器方法的返回值转换为 HTTP 响应。
 * 不同类型的返回值需要不同的处理策略：
 * - 字符串：可以作为视图名称或直接输出
 * - 对象：可以序列化为 JSON 或其他格式
 * - void：表示响应已经在控制器方法中处理完成
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public interface ViewResolver {

  /**
   * 解析视图并生成响应
   * 
   * @param returnValue 控制器方法的返回值
   * @param request     HTTP 请求对象
   * @param response    HTTP 响应对象
   * @throws Exception 如果视图解析失败
   */
  void resolveView(Object returnValue, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
