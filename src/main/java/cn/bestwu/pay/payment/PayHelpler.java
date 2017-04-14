package cn.bestwu.pay.payment;

import cn.bestwu.pay.payment.alipay.Alipay;
import cn.bestwu.pay.payment.loongpay.Loongpay;
import cn.bestwu.pay.payment.weixinpay.Weixinpay;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 支付辅助类
 *
 * @author Peter Wu
 */
public class PayHelpler {

  @Autowired(required = false)
  private Weixinpay weixinpay;
  @Autowired(required = false)
  private Alipay alipay;
  @Autowired(required = false)
  private Loongpay loongpay;

  /**
   * 下单
   *
   * @param payMode 支付方式
   * @param order 订单
   * @param payType 支付方式：APP,扫码支付
   * @return 下单结果，客户端调起支付使用的信息
   */
  public Object placeOrder(PayMode payMode, Order order, PayType payType) {
    switch (payMode) {
      case ALIPAY:
        if (alipay == null) {
          break;
        }
        return alipay.placeOrder(order, payType);
      case WEIXINPAY:
        if (weixinpay == null) {
          break;
        }
        return weixinpay.placeOrder(order, payType);
      case LOONGPAY:
        if (loongpay == null) {
          break;
        }
        return loongpay.placeOrder(order, payType);
      case UNIONPAY:
      default:
        break;
    }
    throw new PayException("不支持的支付方式");
  }

  /**
   * 主动查询订单是否支付
   *
   * @param payMode 支付方式
   * @param order 订单
   * @return 是否支付完成
   */
  public boolean checkOrder(PayMode payMode, Order order, OrderHandler orderHandler) {
    switch (payMode) {
      case ALIPAY:
        if (alipay == null) {
          break;
        }
        return alipay.checkOrder(order, orderHandler);
      case WEIXINPAY:
        if (weixinpay == null) {
          break;
        }
        return weixinpay.checkOrder(order, orderHandler);
      case LOONGPAY:
        if (loongpay == null) {
          break;
        }
        return loongpay.checkOrder(order, orderHandler);
      case UNIONPAY:
      default:
        break;
    }
    throw new PayException("不支持的支付方式");
  }

  /**
   * 异步通知回调
   *
   * @param payMode 支付方式
   * @param params 回调参数
   * @param orderHandler 订单处理类
   * @return 异步通知响应
   */
  public Object payNotify(PayMode payMode, Map<String, String> params, OrderHandler orderHandler) {
    switch (payMode) {
      case ALIPAY:
        if (alipay == null) {
          break;
        }
        return alipay.payNotify(params, orderHandler);
      case WEIXINPAY:
        if (weixinpay == null) {
          break;
        }
        return weixinpay.payNotify(params, orderHandler);
      case LOONGPAY:
        if (loongpay == null) {
          break;
        }
        return loongpay.payNotify(params, orderHandler);
      case UNIONPAY:
      default:
        break;
    }
    throw new PayException("不支持的支付方式");
  }

}
