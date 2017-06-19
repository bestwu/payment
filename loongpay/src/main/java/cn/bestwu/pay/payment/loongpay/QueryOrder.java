package cn.bestwu.pay.payment.loongpay;

import CCBSign.RSASig;
import java.math.BigDecimal;

/**
 * 例子：
 * <pre>
 * <QUERYORDER>
 * <MERCHANTID>商户代码</MERCHANTID>
 * <BRANCHID>商户所在分行</BRANCHID>
 * <POSID>商户的POS号</POSID>
 * <ORDERID>订单号</ORDERID>
 * <ORDERDATE>支付/退款交易时间</ORDERDATE>
 * <ACCDATE>记账日期</ACCDATE>
 * <AMOUNT>支付金额</AMOUNT>
 * <STATUSCODE>支付/退款状态码</STATUSCODE>
 * <STATUS>支付/退款状态</STATUS>
 * <REFUND>退款金额</REFUND>
 * <SIGN>签名串</SIGN>
 * </QUERYORDER>
 * </pre>
 *
 * @author Peter Wu
 */
public class QueryOrder {

  /**
   * 商户代码
   */
  public String MERCHANTID;
  /**
   * 商户所在分行
   */
  public String BRANCHID;
  /**
   * 商户的POS号
   */
  public String POSID;
  /**
   * 订单号
   */
  public String ORDERID;
  /**
   * 支付/退款交易时间
   */
  public String ORDERDATE;
  /**
   * 记账日期
   */
  public String ACCDATE;
  /**
   * 支付金额
   */
  public BigDecimal AMOUNT;
  /**
   * 支付/退款状态码
   * XML页面形式返回中STATUSCODE和STATUS的说明
   * 支付流水共6中状态码和状态，他们之间一一对应
   * STATUSCODE：0      STATUS:失败
   * STATUSCODE：1      STATUS:成功
   * STATUSCODE：2      STATUS:待银行确认
   * STATUSCODE：3      STATUS:已部分退款
   * STATUSCODE：4      STATUS:已全额退款
   * STATUSCODE：5      STATUS:待银行确认
   *
   * 退款流水共4中状态码和状态，他们之间一一对应
   * STATUSCODE：0      STATUS:失败
   * STATUSCODE：1      STATUS:成功
   * STATUSCODE：2      STATUS:待银行确认
   * STATUSCODE：5      STATUS:待银行确认
   */
  public String STATUSCODE;
  /**
   * 支付/退款状态
   */
  public String STATUS;
  /**
   * 退款金额
   */
  public BigDecimal REFUND;
  /**
   * 签名串
   */
  public String SIGN;

  /**
   * @return 订单是否成功
   */
  public boolean isSuccess() {
    return "1".equals(STATUSCODE);
  }

  /**
   * @return 验签
   */
  public boolean verify(RSASig rsaSig) {
    //验签
    String pre =
        "MERCHANTID=" + MERCHANTID + "&BRANCHID=" + BRANCHID + "&POSID=" + POSID + "&ORDERID="
            + ORDERID + "&ORDERDATE=" + ORDERDATE + "&ACCDATE=" + ACCDATE + "&AMOUNT=" + AMOUNT +
            "&STATUSCODE=" + STATUSCODE + "&STATUS=" + STATUS + "&REFUND=" + REFUND;
    return rsaSig.verifySigature(SIGN, pre);
  }
}
