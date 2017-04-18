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
  private String attach;

  @Override
  public boolean isCompleted() {
    return completed;
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
  public String getAttach() {
    return attach;
  }

  public void setAttach(String attach) {
    this.attach = attach;
  }

  @Override
  public Object getExtra(String key) {
    return null;
  }

  @Override
  public boolean isRefundCompleted() {
    return false;
  }
}
