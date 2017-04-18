package cn.bestwu.pay.payment;

import org.springframework.stereotype.Component;

/**
 * @author Peter Wu
 */
@Component
public class TestOrderHander implements OrderHandler {

  @Override
  public Order findByNo(String orderNo) {
    return new  MyOrder();
  }

  @Override
  public Order complete(Order order, String provider) {
    return order;
  }

  @Override
  public Order refund(Order order, String provider) {
    return null;
  }

  @Override
  public Order refundComplete(Order order, String provider) {
    return null;
  }
}
