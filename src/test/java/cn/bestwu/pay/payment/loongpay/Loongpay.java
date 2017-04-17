package cn.bestwu.pay.payment.loongpay;

import CCBSign.RSASig;
import cn.bestwu.lang.util.EscapeUtil;
import cn.bestwu.lang.util.StringUtil;
import cn.bestwu.pay.payment.AbstractPay;
import cn.bestwu.pay.payment.Order;
import cn.bestwu.pay.payment.OrderHandler;
import cn.bestwu.pay.payment.PayException;
import cn.bestwu.pay.payment.PayType;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * 龙支付
 *
 * @author Peter Wu
 */
@Component
@EnableConfigurationProperties(LoongpayProperties.class)
public class Loongpay extends AbstractPay<LoongpayProperties> {

  private RSASig rsaSig = new RSASig();

  @Autowired
  public Loongpay(LoongpayProperties properties) {
    super("loongpay", properties);
  }

  @PostConstruct
  public void init() {
    rsaSig.setPublicKey(properties.getPub());
  }

  /**
   * 参与签名运算的字符串及其顺序如下： 请注意ACC_TYPE、ACCDATE、USRMSG、INSTALLNUM、ERRMSG、USRINFO只有在满足条件的情况下才参与签名。
   * POSID=000000000&BRANCHID=110000000&ORDERID=19991101234&PAYMENT=500.00&CURCODE=01&REMARK1=&REMARK2=&
   * ACC_TYPE=12&SUCCESS=Y&TYPE=1&REFERER=http://www.ccb.com/index.jsp&CLIENTIP=172.0.0.1&
   * ACCDATE=20100907&USRMSG=T4NJx%2FVgocRsLyQnrMZLyuQQkFzMAxQjdqyzf6pM%2Fcg%3D&INSTALLNUM=3&ERRMSG=&USRINFO=
   * T4NJx%2FVgocRsLyQnrMZLyuQQkFzMAxQjdqyzf6pM%2Fcg%3D
   *
   * 注：字符串中变量名必须是大写字母。
   *
   * @param params 通知返回来的参数数组
   * @param sign 比对的签名结果
   * @return 签名是否正确
   */
  private boolean verify(Map<String, String> params, String sign) {
    String macStr =
        "&POSID=" + params.get("POSID") +
            "&BRANCHID=" + params.get("BRANCHID") +
            "&ORDERID=" + params.get("ORDERID") +
            "&PAYMENT=" + params.get("PAYMENT") +
            "&CURCODE=" + params.get("CURCODE") +
            "&REMARK1=" + params.get("REMARK1") +
            "&REMARK2=" + params.get("REMARK2");
    String ACC_TYPE = params.get("ACC_TYPE");
    if (ACC_TYPE != null) {
      macStr += "&ACC_TYPE=" + ACC_TYPE;
    }
    macStr += "&SUCCESS=" + params.get("SUCCESS") +
        "&TYPE=" + params.get("TYPE") +
        "&REFERER=" + params.get("REFERER") +
        "&CLIENTIP=" + params.get("CLIENTIP");
    String ACCDATE = params.get("ACCDATE");
    if (ACCDATE != null) {
      macStr += "&ACCDATE=" + ACCDATE;
    }
    String USRMSG = params.get("USRMSG");
    if (USRMSG != null) {
      macStr += "&USRMSG=" + USRMSG;
    }
    String INSTALLNUM = params.get("INSTALLNUM");
    if (INSTALLNUM != null) {
      macStr += "&INSTALLNUM=" + INSTALLNUM;
    }
    String ERRMSG = params.get("ERRMSG");
    if (ERRMSG == null) {
      ERRMSG = "";
    }
    macStr += "&ERRMSG=" + ERRMSG;
    String USRINFO = params.get("USRINFO");
    if (USRINFO != null) {
      macStr += "&USRINFO=" + USRINFO;
    }
    return rsaSig.verifySigature(sign, macStr);
  }

  /**
   * 2)	USRMSG字段说明：
   * 使用对称加密算法对“户名|账号”进行加密后通过商户通知接口传送到商户系统，对称加密的密钥为商户公钥后30位。请商户妥善管理商户公钥。
   * eg.
   * 密钥：48060ab8d0a827b9adba32d9020111
   * 原串：建设银行|4367888888888888888
   * 加密串：T4NJx%2FVgocRsLyQnrMZLyuQQkFzMAxQjdqyzf6pM%2Fcg%3D
   * 使用MCipherDecode.java类中的getDecodeString(String urlString)方法进行解密，主要步骤如下：
   * MCipherDecode mcd = new MCipherDecode(key);//设置密钥
   * decodedString = mcd.getDecodeString(cipherdURL);//解密
   * byte[] tempByte = decodedString.getBytes("ISO-8859-1");
   * String a = new String(tempByte,"GBK"); //进行字符转码
   *
   * 我行系统编码为ISO-8859-1，商户处理时请注意编码问题。
   *
   * @param msg 字符
   * @return 解密后的字符
   */
  private String decode(String msg)
      throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, NoSuchProviderException, IllegalBlockSizeException {
    String pub = properties.getPub();
    pub = pub.substring(pub.length() - 30, pub.length());
    String decodedString = MCipherDecode.getDecodeString(msg, pub);
    byte[] tempByte = decodedString.getBytes("ISO-8859-1");
    return new String(tempByte, "UTF-8");
  }


