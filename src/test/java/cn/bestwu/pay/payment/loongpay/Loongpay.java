package cn.bestwu.pay.payment.loongpay;

import CCBSign.RSASig;
import cn.bestwu.lang.util.EscapeUtil;
import cn.bestwu.lang.util.StringUtil;
import cn.bestwu.pay.payment.AbstractPay;
import cn.bestwu.pay.payment.Order;
import cn.bestwu.pay.payment.OrderHandler;
import cn.bestwu.pay.payment.PayException;
import cn.bestwu.pay.payment.PayType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 龙支付
 *
 * @author Peter Wu
 */
@Component
@EnableConfigurationProperties(LoongpayProperties.class)
public class Loongpay extends AbstractPay<LoongpayProperties> {

  private RSASig rsaSig = new RSASig();
  private RestTemplate restTemplate = new RestTemplate();
  private XmlMapper xmlMapper = new XmlMapper();

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
    String preMacStr =
        "&POSID=" + params.get("POSID") +
            "&BRANCHID=" + params.get("BRANCHID") +
            "&ORDERID=" + params.get("ORDERID") +
            "&PAYMENT=" + params.get("PAYMENT") +
            "&CURCODE=" + params.get("CURCODE") +
            "&REMARK1=" + params.get("REMARK1") +
            "&REMARK2=" + params.get("REMARK2");
    String ACC_TYPE = params.get("ACC_TYPE");
    if (ACC_TYPE != null) {
      preMacStr += "&ACC_TYPE=" + ACC_TYPE;
    }
    preMacStr += "&SUCCESS=" + params.get("SUCCESS") +
        "&TYPE=" + params.get("TYPE") +
        "&REFERER=" + params.get("REFERER") +
        "&CLIENTIP=" + params.get("CLIENTIP");
    String ACCDATE = params.get("ACCDATE");
    if (ACCDATE != null) {
      preMacStr += "&ACCDATE=" + ACCDATE;
    }
    String USRMSG = params.get("USRMSG");
    if (USRMSG != null) {
      preMacStr += "&USRMSG=" + USRMSG;
    }
    String INSTALLNUM = params.get("INSTALLNUM");
    if (INSTALLNUM != null) {
      preMacStr += "&INSTALLNUM=" + INSTALLNUM;
    }
    String ERRMSG = params.get("ERRMSG");
    if (ERRMSG == null) {
      ERRMSG = "";
    }
    preMacStr += "&ERRMSG=" + ERRMSG;
    String USRINFO = params.get("USRINFO");
    if (USRINFO != null) {
      preMacStr += "&USRINFO=" + USRINFO;
    }
    return rsaSig.verifySigature(sign, preMacStr);
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
      if (order.isCompleted()) {
        throw new PayException("订单已支付");
      }
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
          "&TXCODE=" + "520100" +
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
          .append("&TXCODE=").append("520100")
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
  public boolean checkOrder(String orderNo, OrderHandler orderHandler) {
    return orderQuery(orderNo, orderHandler, "0");
  }

