package cn.bestwu.pay.payment;

import cn.bestwu.pay.payment.PayConfiguration.PayNotifyController;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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
    Enumeration<String> parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String key = parameterNames.nextElement();
      params.put(key, request.getParameter(key));
    }
    return params;
  }

}
