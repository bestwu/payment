package cn.bestwu.pay.payment;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

  @Test
  public void payNotify() throws Exception {
    MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
    param.add("CLIENTIP", "118.116.111.39");
    param.add("BRANCHID", "510000000");
    param.add("CURCODE", "01");
    param.add("REFERER", "");
    param.add("TYPE", "1");
    param.add("REMARK1", "");
    param.add("REMARK2", "");
    param.add("SUCCESS", "Y");
    param.add("ACC_TYPE", "02");
    param.add("POSID", "426295203");
    param.add("ORDERID", "2017032813320866");
    param.add("PAYMENT", "0.02");
    param.add("SIGN", "");

//    {"SIGN":["96b418dd0ca4ea9330f0ffa7c967f25d60eb915956cf21d50310c388f7e79dad1e2cca4459360c485d9b7f29c382c99c3f805531f1fe8a71385e6082f1c2a885a05559f7bde392ab1dc9be7fc0ebb012e52fcda1c52c7e1762853ee049a5278660a88766933ef28066701d671e89e7f47cc5a9db1f1d90b208aecfda62e540f9"],
//      "CLIENTIP":["118.116.111.39"],"BRANCHID":["510000000"],"CURCODE":["01"],
//      "ORDERID":["2017032813320866"],"REFERER":[""],"TYPE":["1"],"REMARK1":[""],"REMARK2":[""]
//      ,"SUCCESS":["Y"],"ACC_TYPE":["02"],"POSID":["426295203"],"PAYMENT":["0.02"]}

  }
}
