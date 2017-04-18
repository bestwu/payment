package cn.bestwu.pay.payment;

/**
 * 订单
 *
 * @author Peter Wu
 */
public interface Order {

  /**
   * @return 订单是否完成
   */
  boolean isCompleted();

  /**
   * @return 订单号
   */
  String getNo();

  /**
   * @return 订单标题
   */
  String getSubject();

  /**
   * @return 订单详情
   */
  String getBody();

  /**
   * @return 订单金额 （单位：分）
   */
  long getTotalAmount();

  /**
   * @return 需要退款的金额，该金额不能大于订单金（单位：分）
   */
  long getRefundAmount();

  /**
   * @return 退款唯一标识
   */
  String getRefundNo();

  /**
   * @return 下单客户端IP
   */
  String getSpbillCreateIp();

  /**
   * @return 下单时间
   */
  long getCurrentTimeMillis();

  /**
   * @return 下单设备信息
   */
  String getDeviceInfo();

  /**
   * @return 附加数据
   */
  String getAttach();

  /**
   * @param key 字段名
   * @return 扩展字段值
   */
  Object getExtra(String key);

  /**
   * @return 退款是否完成
   */
  boolean isRefundCompleted();

}
