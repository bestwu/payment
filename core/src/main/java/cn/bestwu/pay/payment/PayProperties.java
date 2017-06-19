package cn.bestwu.pay.payment;

/**
 * 支付公共配置属性
 *
 * @author Peter Wu
 */
public class PayProperties {

  /**
   * 回调地址
   */
  private String notifyUrl;

  public String getNotifyUrl() {
    return notifyUrl;
  }

  public void setNotifyUrl(String notifyUrl) {
    this.notifyUrl = notifyUrl;
  }
}
