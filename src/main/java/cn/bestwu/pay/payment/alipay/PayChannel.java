package cn.bestwu.pay.payment.alipay;

/**
 * @author Peter Wu
 */
public enum PayChannel {
  /**
   * 支付宝红包
   */
  COUPON,
  /**
   * 支付宝余额
   */
  ALIPAYACCOUNT,
  /**
   * 集分宝
   */
  POINT,
  /**
   * 折扣券
   */
  DISCOUNT,
  /**
   * 预付卡
   */
  PCARD,
  /**
   * 余额宝
   */
  FINANCEACCOUNT,
  /**
   * 商家储值卡
   */
  MCARD,
  /**
   * 商户优惠券
   */
  MDISCOUNT,
  /**
   * 商户红包
   */
  MCOUPON,
  /**
   * 蚂蚁花呗
   */
  PCREDIT
}
