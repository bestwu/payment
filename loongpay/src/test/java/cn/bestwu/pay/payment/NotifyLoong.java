package cn.bestwu.pay.payment;

import cn.bestwu.test.client.CustomRestTemplate;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Peter Wu
 */
public class NotifyLoong {

  @Test
  public void payNotify() throws Exception {
    MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
    param.add("CLIENTIP", "222.211.204.87");
    param.add("BRANCHID", "510000000");
    param.add("CURCODE", "01");
    param.add("REFERER", "");
    param.add("TYPE", "1");
    param.add("REMARK1", "");
    param.add("REMARK2", "");
    param.add("SUCCESS", "Y");
    param.add("ACC_TYPE", "06");
    param.add("POSID", "426295203");
    param.add("ORDERID", "2017041818365200");
    param.add("PAYMENT", "2.30");
    param.add("SIGN",
        "32d9abd7eecee879ea7aa8c65a872bfcb2bed7b7d61f2048ef9088c64eee565a4c093d6ff821686e6983f07ceb7b7155bbf61fc898f5aec46fd057862e4d39a2206ac56c694c937ec870008fe0daf172445a79914fe06533f58fedf50b2732ce0d4075110ab3294735706fa64dbba4dc9720baa7b6397c44b67239dc17a61672");

    ResponseEntity<String> entity = new CustomRestTemplate()
        .postForEntity("http://127.0.0.1:8080/stopcar_springmvc/v1/notify_lzf.php", param,
            String.class);
    System.err.println(entity.getStatusCode());
    System.err.println(entity.getBody());

  }

  @Test
  public void getNotify() {
    String content = "POSID=426295203&BRANCHID=510000000&ORDERID=2017041818365200&PAYMENT=2.30&CURCODE=01&REMARK1=&REMARK2=&ACC_TYPE=06&SUCCESS=Y&TYPE=1&REFERER=&CLIENTIP=222.211.204.87&SIGN=32d9abd7eecee879ea7aa8c65a872bfcb2bed7b7d61f2048ef9088c64eee565a4c093d6ff821686e6983f07ceb7b7155bbf61fc898f5aec46fd057862e4d39a2206ac56c694c937ec870008fe0daf172445a79914fe06533f58fedf50b2732ce0d4075110ab3294735706fa64dbba4dc9720baa7b6397c44b67239dc17a61672";
    ResponseEntity<String> entity = new CustomRestTemplate()
        .getForEntity("http://127.0.0.1:8080/stopcar_springmvc/v1/notify_lzf.php?" + content,
            String.class);
    System.err.println(entity.getStatusCode());
    System.err.println(entity.getBody());
  }

}
