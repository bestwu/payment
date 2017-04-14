package cn.bestwu.pay.payment;

import cn.bestwu.pay.payment.PayConfiguration.PayNotifyController;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.util.StringUtils;

/**
 * 支付功能接口
 *
 * @author Peter Wu
 */
public abstract class AbstractPay<P extends PayProperties> {

  protected final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * 支付配置属性
   */
  private P properties;

  public AbstractPay(P properties) {
    this.properties = properties;
  }

  /**
   * 下单
   *
   * @param order 订单
   * @param payType 支付方式：APP,扫码支付
   * @return 下单结果，客户端调起支付使用的信息
   */
  public abstract Object placeOrder(Order order, PayType payType);

  /**
   * 主动查询订单是否支付
   *
   * @param order 订单
   * @return 是否支付完成
   */
  public abstract boolean checkOrder(Order order, OrderHandler orderHandler);

  /**
   * 异步通知回调
   *
   * @param params 回调参数
   * @param orderHandler 订单处理类
   * @return 异步通知响应
   */
  public abstract Object payNotify(Map<String, String> params, OrderHandler orderHandler);

  /**
   * 支付结果回调接口
   *
   * @param payMode 支付方式
   * @return 回调接口
   */
  protected String getNotifyUrl(PayMode payMode) throws NoSuchMethodException {
    String notifyUrl = properties.getNotifyUrl();
    if (StringUtils.hasText(notifyUrl)) {
      return notifyUrl;
    } else {
      switch (payMode) {
        case LOONGPAY:
          return "";
        case WEIXINPAY:
          return ControllerLinkBuilder.linkTo(PayNotifyController.class,
              PayNotifyController.class.getMethod("weixin", Map.class)).withSelfRel().getHref();
        case ALIPAY:
          return ControllerLinkBuilder.linkTo(PayNotifyController.class,
              PayNotifyController.class.getMethod("alipay", Map.class)).withSelfRel().getHref();
        case UNIONPAY:
          return "";
        default:
          return "";
      }
    }
  }

  /**
   * @return 对应支付配置属性
   */
  public P getProperties() {
    return properties;
  }
}
