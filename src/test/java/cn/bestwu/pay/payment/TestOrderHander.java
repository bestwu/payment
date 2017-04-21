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
  public void complete(Order order, String provider) {

  }

  @Override
  public void completed(Order order, String provider) {

  }

  @Override
  public void refund(Order order, String provider) {

  }

  @Override
  public void refundComplete(Order order, String provider) {

  }


}
