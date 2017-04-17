package cn.bestwu.pay.payment.weixinpay;

import cn.bestwu.lang.util.RandomUtil;
import cn.bestwu.lang.util.StringUtil;
import cn.bestwu.pay.payment.AbstractPay;
import cn.bestwu.pay.payment.Order;
import cn.bestwu.pay.payment.OrderHandler;
import cn.bestwu.pay.payment.PayException;
import cn.bestwu.pay.payment.PayType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

/**
 * 微信支付
 *
 * @author Peter Wu
 */
public class Weixinpay extends AbstractPay<WeixinpayProperties> {


  private final MapType mapType = TypeFactory.defaultInstance()
      .constructMapType(HashMap.class, String.class, String.class);
  private RestTemplate restTemplate = new RestTemplate();
  private MappingJackson2XmlHttpMessageConverter messageConverter;
  /**
   * 统一下单地址
   */
  private static final String UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
  /**
   * 订单查询地址
   */
  private static final String ORDERQUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";


  @Autowired
  public Weixinpay(WeixinpayProperties properties) {
    super(WEIXINPAY, properties);
    messageConverter = new MappingJackson2XmlHttpMessageConverter() {
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

  /**
   * @param params 通知返回来的参数数组
   * @param sign 比对的签名结果
   * @return 签名是否正确
   */
  private boolean verify(Map<String, String> params, String sign)
      throws UnsupportedEncodingException {
    return getSign(params).equals(sign);
  }

  /**
   * 对参数签名
   *
   * @param params 参数
   * @return 签名后字符串
   */
  private String getSign(Map<String, String> params) throws UnsupportedEncodingException {
    //获取待签名字符串
    List<String> keys = new ArrayList<>(params.keySet());
    Collections.sort(keys);

    StringBuilder prestr = new StringBuilder();

    for (int i = 0; i < keys.size(); i++) {
      String key = keys.get(i);
      String value = params.get(key);
      if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
        continue;
      }
      if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
        prestr.append(key).append("=").append(value);
      } else {
        prestr.append(key).append("=").append(value).append("&");
      }
    }
    //获得签名验证结果
    String stringSignTemp = prestr + "&key=" + properties.getApi_key();
    return DigestUtils.md5DigestAsHex(stringSignTemp.getBytes("UTF-8")).toUpperCase();
  }

  /**
   * 下单结果组装客户端调起支付所需信息
   *
   * @param map 下单结果
   * @return 客户端调起支付所需信息
   */
  private Map<String, String> getPayInfo(Map map) throws UnsupportedEncodingException {
    Map<String, String> result = new HashMap<>();
    result.put("appid", (String) map.get("appid"));
    result.put("partnerid", (String) map.get("mch_id"));
    result.put("prepayid", (String) map.get("prepayid"));
    result.put("package", "Sign=WXPay");
    result.put("noncestr", RandomUtil.nextString2(32));
    result.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

    result.put("sign", getSign(result));
    return result;
  }

