package cn.bestwu.pay.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Wu
 */
@RestController
public class TestController {

  @Autowired
  private PayHelpler payHelpler;

  @RequestMapping("/test")
  public Object test(PayType payType,MyOrder order,String provider) throws PayException {
    return payHelpler.placeOrder(provider, order, payType);
  }
}
