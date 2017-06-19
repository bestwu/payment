package cn.bestwu.pay.payment;

/**
 * 订单处理
 *
 * @author Peter Wu
 */
public interface OrderHandler {

  /**
   * @return 处理类型 不能为空或空白字符串
   */
  String getHandlerType();

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
   */
  void complete(Order order, String provider);

  /**
   * 已完成订单，同时多种方式支付成功，进一步处理，退款或其他处理
   *
   * @param order 订单
   * @param provider 第三方支付提供方
   */
  void completed(Order order, String provider);

  /**
   * 处理退款申请完成业务逻辑
   *
   * @param order 订单
   * @param provider 第三方支付提供方
   */
  void refund(Order order, String provider);

  /**
   * 处理退款完成业务逻辑
   *
   * @param order 订单
   * @param provider 第三方支付提供方
   */
  void refundComplete(Order order, String provider);
}
