package cn.bestwu.pay.payment.loongpaycheck;

import cn.bestwu.test.client.CustomRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class CcbAccountQry1 {

  //@SuppressWarnings("unchecked")
  public static void main(String[] args) throws Exception {
    String MERCHANTID = "105510548160013";
    String BRANCHID = "510000000";                 //分行代码
    String POSID = "426295203";                    //柜台号
    String ORDERDATE = "20170419";                  //订单日期
    String BEGORDERTIME = "00:00:00";
    String ENDORDERTIME = "23:59:59";
    String BEGORDERID = "";
    String ENDORDERID = "";
    String QUPWD = "WBO123";
    String TXCODE = "410408";
    String SEL_TYPE = "3";
    String OPERATOR = "";

    String bankURL = "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain";
//        String bankURL="http://128.128.96.2:8001/app/ccbMain";

    String preSign =
        "MERCHANTID=" + MERCHANTID + "&BRANCHID=" + BRANCHID + "&POSID=" + POSID + "&ORDERDATE="
            + ORDERDATE + "&BEGORDERTIME=" + BEGORDERTIME + "&ENDORDERTIME=" + ENDORDERTIME
            + "&BEGORDERID=" + BEGORDERID + "&ENDORDERID=" + ENDORDERID + "&QUPWD=&TXCODE=" + TXCODE
            + "&SEL_TYPE=" + SEL_TYPE + "&OPERATOR=" + OPERATOR;

    System.out.println("-----" + DigestUtils.md5DigestAsHex(preSign.getBytes("UTF-8")));

    MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
    param.add("MERCHANTID", MERCHANTID);

    param.add("BRANCHID", BRANCHID);
    param.add("POSID", POSID);

    param.add("ORDERDATE", ORDERDATE);

    param.add("BEGORDERTIME", BEGORDERTIME);

    param.add("ENDORDERTIME", ENDORDERTIME);

    param.add("BEGORDERID", BEGORDERID);

    param.add("ENDORDERID", ENDORDERID);

    param.add("QUPWD", QUPWD);

    param.add("TXCODE", TXCODE);

    param.add("SEL_TYPE", SEL_TYPE);

    param.add("OPERATOR", OPERATOR);

    param.add("MAC", DigestUtils.md5DigestAsHex(preSign.getBytes("UTF-8")));

    String useragent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.USER_AGENT, useragent);
    CustomRestTemplate customRestTemplate = new CustomRestTemplate();
    customRestTemplate.setPrint(true);
    ResponseEntity<String> entity = customRestTemplate
        .postForEntity(bankURL, new HttpEntity<>(param, httpHeaders), String.class);

    System.out.println("ret::" + entity.getBody());

  }

}
