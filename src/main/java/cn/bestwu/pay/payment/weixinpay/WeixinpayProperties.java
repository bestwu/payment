package cn.bestwu.pay.payment.weixinpay;

import cn.bestwu.pay.payment.PayProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微售支付配置
 */
@ConfigurationProperties(prefix = "weixinpay")
public class WeixinpayProperties extends PayProperties {

  /**
   * API密钥
   */
  private String api_key;
  /**
   * 商户ID
   */
  private String mch_id;
  /**
   * 应用ID
   */
  private String appid;

  public String getApi_key() {
    return api_key;
  }

  public void setApi_key(String api_key) {
    this.api_key = api_key;
  }

  public String getAppid() {
    return appid;
  }

  public void setAppid(String appid) {
    this.appid = appid;
  }

  public String getMch_id() {
    return mch_id;
  }

  public void setMch_id(String mch_id) {
    this.mch_id = mch_id;
  }
}