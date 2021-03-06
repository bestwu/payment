package cn.bestwu.pay.payment;

/**
 * @author Peter Wu
 */
public class MyOrder implements Order {

  private boolean completed;
  private String no;
  private String subject;
  private String body;
  private long totalAmount;
  private String spbillCreateIp;
  private long currentTimeMillis;
  private String deviceInfo;
  private String payProvider;

  @Override
  public boolean isCompleted() {
    return completed;
  }

  @Override
  public void setException(String exception) {

  }

  @Override
  public String getPayProvider() {
    return payProvider;
  }

  @Override
  public void setPayProvider(String payProvider) {
    this.payProvider = payProvider;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  @Override
  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  @Override
  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Override
  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public long getTotalAmount() {
    return totalAmount;
  }

  @Override
  public long getRefundAmount() {
    return 0;
  }

  @Override
  public String getRefundNo() {
    return null;
  }

  public void setTotalAmount(long totalAmount) {
    this.totalAmount = totalAmount;
  }

  @Override
  public String getSpbillCreateIp() {
    return spbillCreateIp;
  }

  public void setSpbillCreateIp(String spbillCreateIp) {
    this.spbillCreateIp = spbillCreateIp;
  }

  @Override
  public long getCurrentTimeMillis() {
    return currentTimeMillis;
  }

  public void setCurrentTimeMillis(long currentTimeMillis) {
    this.currentTimeMillis = currentTimeMillis;
  }

  @Override
  public String getDeviceInfo() {
    return deviceInfo;
  }

  public void setDeviceInfo(String deviceInfo) {
    this.deviceInfo = deviceInfo;
  }

  @Override
  public boolean isRefundCompleted() {
    return false;
  }
}
