package cn.bestwu.pay.payment;

import org.springframework.stereotype.Component;

/**
 * @author Peter Wu
 */
@Component
public class TestOrderHander implements OrderHandler {

  @Override
  public Order findByNo(String orderNo) {
    MyOrder myOrder = new MyOrder();
    myOrder.setNo(orderNo);
    return myOrder;
  }

  @Override
  public Order complete(Order order, String provider) {
    return order;
  }

  @Override
  public Order completed(Order order, String provider) {
    return order;
  }

  @Override
  public Order refund(Order order, String provider) {
    return order;
  }

  @Override
  public Order refundComplete(Order order, String provider) {
    return order;
  }
}
