package cn.bestwu.pay.payment;

import cn.bestwu.test.client.CustomRestTemplate;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * web容器基础测试类
 *
 * @author Peter Wu
 */
@SpringBootTest(classes = Application.class, value = {
    "server.context-path=/payment"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseWebTest extends BaseTest {

  protected CustomRestTemplate restTemplate = new CustomRestTemplate();

  @LocalServerPort
  private int port;

  /**
   * @return 基础URL
   */
  protected String getBaseUrl() {
    String host = "http://127.0.0.1";
    return host + ":" + port + "/payment";
  }

  /**
   * 补全URL
   *
   * @param apiPath api path
   * @return 完整URL
   */
  protected String expandUrl(String apiPath) {
    return getBaseUrl() + apiPath;
  }


}
