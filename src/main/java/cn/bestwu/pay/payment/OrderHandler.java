package cn.bestwu.pay.payment;

/**
 * 订单处理
 *
 * @author Peter Wu
 */
public interface OrderHandler {

  /**
   * 根据订单号查询订单
   *
   * @param orderNo 订单号
   * @return 订单
   */
  Order findByNo(String orderNo);

  /**
   * 完成订单
   *
   * @param order 订单
   * @param provider 第三方支付提供方
   * @return 订单
   */
  Order complete(Order order, String provider);
}
