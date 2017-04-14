package cn.bestwu.pay.payment.alipay;

import cn.bestwu.lang.util.StringUtil;
import cn.bestwu.pay.payment.AbstractPay;
import cn.bestwu.pay.payment.Order;
import cn.bestwu.pay.payment.OrderHandler;
import cn.bestwu.pay.payment.PayException;
import cn.bestwu.pay.payment.PayMode;
import cn.bestwu.pay.payment.PayType;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Peter Wu
 */
public class Alipay extends AbstractPay<AliPayProperties> {

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private String charset = "UTF-8";
  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  public Alipay(AliPayProperties properties) {
    super(properties);
    dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
  }

  /**
   * 参数验证
   *
   * @param params 参数
   * @return 是否合法
   * @throws AlipayApiException AlipayApiException
   */
  public boolean checkSign(Map<String, String> params) throws AlipayApiException {
    String publicKey = getProperties().getPublicKey();
    return AlipaySignature.rsaCheckV2(params, publicKey, charset);
  }

  @Override
  public Object placeOrder(Order order, PayType payType) {
    switch (payType) {
      case APP:
        return appPlaceOrder(order);
      case SCAN_CODE:
      default:
        throw new PayException("不支持的支付方式");
    }
  }

  @Override
  public boolean checkOrder(Order order, OrderHandler orderHandler) {
    return false;
  }

  /**
   * 支付宝服务器异步通知页面
   *
   * 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
   *
   * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
   *
   * 3、校验通知中的seller_id（或者seller_email)
   * 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
   *
   * 4、验证app_id是否为该商户本身。
   *
   * 上述1、2、3、4有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
   *
   * 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
   *
   * 在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
   *
   * @param params 回调参数
   * @param orderHandler 订单处理类
   * @return 异步通知响应
   */
  @Override
  public Object payNotify(Map<String, String> params, OrderHandler orderHandler) {
    try {
      if (log.isInfoEnabled()) {
        log.info("alipay异步通知收到的通知：{}", StringUtil.valueOf(params, true));
      }
      if (checkSign(params)) {
        String trade_status = params.get("trade_status");
        String seller_id = params.get("seller_id");
        String app_id = params.get("app_id");
        if ("TRADE_SUCCESS".equals(trade_status)) {
          if (getProperties().getSeller_id()
              .equals(seller_id) && getProperties().getApp_id().equals(app_id)) {
            String out_trade_no = params.get("out_trade_no");
            Order order = orderHandler.findByNo(out_trade_no);
            BigDecimal total_amount = new BigDecimal(params.get("trade_status"));
            if (order != null && new BigDecimal(order.getTotalAmount())
                .equals(total_amount.multiply(BigDecimal.valueOf(100)))) {
              if (!order.isComplete()) {
                orderHandler.complete(order);
                return "success";
              }
            } else {
              log.error("alipay异步通知，不是系统订单：{}", StringUtil.valueOf(params, true));
            }
          } else {
            log.error("alipay异步通知，不是系统订单：{}", StringUtil.valueOf(params, true));
          }
        }
      } else {
        log.error("alipay异步通知签名验证不通过：{}", StringUtil.valueOf(params, true));
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return "fail";
  }

  /**
   * APP 下单
   *
   * @param order 订单
   * @return 下单结果
   */
  private String appPlaceOrder(Order order) {
    try {
      String privateKey = getProperties().getPrivateKey();
      String app_id = getProperties().getApp_id();
      String method = "alipay.trade.app.pay";
      Map<String, String> params = new HashMap<>();
      {//公共参数
        params.put("app_id", app_id);
        params.put("method", method);
        params.put("charset", charset);
        params.put("sign_type", "RSA2");
        params.put("timestamp",
            dateFormat.format(new Date(order.getCurrentTimeMillis())));//2014-07-24 03:07:50
        params.put("version", "1.0");
        params.put("notify_url", getNotifyUrl(PayMode.ALIPAY));
      }
      {
        String seller_id = getProperties().getSeller_id();
        Map<String, String> biz_content = new HashMap<>();
        biz_content.put("subject", order.getSubject());
        biz_content.put("seller_id", seller_id);
        biz_content.put("body", order.getSubject());
        biz_content.put("out_trade_no", order.getNo());
        String total_amount = new BigDecimal(order.getTotalAmount())
            .divide(new BigDecimal(100), 2, BigDecimal.ROUND_UNNECESSARY).toString();
        biz_content.put("total_amount", total_amount);
        biz_content.put("product_code", "QUICK_MSECURITY_PAY");

        params.put("biz_content", objectMapper.writeValueAsString(biz_content));
      }

      if (log.isDebugEnabled()) {
        log.debug("参数：{}", StringUtil.valueOf(params));
      }

      List<String> keys = new ArrayList<>(params.keySet());
      Collections.sort(keys);
      StringBuilder orderStr = new StringBuilder("");
      for (String key : keys) {
        orderStr.append(key).append("=").append(URLEncoder.encode(params.get(key), charset))
            .append("&");
      }
      orderStr.append("sign").append("=")
          .append(URLEncoder.encode(AlipaySignature.rsaSign(params, privateKey, charset), charset));

      if (log.isDebugEnabled()) {
        log.debug("订单参数字符串:{}", orderStr.toString());
      }

      return orderStr.toString();
    } catch (Exception e) {
      throw new RuntimeException("下单失败", e);
    }
  }

  /**
   * 验证客户端结果
   *
   * @param result 客户端结果
   * @return 是否合法
   * @throws AlipayApiException 验证异常
   */
  public boolean checkClientResult(AliClientResult result) throws AlipayApiException {
    String publicKey = getProperties().getPublicKey();
    return AlipaySignature
        .rsaCheck(result.getAlipay_trade_app_pay_response(), result.getSign(), publicKey, charset,
            result.getSign_type());
  }

}
