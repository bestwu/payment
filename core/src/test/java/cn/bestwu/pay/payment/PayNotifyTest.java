package cn.bestwu.pay.payment;

import cn.bestwu.lang.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

/**
 * @author Peter Wu
 */
public class PayNotifyTest extends BaseWebTest {

  @Test
  public void alipay() throws Exception {
    ResponseEntity<String> entity = restTemplate
        .postForEntity(expandUrl(
            "/payNotifies/alipay?total_amount=2.00&buyer_id=2088102116773037&body=大乐透2.1&trade_no=2016071921001003030200089909&refund_fee=0.00&notify_time=2016-07-19 14:10:49&subject=大乐透2.1&sign_type=RSA2&charset=utf-8&notify_type=trade_status_sync&out_trade_no=0719141034-6418&gmt_close=2016-07-19 14:10:46&gmt_payment=2016-07-19 14:10:47&trade_status=TRADE_SUCCESS&version=1.0&sign=kPbQIjX+xQc8F0/A6/AocELIjhhZnGbcBN6G4MM/HmfWL4ZiHM6fWl5NQhzXJusaklZ1LFuMo+lHQUELAYeugH8LYFvxnNajOvZhuxNFbN2LhF0l/KL8ANtj8oyPM4NN7Qft2kWJTDJUpQOzCzNnV9hDxh5AaT9FPqRS6ZKxnzM=&gmt_create=2016-07-19 14:10:44&app_id=2015102700040153&seller_id=2088102119685838&notify_id=4a91b7a78a503640467525113fb7d8bg8e"),
            null, String.class);
    System.err.println(StringUtil.valueOf(entity.getBody(), true));
    Assert.assertEquals(HttpStatus.OK, entity.getStatusCode());
  }

  @Test
  public void wechatpay() throws Exception {
    LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add(HttpHeaders.CONTENT_TYPE, "application/xml;charset=UTF-8");
    HttpEntity<String> requestEntity = new HttpEntity<>("<xml>\n"
        + "  <appid><![CDATA[wx2421b1c4370ec43b]]></appid>\n"
        + "  <attach><![CDATA[支付测试]]></attach>\n"
        + "  <bank_type><![CDATA[CFT]]></bank_type>\n"
        + "  <fee_type><![CDATA[CNY]]></fee_type>\n"
        + "  <is_subscribe><![CDATA[Y]]></is_subscribe>\n"
        + "  <mch_id><![CDATA[10000100]]></mch_id>\n"
        + "  <nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str>\n"
        + "  <openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid>\n"
        + "  <out_trade_no><![CDATA[1409811653]]></out_trade_no>\n"
        + "  <result_code><![CDATA[SUCCESS]]></result_code>\n"
        + "  <return_code><![CDATA[SUCCESS]]></return_code>\n"
        + "  <sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign>\n"
        + "  <sub_mch_id><![CDATA[10000100]]></sub_mch_id>\n"
        + "  <time_end><![CDATA[20140903131540]]></time_end>\n"
        + "  <total_fee>1</total_fee>\n"
        + "  <trade_type><![CDATA[JSAPI]]></trade_type>\n"
        + "  <transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id>\n"
        + "</xml>", headers);

    ResponseEntity<String> entity = restTemplate
        .postForEntity(expandUrl("/payNotifies/wechatpay"), requestEntity, String.class);
    System.err.println(StringUtil.valueOf(entity.getBody(), true));
    Assert.assertEquals(HttpStatus.OK, entity.getStatusCode());
  }
}
