package cn.bestwu.pay.payment.loongpay;

import java.math.BigDecimal;

/**
 * 例子：
 * <pre>
 * <RETURN_CODE>交易返回码，成功时总为000000</RETURN_CODE>
 * <RETURN_MSG>交易返回提示信息，成功时为空</RETURN_MSG>
 * <CURPAGE>当前页</CURPAGE>
 * <PAGECOUNT>总页数</PAGECOUNT>
 * <TOTAL>总笔数</TOTAL>
 * <PAYAMOUNT>支付总金额</PAYAMOUNT>
 * <REFUNDAMOUNT>退款总金额</REFUNDAMOUNT>
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
 *
 * </pre>
 *
 * @author Peter Wu
 */
public class LoongQueryResult {

  /**
   * 交易返回码，成功时总为000000
   */
  public String RETURN_CODE;
  /**
   * 交易返回提示信息，成功时为空
   */
  public String RETURN_MSG;
  /**
   * 当前页
   */
  public int CURPAGE;
  /**
   * 总页数
   */
  public int PAGECOUNT;
  /**
   * 总笔数
   */
  public int TOTAL;
  /**
   * 支付总金额
   */
  public BigDecimal PAYAMOUNT;
  /**
   * 退款总金额
   */
  public BigDecimal REFUNDAMOUNT;

  /**
   * 订单记录
   */
  public QueryOrder QUERYORDER;

  /**
   * @return 查询是否成功
   */
  public boolean isSuccess() {
    return "000000".equals(RETURN_CODE);
  }
}
