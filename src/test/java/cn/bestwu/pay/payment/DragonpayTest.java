package cn.bestwu.pay.payment;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Peter Wu
 */
public class DragonpayTest extends BaseWebTest {

  @Autowired
  PayHelpler loongpay;

  @Test
  public void test() throws Exception {
    Order order = new Order() {
      @Override
      public boolean isComplete() {
        return false;
      }

      @Override
      public String getNo() {
        return "";
      }

      @Override
      public String getSubject() {
        return "";
      }

      @Override
      public String getBody() {
        return "";
      }

      @Override
      public long getTotalAmount() {
        return 0;
      }

      @Override
      public String getSpbillCreateIp() {
        return "";
      }

      @Override
      public long getCurrentTimeMillis() {
        return 0;
      }

      @Override
      public String getDeviceInfo() {
        return "";
      }

      @Override
      public String getAttach() {
        return "";
      }

      @Override
      public Object getExtra(String key) {
        return "";
      }
    };
    System.err.println(loongpay.placeOrder(PayMode.LOONGPAY,order,PayType.APP));
  }
}
