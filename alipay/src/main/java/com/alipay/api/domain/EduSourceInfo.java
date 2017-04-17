package com.alipay.api.domain;

import com.alipay.api.AlipayObject;
import com.alipay.api.internal.mapping.ApiField;

/**
 * 贴子发布接口职位供应商信息
 *
 * @author auto create
 * @since 1.0, 2016-10-26 17:43:38
 */
public class EduSourceInfo extends AlipayObject {

  private static final long serialVersionUID = 7558371358962172513L;

  /**
   * 供应商的LOGO
   */
  @ApiField("logo")
  private String logo;

  /**
   * 供应商电话
   */
  @ApiField("mobile")
  private String mobile;

  /**
   * 供应商名字
   */
  @ApiField("name")
  private String name;

  public String getLogo() {
    return this.logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public String getMobile() {
    return this.mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
