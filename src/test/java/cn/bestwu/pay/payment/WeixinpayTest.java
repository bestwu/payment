package cn.bestwu.pay.payment;

import cn.bestwu.lang.util.RandomUtil;
import cn.bestwu.lang.util.StringUtil;
import cn.bestwu.test.client.CustomRestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

/**
 * @author Peter Wu
 */
public class WeixinpayTest {

  private CustomRestTemplate restTemplate = new CustomRestTemplate();

  private static final String URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

  public WeixinpayTest() {
    HttpMessageConverter<?> messageConverter = new MappingJackson2XmlHttpMessageConverter() {
      @Override
      protected boolean canRead(MediaType mediaType) {
        return true;
      }

      @Override
      public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return true;
      }
    };

    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(messageConverter);
    restTemplate.setMessageConverters(messageConverters);
  }

  @Test
  public void test() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("appid", "dd");
    params.put("mch_id", "dd");
    params.put("device_info", "WEB");
    params.put("nonce_str", RandomUtil.nextString2(32));
    params.put("body", "测试");
    params.put("out_trade_no", "123");
    params.put("total_fee", "1");
    params.put("spbill_create_ip", "233.12.12.1");

    params.put("notify_url", "sdf");
    params.put("trade_type", "APP");

    params.put("sign", "sd");

    restTemplate.setPrint(true);
    @SuppressWarnings("unchecked")
    Map<String, String> entity = restTemplate.postForObject(URL, params, Map.class);

    System.err.println(StringUtil.valueOf(entity));

  }
}