  @Override
  public Object placeOrder(Order order, PayType payType) throws PayException {
    try {
      String pub = properties.getPub();
      pub = pub.substring(pub.length() - 30, pub.length());
      String total_amount = new BigDecimal(order.getTotalAmount())
          .divide(new BigDecimal(100), 2, BigDecimal.ROUND_UNNECESSARY).toString();

      String attach = order.getAttach();
      if (attach == null) {
        attach = "";
      }
      String subject = order.getSubject();
      if (subject == null) {
        subject = "";
      }
      String macStr = "MERCHANTID=" + properties.getMerchantid() +
          "&POSID=" + properties.getPosid() +
          "&BRANCHID=" + properties.getBranchid() +
          "&ORDERID=" + order.getNo() +
          "&PAYMENT=" + total_amount +
          "&CURCODE=" + "01" +
          "&TXCODE=" + properties.getTxcode() +
          "&REMARK1=" + attach +
          "&REMARK2=" + "" +
          "&TYPE=" + properties.getType() +
          "&PUB=" + pub +
          "&GATEWAY=" + "W1" +
          "&CLIENTIP=" + "" +
          "&REGINFO=" + "" +
          "&PROINFO=" + EscapeUtil.escape(subject)
          + "&REFERER=" + "";

      StringBuilder orderStr = new StringBuilder("https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain?");
      orderStr.append("MERCHANTID=").append(properties.getMerchantid())
          .append("&POSID=").append(properties.getPosid())
          .append("&BRANCHID=").append(properties.getBranchid())
          .append("&ORDERID=").append(order.getNo())
          .append("&PAYMENT=").append(total_amount)//金额
          .append("&CURCODE=").append("01")
          .append("&TXCODE=").append(properties.getTxcode())
          .append("&REMARK1=").append(attach)
          .append("&REMARK2=").append("")
          .append("&TYPE=").append(properties.getType())
          .append("&GATEWAY=").append("W1")
          .append("&CLIENTIP=").append("")
          .append("&REGINFO=").append("")
          .append("&PROINFO=").append(EscapeUtil.escape(subject))
          .append("&REFERER=").append("")
          //			.append(	+"&INSTALLNUM=") .append("")
          //        .append("&THIRDAPPINFO=").append("")
          //        .append("&TIMEOUT=").append("");
          //    String ISSINSCODE="UnionPay";
          .append("&MAC=").append(DigestUtils.md5DigestAsHex(macStr.getBytes("UTF-8")));

      if (log.isDebugEnabled()) {
        log.debug("订单参数字符串{}", orderStr.toString());
      }
      return orderStr.toString();
    } catch (Exception e) {
      throw new PayException("下单失败", e);
    }
  }

  @Override
  public boolean checkOrder(Order order, OrderHandler orderHandler) {
    return false;
  }

  @Override
  public Object payNotify(HttpServletRequest request, OrderHandler orderHandler) {
    try {
      Map<String, String> params = toParams(request);
      if (log.isInfoEnabled()) {
        log.info("龙支付收到的通知：{}", StringUtil.valueOf(params, true));
      }
      //交易状态
      if (verify(params, params.get("SIGN"))) {
        if ("Y".equalsIgnoreCase(params.get("SUCCESS"))) {
          String POSID = params.get("POSID");
          String BRANCHID = params.get("BRANCHID");
          if (properties.getPosid().equals(POSID) && properties.getBranchid()
              .equals(BRANCHID)) {
            //验证成功  更新该订单的支付状态 并把对应的金额添加给用户
            String orderid = params.get("ORDERID");
            //金额
            double PAYMENT = Double.parseDouble(params.get("PAYMENT"));
            long money = (long) (PAYMENT * 100);
            Order order = orderHandler.findByNo(orderid);
            if (order != null && order.getTotalAmount() == money) {
              if (!order.isCompleted()) {
                orderHandler.complete(order, getProvider());
                return "success";
              }
            } else {
              log.error("龙支付异步通知，不是系统订单：{}", StringUtil.valueOf(params, true));
            }
          } else {
            log.error("龙支付异步通知，不是系统订单：{}", StringUtil.valueOf(params, true));
          }
        } else {
          log.error("龙支付失败，{}", params.get("ERRMSG"));
        }
      } else {
        log.error("龙支付通知签名验证不通过：{}", StringUtil.valueOf(params, true));
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return "error";
  }

}
