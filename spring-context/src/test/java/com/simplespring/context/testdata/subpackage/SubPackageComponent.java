package com.simplespring.context.testdata.subpackage;

import com.simplespring.core.annotation.Component;

/**
 * 测试用的子包组件类
 */
@Component("subComponent")
public class SubPackageComponent {

  public String getMessage() {
    return "Hello from SubPackageComponent";
  }
}
