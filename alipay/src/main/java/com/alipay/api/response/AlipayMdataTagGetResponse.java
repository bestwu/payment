package com.alipay.api.response;

import com.alipay.api.internal.mapping.ApiField;

import com.alipay.api.AlipayResponse;

/**
 * ALIPAY API: alipay.mdata.tag.get response.
 *
 * @author auto create
 * @since 1.0, 2015-03-11 14:09:56
 */
public class AlipayMdataTagGetResponse extends AlipayResponse {

  private static final long serialVersionUID = 1464485471613765445L;

  /**
   * 查询到的标签值, JSON字符串
   */
  @ApiField("tag_values")
  private String tagValues;

  public void setTagValues(String tagValues) {
    this.tagValues = tagValues;
  }

  public String getTagValues() {
    return this.tagValues;
  }

}
