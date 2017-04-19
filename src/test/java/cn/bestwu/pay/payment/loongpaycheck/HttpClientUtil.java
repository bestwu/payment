package cn.bestwu.pay.payment.loongpaycheck;


import java.io.IOException;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpClientUtil {

  public static final Log logger = LogFactory.getLog("httpclient");


  public static String httpReader(String url, String code) {
    logger.info("GetPage:" + url);

    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod(url);

    String result = null;
    try {
      client.executeMethod(method);
      int status = method.getStatusCode();
      if (status == HttpStatus.SC_OK) {
        result = method.getResponseBodyAsString();
      } else {
        logger.info("Method failed: " + method.getStatusLine());
      }
    } catch (HttpException e) {
      // 发生致命的异常，可能是协议不对或者返回的内容有问题
      logger.info("Please check your provided http address!");
      e.printStackTrace();
    } catch (IOException e) {
      // 发生网络异常
      logger.info("发生网络异常！");
      e.printStackTrace();
    } finally {
      // 释放连接
      if (method != null) {
        method.releaseConnection();
      }
    }
    return result;
  }

  public static String httpPost(String url, Map paramMap, String code) {
    logger.info("GetPage:" + url);
    String content = null;
    if (url == null || url.trim().length() == 0 || paramMap == null
        || paramMap.isEmpty()) {
      return null;
    }
    HttpClient httpClient = new HttpClient();
    //设置header
    httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
        "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");//

    //代理设置
    //httpClient.getHostConfiguration().setProxy("128.128.176.74", 808);

    PostMethod method = new PostMethod(url);

    for (Object o1 : paramMap.keySet()) {
      String key = o1 + "";
      Object o = paramMap.get(key);
      if (o != null && o instanceof String) {
        method.addParameter(new NameValuePair(key, o.toString()));
      }
      if (o != null && o instanceof String[]) {
        String[] s = (String[]) o;
        if (s != null) {
          for (String value : s) {
            method.addParameter(new NameValuePair(key, value));
          }
        }
      }
    }
    try {

      int statusCode = httpClient.executeMethod(method);

      System.out.println("httpClientUtils::statusCode=" + statusCode);

      logger.info(method.getStatusLine());
      content = new String(method.getResponseBody(), code);

    } catch (Exception e) {
      logger.info("time out");
      e.printStackTrace();
    } finally {
      if (method != null) {
        method.releaseConnection();
      }
    }
    return content;

  }

  public static String httpPost(String url, Map paramMap) {
    //编码：UTF-8
    return HttpClientUtil.httpPost(url, paramMap, "UTF-8");
  }
}