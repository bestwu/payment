package cn.bestwu.pay.payment;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付功能接口
 *
 * @author Peter Wu
 */
public interface Pay {


  /**
   * 支付宝支付
   */
  String ALIPAY = "alipay";
  /**
   * 微信支付
   */
  String WEIXINPAY = "weixinpay";


  String getProvider();

  /**
   * 下单
   *
   * @param order 订单
   * @param payType 支付方式：APP,扫码支付
   * @return 下单结果，客户端调起支付使用的信息
   */
  Object placeOrder(Order order, PayType payType) throws PayException;

  /**
   * 主动查询订单是否支付
   *
   * @param order 订单
   * @return 是否支付完成
   */
  boolean checkOrder(Order order, OrderHandler orderHandler) throws PayException;

  /**
   * 异步通知回调
   *
   * @param request 回调参数
   * @param orderHandler 订单处理类
   * @return 异步通知响应
   */
  Object payNotify(HttpServletRequest request, OrderHandler orderHandler);


}
