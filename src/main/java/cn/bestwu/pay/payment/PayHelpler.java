package cn.bestwu.pay.payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * 支付辅助类
 *
 * @author Peter Wu
 */
public class PayHelpler {

  private Map<String, AbstractPay<? extends PayProperties>> payProviders = new HashMap<>();


  public PayHelpler(List<AbstractPay<? extends PayProperties>> payProviders) {
    for (AbstractPay<? extends PayProperties> payProvider : payProviders) {
      this.payProviders.put(payProvider.getProvider(), payProvider);
    }
  }

  /**
   * 下单
   *
   * @param provider 第三方支付提供方
   * @param order 订单
   * @param payType 支付方式：APP,扫码支付
   * @return 下单结果，客户端调起支付使用的信息
   */
  public Object placeOrder(String provider, Order order, PayType payType) throws PayException {
    AbstractPay<? extends PayProperties> payProvider = payProviders.get(provider);
    if (payProvider == null) {
      throw new PayException("不支持的支付方式");
    } else {
      return payProvider.placeOrder(order, payType);
    }
  }

  /**
   * 主动查询订单是否支付
   *
   * @param provider 第三方支付提供方
   * @param orderNo 订单号
   * @return 是否支付完成
   */
  public boolean checkOrder(String provider, String orderNo, OrderHandler orderHandler)
      throws PayException {
    AbstractPay<? extends PayProperties> payProvider = payProviders.get(provider);
    if (payProvider == null) {
      throw new PayException("不支持的支付方式");
    } else {
      return payProvider.checkOrder(orderNo, orderHandler);
    }
  }

  /**
   * 异步通知回调
   *
   * @param provider 第三方支付提供方
   * @param request 回调参数
   * @param orderHandler 订单处理类
   * @return 异步通知响应
   */
  public Object payNotify(String provider, HttpServletRequest request, OrderHandler orderHandler)
      throws PayException {
    AbstractPay<? extends PayProperties> payProvider = payProviders.get(provider);
    if (payProvider == null) {
      throw new PayException("不支持的支付方式");
    } else {
      return payProvider.payNotify(request, orderHandler);
    }
  }

  /**
   * 订单退款
   *
   * @param order 订单
   * @param orderHandler 订单处理类
   * @throws PayException PayException
   */
  public void refund(String provider, Order order, OrderHandler orderHandler) throws PayException {
    AbstractPay<? extends PayProperties> payProvider = payProviders.get(provider);
    if (payProvider == null) {
      throw new PayException("不支持的支付方式");
    } else {
      payProvider.refund(order, orderHandler);
    }
  }

  /**
   * 订单退款结果查询
   *
   * @param orderNo 订单号
   * @param orderHandler 订单处理类
   * @return 退款是否成功
   */
  public boolean refundQuery(String provider, String orderNo, OrderHandler orderHandler)
      throws PayException {
    AbstractPay<? extends PayProperties> payProvider = payProviders.get(provider);
    if (payProvider == null) {
      throw new PayException("不支持的支付方式");
    } else {
      return payProvider.refundQuery(orderNo, orderHandler);
    }
  }

}
