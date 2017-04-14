package cn.bestwu.pay.payment;

import cn.bestwu.test.client.CustomRestTemplate;
import java.util.Map;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;

/**
 * web容器基础测试类
 *
 * @author Peter Wu
 */
@SpringBootTest(classes = Application.class, value = {
    "server.context-path=/mobile"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseWebTest extends BaseTest {

  protected CustomRestTemplate restTemplate = new CustomRestTemplate();

  @LocalServerPort
  private int port;
  /**
   * 默认测试账号
   */
  protected String defaultUsername = "KD001";
  /**
   * 默认测试账号密码
   */
  protected String defaultPassword = DigestUtils.md5DigestAsHex(("666666" + "KD001").getBytes());

  /**
   * @return 基础URL
   */
  protected String getBaseUrl() {
    String host = "http://127.0.0.1";
    return host + ":" + port + "/mobile";
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

  /**
   * 补全URL
   *
   * @param apiPath api path
   * @return 完整URL
   */
  protected String expandUrlWithToken(String apiPath) {
    if (apiPath.contains("?")) {
      return getBaseUrl() + apiPath + "&accessToken=" + getAccessToken();
    } else {
      return getBaseUrl() + apiPath + "?accessToken=" + getAccessToken();
    }
  }

  private String getAccessToken() {
    LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("jobNumber", defaultUsername);
    params.add("password", defaultPassword);
    ResponseEntity<Map> entity = restTemplate.postForEntity(expandUrl("/login"), params, Map.class);
    return (String) ((Map) entity.getBody().get("accessToken")).get("accessToken");
  }
}
