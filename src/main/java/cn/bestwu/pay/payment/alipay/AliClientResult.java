package cn.bestwu.pay.payment.alipay;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 * <pre>
 * {
 *    "alipay_trade_app_pay_response": {
 *        "code": "10000",
 *        "msg": "Success",
 *        "app_id": "2014072300007148",
 *        "out_trade_no": "081622560194853",
 *        "trade_no": "2016081621001004400236957647",
 *        "total_amount": "0.01",
 *        "seller_id": "2088702849871851",
 *        "charset": "utf-8",
 *        "timestamp": "2016-10-11 17:43:36"
 *    },
 *    "sign": "NGfStJf3i3ooWBuCDIQSumOpaGBcQz+aoAqyGh3W6EqA/gmyPYwLJ2REFijY9XPTApI9YglZyMw+ZMhd3kb0mh4RAXMrb6mekX4Zu8Nf6geOwIa9kLOnw0IMCjxi4abDIfXhxrXyj********",
 *    "sign_type": "RSA2"
 * }
 * </pre>
 *
 * @author Peter Wu
 */
public class AliClientResult {

  @NotBlank
  @NotNull
  private String alipay_trade_app_pay_response;
  @NotBlank
  @NotNull
  private String sign;
  @NotBlank
  @NotNull
  private String sign_type;

  public String getAlipay_trade_app_pay_response() {
    return alipay_trade_app_pay_response;
  }

  public void setAlipay_trade_app_pay_response(String alipay_trade_app_pay_response) {
    this.alipay_trade_app_pay_response = alipay_trade_app_pay_response;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getSign_type() {
    return sign_type;
  }

  public void setSign_type(String sign_type) {
    this.sign_type = sign_type;
  }


}
