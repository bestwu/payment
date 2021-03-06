package cn.bestwu.pay.payment.alipay;

import cn.bestwu.lang.util.StringUtil;
import cn.bestwu.pay.payment.AbstractPay;
import cn.bestwu.pay.payment.Order;
import cn.bestwu.pay.payment.OrderHandler;
import cn.bestwu.pay.payment.PayException;
import cn.bestwu.pay.payment.PayType;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
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
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 支付宝
 *
 * @author Peter Wu
 */
public class Alipay extends AbstractPay<AliPayProperties> {

  private final String signType = "RSA2";
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private String charset = "UTF-8";
  private ObjectMapper objectMapper = new ObjectMapper();
  AlipayClient alipayClient;

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  @Autowired
  public Alipay(AliPayProperties properties) {
    super("alipay", properties);
    dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
        properties.getApp_id(), properties.getPrivateKey(), "json", charset,
        properties.getPublicKey(), signType);
  }

  @Override
  public Object placeOrder(Order order, PayType payType) throws PayException {
    if (order.isCompleted()) {
      throw new PayException("订单已支付");
    }
    switch (payType) {
      case APP:
        return appPlaceOrder(order);
      case QR_CODE:
        return qrCodePlaceOrder(order);
      default:
        throw new PayException("不支持的支付方式");
    }
  }

  /**
   * APP 下单
   *
   * @param order 订单
   * @return 下单结果
   */
  private String appPlaceOrder(Order order) throws PayException {
    try {
      String privateKey = properties.getPrivateKey();
      String app_id = properties.getApp_id();
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
        params.put("notify_url", getNotifyUrl());
      }
      {
        String seller_id = properties.getSeller_id();
        Map<String, String> biz_content = new HashMap<>();
        biz_content.put("subject", order.getSubject());
        biz_content.put("seller_id", seller_id);
        biz_content.put("body", order.getBody());
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
        log.debug("订单：{}订单参数字符串:{}", order.getNo(), orderStr.toString());
      }

      return orderStr.toString();
    } catch (Exception e) {
      throw new PayException("订单：" + order.getNo() + "下单失败", e);
    }
  }

  /**
   * @param order 订单
   * @return QrCode
   * @throws PayException PayException
   */
  private Object qrCodePlaceOrder(Order order) throws PayException {
    try {
      AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();//创建API对应的request类
      {
        String seller_id = properties.getSeller_id();
        Map<String, String> biz_content = new HashMap<>();
        biz_content.put("out_trade_no", order.getNo());
        String total_amount = new BigDecimal(order.getTotalAmount())
            .divide(new BigDecimal(100), 2, BigDecimal.ROUND_UNNECESSARY).toString();
        biz_content.put("total_amount", total_amount);
        biz_content.put("subject", order.getSubject());
        biz_content.put("seller_id", seller_id);
        biz_content.put("body", order.getBody());

        request.setBizContent(objectMapper.writeValueAsString(biz_content));
      }
      AlipayTradePrecreateResponse response = alipayClient.execute(request);
      if ("10000".equals(response.getCode()) && "10000".equals(response.getSubCode())) {
        return response.getQrCode();
      } else {
        throw new PayException("订单：" + order.getNo() + "下单失败，支付宝响应：" + response.getBody());
      }
    } catch (Exception e) {
      throw new PayException("订单：" + order.getNo() + "下单失败", e);
    }
  }

  @Override
  public boolean checkOrder(String orderNo, OrderHandler orderHandler) {
    Order order = orderHandler.findByNo(orderNo);
    try {
      if (order.isCompleted()) {
        return true;
      }
      AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
      String out_trade_no = order.getNo();
      request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"}");
      AlipayTradeQueryResponse response = alipayClient.execute(request);
      if ("10000".equals(response.getCode()) && "10000".equals(response.getSubCode())) {
        if ("TRADE_SUCCESS".equals(response.getTradeStatus())) {
          complete(order, orderHandler);
          return true;
        } else {
          log.error("支付未成功");
        }
      } else {
        log.error("订单：{}查询失败，支付宝响应：{}", order.getNo(), response.getBody());
      }
    } catch (Exception e) {
      log.error("订单：" + order.getNo() + "查询失败", e);
    }
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
   * @param request 回调请求
   * @param orderHandler 订单处理类
   * @return 异步通知响应
   */
  @Override
  public Object payNotify(HttpServletRequest request, OrderHandler orderHandler) {
    try {
      Map<String, String> params = toParams(request);
      if (log.isInfoEnabled()) {
        log.info("支付宝异步通知收到的通知：{}", StringUtil.valueOf(params, true));
      }
      if (AlipaySignature.rsaCheckV1(params, properties.getPublicKey(), charset, signType)) {
        if ("TRADE_SUCCESS".equals(params.get("trade_status"))) {
          String seller_id = params.get("seller_id");
          String app_id = params.get("app_id");
          String localSellerId = properties.getSeller_id();
          String localAppId = properties.getApp_id();
          if (localSellerId.equals(seller_id) && localAppId.equals(app_id)) {
            String out_trade_no = params.get("out_trade_no");
            Order order = orderHandler.findByNo(out_trade_no);
            if (order != null) {
              BigDecimal outTotalAmount = new BigDecimal(params.get("total_amount"))
                  .multiply(BigDecimal.valueOf(100));
              long totalAmount = order.getTotalAmount();
              if (new BigDecimal(totalAmount).equals(outTotalAmount)) {
                complete(order, orderHandler);
                return "success";
              } else {
                log.error(
                    "支付宝异步通知失败，金额不匹配，服务器金额：{}分,本地订单金额：{}", outTotalAmount, totalAmount);
              }
            } else {
              log.error(
                  "支付宝异步通知失败，不是系统订单：{}", StringUtil.valueOf(params, true));
            }
          } else {
            log.error(
                "支付宝异步通知失败，商户/应用不匹配,响应商户：{},本地商户：{},响应应用ID：{},本地应用ID：{}", seller_id,
                localSellerId, app_id, localAppId);
          }
        }
      } else {
        log.error("支付宝异步通知签名验证不通过：{}", StringUtil.valueOf(params, true));
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return "fail";
  }

  @Override
  public void refund(Order order, OrderHandler orderHandler) throws PayException {
    try {
      if (order.isRefundCompleted()) {
        throw new PayException("订单：" + order.getNo() + "已退款");
      }
      AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
      Map<String, String> biz_content = new HashMap<>();
      String out_trade_no = order.getNo();
      biz_content.put("out_trade_no", out_trade_no);
      String refundAmount = new BigDecimal(order.getRefundAmount())
          .divide(new BigDecimal(100), 2, BigDecimal.ROUND_UNNECESSARY).toString();
      biz_content.put("refund_amount", refundAmount);
      biz_content.put("out_request_no", order.getRefundNo());
      request.setBizContent(objectMapper.writeValueAsString(biz_content));
      AlipayTradeRefundResponse response = alipayClient.execute(request);
      if ("10000".equals(response.getCode()) && "10000".equals(response.getSubCode())) {
        orderHandler.refund(order, getProvider());
      } else {
        throw new PayException("订单：" + order.getNo() + "退款失败，支付宝响应：" + response.getBody());
      }
    } catch (Exception e) {
      throw new PayException("订单：" + order.getNo() + "退款失败", e);
    }
  }

  @Override
  public boolean refundQuery(String orderNo, OrderHandler orderHandler) {
    Order order = orderHandler.findByNo(orderNo);
    try {
      if (order.isRefundCompleted()) {
        return true;
      }
      AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
      Map<String, String> biz_content = new HashMap<>();
      biz_content.put("out_trade_no", orderNo);
      String refundNo = order.getRefundNo();
      biz_content.put("out_request_no", refundNo);
      request.setBizContent(objectMapper.writeValueAsString(biz_content));
      ////该接口的返回码10000，仅代表本次查询操作成功，不代表退款成功。如果该接口返回了查询数据，则代表退款成功，如果没有查询到则代表未退款成功，可以调用退款接口进行重试。
      AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
      if ("10000".equals(response.getCode()) && "10000".equals(response.getSubCode())) {
        String outTradeNo = response.getOutTradeNo();
        String outRequestNo = response.getOutRequestNo();
        if (orderNo.equals(outTradeNo) && refundNo
            .equals(outRequestNo)) {
          BigDecimal response_refund_fee = new BigDecimal(response.getRefundAmount());
          long totalAmount = order.getTotalAmount();
          long refundAmount = order.getRefundAmount();
          BigDecimal outTotalAmount = new BigDecimal(response.getTotalAmount())
              .multiply(BigDecimal.valueOf(100));
          BigDecimal outRefundFee = response_refund_fee.multiply(BigDecimal.valueOf(100));
          if (order != null && new BigDecimal(refundAmount).equals(outRefundFee) && new BigDecimal(
              totalAmount).equals(outTotalAmount)) {
            if (!order.isRefundCompleted()) {
              orderHandler.refundComplete(order, getProvider());
            }
            return true;
          } else {
            log.error(
                "订单：{}退款失败，金额不匹配，服务器金额：{}/{} 分,本地订单金额：{}/{} 分", orderNo,
                outTotalAmount, outRefundFee, totalAmount, refundAmount);
          }
        } else {
          log.error(
              "订单：{}退款查询失败，订单不匹配，服务器订单号：{}/{},本地订单号：{}/{}", orderNo, outTradeNo,
              outRequestNo, orderNo, refundNo);
        }
      } else {
        log.error("订单：{}退款查询失败,{}", orderNo, response.getBody());
      }
    } catch (Exception e) {
      log.error("订单：" + orderNo + "退款查询失败", e);
    }
    return false;
  }


  /**
   * 验证客户端结果
   *
   * @param result 客户端结果
   * @return 是否合法
   * @throws AlipayApiException 验证异常
   */
  public boolean checkClientResult(AliClientResult result, OrderHandler orderHandler)
      throws AlipayApiException {
    try {
      if (AlipaySignature
          .rsaCheck(result.getAlipay_trade_app_pay_response(), result.getSign(),
              properties.getPublicKey(), charset,
              result.getSign_type())) {
        PayResponse payResponse = objectMapper
            .readValue(result.getAlipay_trade_app_pay_response(), PayResponse.class);
        if ("10000".equals(payResponse.getCode())) {
          String localSellerId = properties.getSeller_id();
          String seller_id = payResponse.getSeller_id();
          String localAppId = properties.getApp_id();
          String app_id = payResponse.getApp_id();
          if (localSellerId.equals(seller_id) && localAppId.equals(app_id)) {
            Order order = orderHandler.findByNo(payResponse.getOut_trade_no());
            if (order != null) {
              BigDecimal outTotalAmount = new BigDecimal(payResponse.getTotal_amount())
                  .multiply(BigDecimal.valueOf(100));
              long totalAmount = order.getTotalAmount();
              if (new BigDecimal(totalAmount).equals(outTotalAmount)) {
                complete(order, orderHandler);
                return true;
              } else {
                log.error(
                    "支付宝异步通知失败，金额不匹配，服务器金额：{}分,本地订单金额：{}", outTotalAmount, totalAmount);
              }
            } else {
              log.error(
                  "支付宝异步通知失败，不是系统订单：{}", StringUtil.valueOf(result, true));
            }
          } else {
            log.error(
                "支付宝异步通知失败，商户/应用不匹配,响应商户：{},本地商户：{},响应应用ID：{},本地应用ID：{}", seller_id,
                localSellerId, app_id, localAppId);
          }
        }
      }
    } catch (Exception e) {
      log.error("客户端结果验证失败：" + StringUtil.valueOf(result), e);
    }
    return false;
  }

}
