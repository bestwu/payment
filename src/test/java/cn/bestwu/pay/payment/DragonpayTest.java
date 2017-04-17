package cn.bestwu.pay.payment;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Peter Wu
 */
public class DragonpayTest extends BaseWebTest {

  @Autowired
  PayHelpler payHelpler;

  @Test
  public void test() throws Exception {
    MyOrder order = new MyOrder();
    order.setNo("123");
    System.err.println(payHelpler.placeOrder("loongpay",order,PayType.APP));
  }
}
