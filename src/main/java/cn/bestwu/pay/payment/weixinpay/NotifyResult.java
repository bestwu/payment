package cn.bestwu.pay.payment.weixinpay;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 返回例子：
 * <xml>
 * <return_code><![CDATA[SUCCESS]]></return_code>
 * <return_msg><![CDATA[OK]]></return_msg>
 * </xml>
 */
@JacksonXmlRootElement(localName = "xml")
public class NotifyResult {

  @JacksonXmlCData
  private String return_code;
  @JacksonXmlCData
  private String return_msg;

  public NotifyResult(String return_code) {
    this.return_code = return_code;
    this.return_msg = return_code;
  }

  public String getReturn_code() {
    return return_code;
  }

  public void setReturn_code(String return_code) {
    this.return_code = return_code;
  }

  public String getReturn_msg() {
    return return_msg;
  }

  public void setReturn_msg(String return_msg) {
    this.return_msg = return_msg;
  }

}