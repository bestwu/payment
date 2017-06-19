package cn.bestwu.pay.payment;

import cn.bestwu.lang.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Peter Wu
 */
public class PlaceOrderTest extends BaseWebTest {


  @Test
  public void test() throws Exception {
    ResponseEntity<String> entity = restTemplate
        .postForEntity(expandUrl(
            "/test?provider=alipay&payType=APP&total_amount=200&no=2088102116773037&body=大乐透2.1"),
            null, String.class);
    System.err.println(StringUtil.valueOf(entity.getBody(), true));
    Assert.assertEquals(HttpStatus.OK, entity.getStatusCode());
  }
}
