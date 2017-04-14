package cn.bestwu.pay.payment;

/**
 * 支付方式（0：龙支付，1：微信支付，2：支付宝支付，3：银联支付）
 *
 * @author Peter Wu
 */
public enum PayMode {
  /**
   * 支付宝支付
   */
  ALIPAY,
  /**
   * 微信支付
   */
  WEIXINPAY,
  /**
   * 龙支付
   */
  LOONGPAY,
  /**
   * 银联支付
   */
  UNIONPAY;

  public String getName() {
    switch (this) {
      case ALIPAY:
        return "支付宝支付";
      case WEIXINPAY:
        return "微信支付";
      case LOONGPAY:
        return "龙支付";
      case UNIONPAY:
        return "银联支付";
      default:
        return this.name();
    }
  }

}
