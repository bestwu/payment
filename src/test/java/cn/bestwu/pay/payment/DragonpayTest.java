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
  public void placeOrder() throws Exception {
    MyOrder order = new MyOrder();
    order.setNo("123");
    System.err.println(payHelpler.placeOrder("loongpay",order,PayType.APP));
  }
  @Test
  public void checkOrder() throws Exception {
    TestOrderHander testOrderHander = new TestOrderHander();
    System.err.println(payHelpler.checkOrder("loongpay","2017041816202235",testOrderHander));
  }
}
