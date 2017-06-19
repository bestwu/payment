package cn.bestwu.pay.payment;

import cn.bestwu.pay.payment.alipay.AliClientResult;
import cn.bestwu.pay.payment.alipay.AliPayProperties;
import cn.bestwu.pay.payment.alipay.Alipay;
import cn.bestwu.pay.payment.weixinpay.WeixinPay;
import cn.bestwu.pay.payment.weixinpay.WeixinpayProperties;
import com.alipay.api.AlipayApiException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
  public PayHelpler payHelpler(List<AbstractPay<? extends PayProperties>> payProviders,
      List<OrderHandler> orderHandlers) {
    return new PayHelpler(payProviders, orderHandlers);
  }

  /**
   * 支付通知相关接口
   *
   * @author Peter Wu
   */
  @ConditionalOnWebApplication
  @ConditionalOnBean({OrderHandler.class, PayHelpler.class})
  @RestController
  public static class PayNotifyController {

    private PayHelpler payHelpler;

    @Autowired
    public PayNotifyController(PayHelpler payHelpler) {
      this.payHelpler = payHelpler;
    }

    @RequestMapping(name = "支付异步通知接口", value = "/payNotifies/{handlerType}/{provider}")
    public Object payNotify(@PathVariable("handlerType") String handlerType,
        @PathVariable("provider") String provider, HttpServletRequest request)
        throws PayException {
      return payHelpler.payNotify(provider, request, payHelpler.getOrderHandler(handlerType));
    }

  }

  /**
   * 支付宝相关接口
   *
   * @author Peter Wu
   */
  @ConditionalOnWebApplication
  @RestController
  @ConditionalOnBean({Alipay.class, OrderHandler.class})
  @RequestMapping(name = "支付宝")
  public class AlipayController {

    @Autowired
    private Alipay alipay;
    @Autowired
    private PayHelpler payHelpler;

    /**
     * 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号；
     *
     * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额）；
     *
     * 3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）；
     *
     * 4、验证app_id是否为该商户本身。
     *
     * 上述1、2、3、4有任何一个验证不通过，则表明同步校验结果是无效的，只有全部验证通过后，才可以认定买家付款成功。
     */

    @RequestMapping(name = "alipay客户端结果验签", value = "/alipay/{handlerType}/checkClientResult", method = RequestMethod.GET)
    public Object checkClientResult(@PathVariable("handlerType") String handlerType,
        @Validated AliClientResult result)
        throws AlipayApiException, IOException {
      return ResponseEntity.ok(Collections
          .singletonMap("validated",
              alipay.checkClientResult(result, payHelpler.getOrderHandler(handlerType))));
    }


  }
}