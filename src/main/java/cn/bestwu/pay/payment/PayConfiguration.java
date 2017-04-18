package cn.bestwu.pay.payment;

import cn.bestwu.pay.payment.alipay.AliPayProperties;
import cn.bestwu.pay.payment.alipay.Alipay;
import cn.bestwu.pay.payment.weixinpay.WeixinPay;
import cn.bestwu.pay.payment.weixinpay.WeixinpayProperties;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付配置
 *
 * @author Peter Wu
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnBean(OrderHandler.class)
@EnableConfigurationProperties({WeixinpayProperties.class, AliPayProperties.class})
public class PayConfiguration {

  @ConditionalOnProperty(prefix = "weixinpay", value = "api_key")
  @Bean
  public WeixinPay weixinpay(WeixinpayProperties properties) {
    return new WeixinPay(properties);
  }


  @ConditionalOnProperty(prefix = "alipay", value = "app_id")
  @Bean
  public Alipay alipay(AliPayProperties properties) {
    return new Alipay(properties);
  }

  @Bean
  public PayHelpler payHelpler(List<AbstractPay<? extends PayProperties>> payProviders) {
    return new PayHelpler(payProviders);
  }

  /**
   * 支付通知相关接口
   *
   * @author Peter Wu
   */
  @ConditionalOnWebApplication
  @ConditionalOnBean(OrderHandler.class)
  @RestController
  public static class PayNotifyController {

    private OrderHandler orderHandler;
    private PayHelpler payHelpler;

    @Autowired
    public PayNotifyController(OrderHandler orderHandler, PayHelpler payHelpler) {
      this.orderHandler = orderHandler;
      this.payHelpler = payHelpler;
    }

    @RequestMapping(name = "支付异步通知接口", value = "/payNotifies/{provider}")
    public Object payNotify(@PathVariable("provider") String provider, HttpServletRequest request)
        throws PayException {
      return payHelpler.payNotify(provider, request, orderHandler);
    }

  }
}