  /**
   * @param orderNo 订单号
   * @param orderHandler 订单处理类
   * @param type 查询类型 0：支付流水 1：退款流水
   * @return 是否完成
   */
  private boolean orderQuery(String orderNo, OrderHandler orderHandler, String type) {
    Order order = orderHandler.findByNo(orderNo);
    try {
      if ("0".equals(type) && order.isCompleted()) {
        return true;
      } else if ("1".equals(type) && order.isRefundCompleted()) {
        return true;
      }

      String MERCHANTID = properties.getMerchantid();
      String BRANCHID = properties.getBranchid();                 //分行代码
      String POSID = properties.getPosid();                    //柜台号
      String QUPWD = properties.getQupwd();
      String TXCODE = "410408";
      String SEL_TYPE = "3";//13.	查询方式SEL_TYPE   1页面形式 2文件返回形式 (提供TXT和XML格式文件的下载) 3 XML页面形式
      String OPERATOR = "";//非必输项。OPERATOR元素必须有,但值可为空。主管查询的时候为空。
      String STATUS = "3";//交易状态STATUS    0失败    1成功    2不确定    3全部（已结算流水查询不支持全部）
      String KIND = "0";//必输项（当日只有未结算流水可供查询）    0 未结算流水    1 已结算流水
      String PAGE = "1";//必输项，输入将要查询的页码。
      String CHANNEL = "";//现值为空，但CHANNEL元素必须有。
      String ORDERDATE = "";
      String BEGORDERTIME = "";
      String ENDORDERTIME = "";
      //MERCHANTID=value&BRANCHID= value &POSID= value &ORDERDATE= value &BEGORDERTIME= value &ENDORDERTIME= value
      // &ORDERID= value &QUPWD=&TXCODE=410408&TYPE= value &KIND= value &STATUS= value &SEL_TYPE= value
      // &PAGE= value &OPERATOR= value &CHANNEL= value
      String preSign =
          "MERCHANTID=" + MERCHANTID + "&BRANCHID=" + BRANCHID + "&POSID=" + POSID + "&ORDERDATE="
              + ORDERDATE + "&BEGORDERTIME=" + BEGORDERTIME + "&ENDORDERTIME=" + ENDORDERTIME
              + "&ORDERID=" + orderNo
              + "&QUPWD=&TXCODE=" + TXCODE + "&TYPE=" + type + "&KIND="
              + KIND + "&STATUS=" + STATUS
              + "&SEL_TYPE=" + SEL_TYPE + "&PAGE=" + PAGE + "&OPERATOR=" + OPERATOR + "&CHANNEL="
              + CHANNEL;
      if (log.isDebugEnabled()) {
        log.debug("MAC:{}", DigestUtils.md5DigestAsHex(preSign.getBytes("UTF-8")));
      }
      MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
      param.add("MERCHANTID", MERCHANTID);
      param.add("BRANCHID", BRANCHID);
      param.add("POSID", POSID);
      param.add("STATUS", STATUS);
      param.add("KIND", KIND);
      param.add("ORDERDATE", ORDERDATE);
      param.add("BEGORDERTIME", BEGORDERTIME);
      param.add("ENDORDERTIME", ENDORDERTIME);
      param.add("ORDERID", orderNo);
      param.add("PAGE", PAGE);
      param.add("QUPWD", QUPWD);
      param.add("TXCODE", TXCODE);
      param.add("CHANNEL", CHANNEL);
      param.add("SEL_TYPE", SEL_TYPE);
      param.add("OPERATOR", OPERATOR);
      param.add("TYPE", type);
      param.add("MAC", DigestUtils.md5DigestAsHex(preSign.getBytes("UTF-8")));

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.set(HttpHeaders.USER_AGENT,
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
      String result = restTemplate.postForObject("https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain",
          new HttpEntity<>(param, httpHeaders), String.class)
          .replaceAll("[\n\r\t]", "");//返回的数据去除\n换行等符号，建行渣渣接口
      if (log.isDebugEnabled()) {
        log.debug("查询结果：{}", StringUtil.valueOf(result));
      }

      LoongQueryResult loongQueryResult = xmlMapper.readValue(result, LoongQueryResult.class);

      if ("000000".equals(loongQueryResult.isSuccess())) {
        QueryOrder queryorder = loongQueryResult.QUERYORDER;
        if (queryorder.MERCHANTID.equals(MERCHANTID) && queryorder.BRANCHID.equals(BRANCHID)
            && queryorder.POSID.equals(POSID)) {
          if (queryorder.ORDERID.equals(orderNo)) {
            if (queryorder.verify(rsaSig)) {
              if (queryorder.isSuccess()) {
                if ("0".equals(type)) {
                  complete(order, orderHandler);
                } else if ("1".equals(type) && !order.isRefundCompleted()) {
                  orderHandler.refundComplete(order, getProvider());
                }
                return true;
              }
            } else {
              log.error("验签失败，{}", result);
            }
          } else {
            log.error("订单：{}查询失败，订单不匹配，响应订单号：{}，本地订单号：{}", order.getNo(),
                queryorder.ORDERID, orderNo);
          }
        } else {
          log.error("订单：{}查询失败，商户不匹配,响应商户：{}/{}/{},本地商户：{}/{}/{}", order.getNo(),
              queryorder.MERCHANTID, queryorder.BRANCHID, queryorder.POSID, MERCHANTID, BRANCHID,
              POSID);
        }
      } else {
        log.error("订单：" + order.getNo() + "查询失败,{}", loongQueryResult.RETURN_MSG);
      }
    } catch (Exception e) {
      log.error("订单：" + order.getNo() + "查询失败", e);
    }
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
          String merchantid = properties.getMerchantid();
          String branchid = properties.getBranchid();
          String posid = properties.getPosid();
          String MERCHANTID = params.get("MERCHANTID");
          if (merchantid.equals(MERCHANTID) && posid
              .equals(POSID) && branchid
              .equals(BRANCHID)) {
            //验证成功  更新该订单的支付状态 并把对应的金额添加给用户
            String orderid = params.get("ORDERID");
            //金额
            Order order = orderHandler.findByNo(orderid);
            if (order != null) {
              BigDecimal payment = new BigDecimal(params.get("PAYMENT"))
                  .multiply(BigDecimal.valueOf(100));
              long totalAmount = order.getTotalAmount();
              if (new BigDecimal(totalAmount).equals(payment)) {
                complete(order, orderHandler);
                return "success";
              } else {
                log.error("龙支付异步通知失败，金额不匹配，服务器金额：{}分,本地订单金额：{}分",
                    payment, totalAmount);
              }
            } else {
              log.error("龙支付异步通知失败，不是系统订单：{}", StringUtil.valueOf(params, true));
            }
          } else {
            log.error("龙支付异步通知失败，商户不匹配,响应商户：{}/{}/{},本地商户：{}/{}/{}",
                MERCHANTID, BRANCHID, POSID, merchantid, branchid, posid);
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

  @Override
  public Order refund(Order order, OrderHandler orderHandler) throws PayException {
    throw new PayException("龙支付不支持退款");
  }

  @Override
  public boolean refundQuery(String orderNo, OrderHandler orderHandler) {
    return orderQuery(orderNo, orderHandler, "1");
  }

}