  @Override
  public Object placeOrder(Order order, PayType payType) throws PayException {
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
   * 扫码下单
   *
   * @param order 订单
   * @return 下单结果
   */
  private String qrCodePlaceOrder(Order order) throws PayException {

    try {
      Map<String, String> params = new HashMap<>();
      params.put("appid", properties.getAppid());
      params.put("mch_id", properties.getMch_id());
      params.put("attach", order.getAttach());
      params.put("body", order.getBody());
      params.put("nonce_str", RandomUtil.nextString2(32));
      params.put("out_trade_no", order.getNo());
      params.put("total_fee", String.valueOf(order.getTotalAmount()));
      params.put("spbill_create_ip", order.getSpbillCreateIp());
      params.put("notify_url", getNotifyUrl());
      params.put("trade_type", "NATIVE");
      params.put("product_id", order.getNo());

      params.put("sign", getSign(params));

      @SuppressWarnings("unchecked")
      Map<String, String> entity = restTemplate.postForObject(UNIFIEDORDER_URL, params, Map.class);

      if (log.isDebugEnabled()) {
        log.debug(StringUtil.valueOf(entity));
      }
      if (verify(entity, entity.get("sign"))) {
        if ("SUCCESS".equals(entity.get("return_code"))) {
          if ("SUCCESS".equals(entity.get("result_code"))) {
            return entity.get("code_url");
          } else {
            String err_code_des = entity.get("err_code_des");
            throw new PayException(entity.get("err_code") + ":" + err_code_des);
          }
        } else {
          throw new PayException(entity.get("return_msg"));
        }
      } else {
        throw new PayException("下单失败");
      }
    } catch (Exception e) {
      throw new PayException("下单失败", e);
    }
  }

  /**
   * APP下单
   *
   * @param order 订单
   * @return 下单结果
   */
  private Map<String, String> appPlaceOrder(Order order) throws PayException {
    try {
      Map<String, String> params = new HashMap<>();
      params.put("appid", properties.getAppid());
      params.put("mch_id", properties.getMch_id());
      params.put("device_info", order.getDeviceInfo());
      params.put("nonce_str", RandomUtil.nextString2(32));
      params.put("body", order.getBody());
      params.put("out_trade_no", order.getNo());
      params.put("total_fee", String.valueOf(order.getTotalAmount()));
      params.put("spbill_create_ip", order.getSpbillCreateIp());
      params.put("notify_url", getNotifyUrl());
      params.put("trade_type", "APP");

      params.put("sign", getSign(params));

      @SuppressWarnings("unchecked")
      Map<String, String> entity = restTemplate.postForObject(UNIFIEDORDER_URL, params, Map.class);

      if (log.isDebugEnabled()) {
        log.debug(StringUtil.valueOf(entity));
      }
      if (verify(entity, entity.get("sign"))) {
        if ("SUCCESS".equals(entity.get("return_code"))) {
          if ("SUCCESS".equals(entity.get("result_code"))) {
            return getPayInfo(entity);
          } else {
            String err_code_des = entity.get("err_code_des");
            throw new PayException(entity.get("err_code") + ":" + err_code_des);
          }
        } else {
          throw new PayException(entity.get("return_msg"));
        }
      } else {
        throw new PayException("下单失败");
      }
    } catch (Exception e) {
      throw new PayException("下单失败", e);
    }
  }

  @Override
  public boolean checkOrder(Order order, OrderHandler orderHandler) {
    try {
      Map<String, String> params = new HashMap<>();
      params.put("appid", properties.getAppid());
      params.put("mch_id", properties.getMch_id());
      String out_trade_no = order.getNo();
      params.put("out_trade_no", out_trade_no);
      params.put("nonce_str", RandomUtil.nextString2(32));
      params.put("sign", getSign(params));

      @SuppressWarnings("unchecked")
      Map<String, String> entity = restTemplate.postForObject(ORDERQUERY_URL, params, Map.class);

      if (log.isDebugEnabled()) {
        log.debug(StringUtil.valueOf(entity));
      }
      if (verify(entity, entity.get("sign"))) {
        if ("SUCCESS".equals(entity.get("return_code"))) {
          if ("SUCCESS".equals(entity.get("result_code"))) {
            String mch_id = params.get("mch_id");
            String appid = params.get("appid");
            if (properties.getMch_id().equals(mch_id) && properties.getAppid()
                .equals(appid)) {
              if (out_trade_no.equals(params.get("out_trade_no"))) {
                int total_fee = Integer.parseInt(params.get("total_fee"));
                if (order.getTotalAmount() == total_fee) {
                  if (!order.isCompleted()) {
                    orderHandler.complete(order, getProvider());
                  }
                  return true;
                } else {
                  log.debug("查询失败，金额不匹配，服务器金额：{},本地订单金额：{}", total_fee, order.getTotalAmount());
                }
              } else {
                log.debug("查询失败，订单不匹配，响应订单号：{}，本地订单号：{}", params.get("out_trade_no"), out_trade_no);
              }
            } else {
              log.debug("查询失败，商户/应用不匹配,响应商户：{},本地商户：{},响应应用ID：{},本地应用ID：{}",
                  mch_id, properties.getMch_id(), appid, properties.getAppid());
            }
          } else {
            String err_code_des = entity.get("err_code_des");
            log.debug(entity.get("err_code") + ":" + err_code_des);
          }
        } else {
          log.debug(entity.get("return_msg"));
        }
      } else {
        log.debug("查询失败");
      }
    } catch (Exception e) {
      log.debug("查询失败", e);
    }
    return false;
  }

  @Override
  public Object payNotify(HttpServletRequest request, OrderHandler orderHandler) {
    try {
      HashMap<String, String> params = messageConverter.getObjectMapper()
          .readValue(request.getInputStream(), mapType);
      if (log.isInfoEnabled()) {
        log.info("微信支付收到的通知：{}", StringUtil.valueOf(params, true));
      }
      //交易状态
      if (verify(params, params.get("sign"))) {//验证成功
        if ("SUCCESS".equals(params.get("return_code"))) {
          if ("SUCCESS".equals(params.get("result_code"))) {
            String mch_id = params.get("mch_id");
            String appid = params.get("appid");
            if (properties.getMch_id().equals(mch_id) && properties.getAppid()
                .equals(appid)) {
              String out_trade_no = params.get("out_trade_no");
              int total_fee = Integer.parseInt(params.get("total_fee"));

              Order order = orderHandler.findByNo(out_trade_no);
              if (order != null && order.getTotalAmount() == total_fee) {
                if (!order.isCompleted()) {
                  orderHandler.complete(order, getProvider());
                  return new NotifyResult("SUCCESS");
                }
              } else {
                log.error("查询失败，金额不匹配，服务器金额：{},本地订单金额：{}", total_fee, order.getTotalAmount());
              }
            } else {
              log.error("查询失败，商户/应用不匹配,响应商户：{},本地商户：{},响应应用ID：{},本地应用ID：{}", mch_id,
                  properties.getMch_id(), appid, properties.getAppid());
            }
          } else {
            log.error("微信支付失败，{}", params.get("err_code_des"));
          }
        } else {
          log.error("微信支付失败，{}", params.get("return_msg"));
        }
      } else {
        log.error("微信支付通知签名验证不通过：{}", StringUtil.valueOf(params, true));
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return new NotifyResult("FAIL");
  }
}
