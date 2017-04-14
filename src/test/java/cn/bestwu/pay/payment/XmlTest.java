package cn.bestwu.pay.payment;

import cn.bestwu.pay.payment.weixinpay.NotifyResult;
import org.junit.Test;

/**
 * @author Peter Wu
 */
public class XmlTest {

  @Test
  public void test() throws Exception {
    NotifyResult notifyResult = new NotifyResult("SUCCESS");
    notifyResult.setReturn_msg("OK");
    System.err.println(notifyResult);

  }
}
