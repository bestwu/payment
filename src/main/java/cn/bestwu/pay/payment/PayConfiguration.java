package cn.bestwu.pay.payment;

import cn.bestwu.pay.payment.alipay.AliPayProperties;
import cn.bestwu.pay.payment.alipay.Alipay;
import cn.bestwu.pay.payment.loongpay.Loongpay;
import cn.bestwu.pay.payment.loongpay.LoongpayProperties;
import cn.bestwu.pay.payment.weixinpay.Weixinpay;
import cn.bestwu.pay.payment.weixinpay.WeixinpayProperties;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付配置
 *
 * @author Peter Wu
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnBean(OrderHandler.class)
@EnableConfigurationProperties({WeixinpayProperties.class, AliPayProperties.class,
    LoongpayProperties.class})
public class PayConfiguration {

  @ConditionalOnProperty(prefix = "weixinpay", value = "api_key")
  @Bean
  public Weixinpay weixinpay(WeixinpayProperties properties) {
    return new Weixinpay(properties);
  }

  @ConditionalOnProperty(prefix = "loongpay", value = "pub")
  @Bean
  public Loongpay loongpay(LoongpayProperties properties) {
    return new Loongpay(properties);
  }

  @ConditionalOnProperty(prefix = "alipay", value = "app_id")
  @Bean
  public Alipay alipay(AliPayProperties properties) {
    return new Alipay(properties);
  }

  @Bean
  public PayHelpler payHelpler() {
    return new PayHelpler();
  }

  /**
   * 支付通知相关接口
   *
   * @author Peter Wu
   */
  @ConditionalOnWebApplication
  @ConditionalOnBean(OrderHandler.class)
  @RestController
  @RequestMapping(name = "支付", value = "/pay/notifies")
  public static class PayNotifyController {

    private OrderHandler orderHandler;
    private PayHelpler payHelpler;

    @Autowired
    public PayNotifyController(OrderHandler orderHandler, PayHelpler payHelpler) {
      this.orderHandler = orderHandler;
      this.payHelpler = payHelpler;
    }

    @RequestMapping(name = "alipay异步通知接口", value = "/alipay", method = RequestMethod.POST)
    public Object alipay(@RequestParam Map<String, String> params) {
      return payHelpler.payNotify(PayMode.ALIPAY, params, orderHandler);
    }


    /**
     * 微信服务器异步通知页面
     */
    @RequestMapping(name = "weixin异步通知接口", value = "/weixin", method = {RequestMethod.POST,
        RequestMethod.GET})
    public Object weixin(@RequestBody Map<String, String> params) {
      return payHelpler.payNotify(PayMode.WEIXINPAY, params, orderHandler);
    }


    /**
     * 龙支付通知
     *
     * @return 结果
     */
    @RequestMapping(value = "/loongpay")
    public Object loongpay(@RequestParam Map<String, String> params) {
      return payHelpler.payNotify(PayMode.LOONGPAY, params, orderHandler);
    }
  }
}