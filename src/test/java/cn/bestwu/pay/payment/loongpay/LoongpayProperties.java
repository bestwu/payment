package cn.bestwu.pay.payment.loongpay;

import cn.bestwu.pay.payment.PayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "loongpay")
public class LoongpayProperties extends PayProperties {

  /**
   * 商户代码
   */
  private String merchantid;
  /**
   * 商户柜台代码
   */
  private String posid;
  /**
   * 分行代码
   */
  private String branchid;
  /**
   * 接口类型 0- 非钓鱼接口 1- 防钓鱼接口 目前该字段以银行开关为准，如果有该字段则需要传送以下字段。
   */
  private String type;
  /**
   * 公钥
   */
  private String pub;
  /**
   * 查询密码
   */
  private String qupwd;

  public String getMerchantid() {
    return merchantid;
  }

  public void setMerchantid(String merchantid) {
    this.merchantid = merchantid;
  }

  public String getPosid() {
    return posid;
  }

  public void setPosid(String posid) {
    this.posid = posid;
  }

  public String getBranchid() {
    return branchid;
  }

  public void setBranchid(String branchid) {
    this.branchid = branchid;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getQupwd() {
    return qupwd;
  }

  public void setQupwd(String qupwd) {
    this.qupwd = qupwd;
  }

  public String getPub() {
    return pub;
  }

  public void setPub(String pub) {
    this.pub = pub;
  }
}