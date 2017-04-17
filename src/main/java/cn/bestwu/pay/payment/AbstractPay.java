package cn.bestwu.pay.payment;

import cn.bestwu.pay.payment.PayConfiguration.PayNotifyController;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.util.StringUtils;

/**
 * 支付功能接口
 *
 * @author Peter Wu
 */
public abstract class AbstractPay<P extends PayProperties> implements Pay {

  protected final Logger log = LoggerFactory.getLogger(this.getClass());

  private final String provider;

  /**
   * 支付配置属性
   */
  protected P properties;

  public AbstractPay(String provider, P properties) {
    this.provider = provider;
    this.properties = properties;
  }

  @Override
  public String getProvider() {
    return provider;
  }


  /**
   * 支付结果回调接口
   *
   * @return 回调接口
   */
  protected String getNotifyUrl() throws NoSuchMethodException {
    String notifyUrl = properties.getNotifyUrl();
    if (StringUtils.hasText(notifyUrl)) {
      return notifyUrl;
    } else {
      String payNotify = ControllerLinkBuilder.linkTo(PayNotifyController.class,
          PayNotifyController.class.getMethod("payNotify", String.class, HttpServletRequest.class),
          provider).withSelfRel().getHref();
      if (log.isDebugEnabled()) {
        log.debug("回调地址：{}", payNotify);
      }
      return payNotify;
    }
  }

  /**
   * @param request 请求
   * @return 参数
   */
  protected Map<String, String> toParams(HttpServletRequest request) {
    Map<String, String> params = new HashMap<>();
    Map<String, String[]> parameterMap = request.getParameterMap();
    for (Entry<String, String[]> entry : parameterMap.entrySet()) {
      String[] values = entry.getValue();
      String valueStr = "";
      for (int i = 0; i < values.length; i++) {
        valueStr = (i == values.length - 1) ? valueStr + values[i]
            : valueStr + values[i] + ",";
      }
      //乱码解决，这段代码在出现乱码时使用。
      //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
      params.put(entry.getKey(), valueStr);
    }
    return params;
  }

}
