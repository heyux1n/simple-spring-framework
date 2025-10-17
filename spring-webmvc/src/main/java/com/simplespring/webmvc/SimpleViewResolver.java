package com.simplespring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 简单视图解析器实现
 * 
 * SimpleViewResolver 提供基本的视图解析功能，支持：
 * 1. 字符串返回值：直接作为文本响应输出
 * 2. 对象返回值：调用 toString() 方法输出
 * 3. null 返回值：输出空响应
 * 4. void 返回值：不做任何处理（假设响应已在控制器中处理）
 * 
 * 响应格式：
 * - 默认 Content-Type: text/plain; charset=UTF-8
 * - 如果返回值包含 JSON 标识，设置为 application/json
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class SimpleViewResolver implements ViewResolver {

  @Override
  public void resolveView(Object returnValue, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 如果返回值为 void 类型，不做任何处理
    if (returnValue == null) {
      return;
    }

    String content = returnValue.toString();
    String contentType = determineContentType(content);

    // 设置响应头
    response.setContentType(contentType);
    response.setCharacterEncoding("UTF-8");

    // 写入响应内容
    PrintWriter writer = response.getWriter();
    writer.print(content);
    writer.flush();
  }

  /**
   * 根据内容确定 Content-Type
   * 
   * @param content 响应内容
   * @return Content-Type 字符串
   */
  private String determineContentType(String content) {
    if (content == null) {
      return "text/plain; charset=UTF-8";
    }

    // 简单的 JSON 检测
    String trimmedContent = content.trim();
    if ((trimmedContent.startsWith("{") && trimmedContent.endsWith("}")) ||
        (trimmedContent.startsWith("[") && trimmedContent.endsWith("]"))) {
      return "application/json; charset=UTF-8";
    }

    // 检查是否包含 HTML 标签
    if (trimmedContent.contains("<") && trimmedContent.contains(">")) {
      return "text/html; charset=UTF-8";
    }

    // 默认为纯文本
    return "text/plain; charset=UTF-8";
  }
}
