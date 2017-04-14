package cn.bestwu.pay.payment.alipay;

import cn.bestwu.pay.payment.PayProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 支付宝支付
 */
@ConfigurationProperties(prefix = "alipay")
public class AliPayProperties extends PayProperties {

  /**
   * 应用ID
   */
  private String app_id;
  /**
   * 商户ID
   */
  private String seller_id;

  /**
   * 私钥
   */
  private String privateKey;
  /**
   * 公钥
   */
  private String publicKey;

  public String getApp_id() {
    return app_id;
  }

  public void setApp_id(String app_id) {
    this.app_id = app_id;
  }

  public String getSeller_id() {
    return seller_id;
  }

  public void setSeller_id(String seller_id) {
    this.seller_id = seller_id;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }
}