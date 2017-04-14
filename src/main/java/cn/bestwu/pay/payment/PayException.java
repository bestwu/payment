package cn.bestwu.pay.payment;

/**
 * 支付异常
 * @author Peter Wu
 */
public class PayException extends RuntimeException {

  private static final long serialVersionUID = 7499338250249815672L;

  public PayException(String message) {
    super(message);
  }

  public PayException(String message, Throwable cause) {
    super(message, cause);
  }
}
