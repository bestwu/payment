package cn.bestwu.pay.payment.loongpaycheck;

import cn.bestwu.lang.util.StringUtil;
import cn.bestwu.pay.payment.loongpay.LoongQueryResult;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import org.springframework.http.MediaType;

/**
 * @author Peter Wu
 */
public class XmlTest {

  @Test
  public void test() throws Exception {
    LoongQueryResult loongQueryResult = new XmlMapper()
        .readValue("<?xml version = \"1.0\" encoding=\"UTF-8\" ?>\n"
            + "<DOCUMENT>\n"
            + "\t<RETURN_CODE>0130Z110W109</RETURN_CODE>\n"
            + "    <RETURN_MSG>您所在查询的IP地址有误。[您查询所在的IP地址是:222.211.204.25]</RETURN_MSG>\n"
            + "</DOCUMENT>", LoongQueryResult.class);

    System.err.println(StringUtil.valueOf(loongQueryResult));
  }

  @Test
  public void xml() throws Exception {
    System.err.println(MediaType.TEXT_HTML.includes(MediaType.TEXT_HTML));
  }
}
