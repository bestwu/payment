package cn.bestwu.pay.payment;

import org.springframework.stereotype.Component;

/**
 * @author Peter Wu
 */
@Component
public class TestOrderHander implements OrderHandler {

  @Override
  public Order findByNo(String orderNo) {
    return new Order() {
      @Override
      public boolean isCompleted() {
        return false;
      }

      @Override
      public String getNo() {
        return null;
      }

      @Override
      public String getSubject() {
        return null;
      }

      @Override
      public String getBody() {
        return null;
      }

      @Override
      public long getTotalAmount() {
        return 0;
      }

      @Override
      public String getSpbillCreateIp() {
        return null;
      }

      @Override
      public long getCurrentTimeMillis() {
        return 0;
      }

      @Override
      public String getDeviceInfo() {
        return null;
      }

      @Override
      public String getAttach() {
        return null;
      }

      @Override
      public Object getExtra(String key) {
        return null;
      }
    };
  }

  @Override
  public Order complete(Order order, String provider) {
    return order;
  }
}
