package cn.bestwu.pay.payment.loongpaycheck;

import java.util.HashMap;
import java.util.Map;

public class CcbAccountQry {

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
    String TXCODE = "520100";
    String SEL_TYPE = "3";
    String OPERATOR = "";

    String bankURL = "https://ibsbjstar.ccb.com.cn/app/ccbMain";
//        String bankURL="http://128.128.96.2:8001/app/ccbMain";

    String param =
        "MERCHANTID=" + MERCHANTID + "&BRANCHID=" + BRANCHID + "&POSID=" + POSID + "&ORDERDATE="
            + ORDERDATE + "&BEGORDERTIME=" + BEGORDERTIME + "&ENDORDERTIME=" + ENDORDERTIME
            + "&BEGORDERID=" + BEGORDERID + "&ENDORDERID=" + ENDORDERID + "&QUPWD=&TXCODE=" + TXCODE
            + "&SEL_TYPE=" + SEL_TYPE + "&OPERATOR=" + OPERATOR;

    System.out.println("-----" + MD5.md5Str(param));

    Map<String, String> map = new HashMap<>();
    map.put("MERCHANTID", MERCHANTID);

    map.put("BRANCHID", BRANCHID);
    map.put("POSID", POSID);

    map.put("ORDERDATE", ORDERDATE);

    map.put("BEGORDERTIME", BEGORDERTIME);

    map.put("ENDORDERTIME", ENDORDERTIME);

    map.put("BEGORDERID", BEGORDERID);

    map.put("ENDORDERID", ENDORDERID);

    map.put("QUPWD", QUPWD);

    map.put("TXCODE", TXCODE);

    map.put("SEL_TYPE", SEL_TYPE);

    map.put("OPERATOR", OPERATOR);

    map.put("MAC", MD5.md5Str(param));

    String ret = HttpClientUtil.httpPost(bankURL, map);

    System.out.println("ret::" + ret);

  }

}
