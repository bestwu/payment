package cn.bestwu.pay.payment;

import java.util.Set;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;

/**
 * 基础测试类
 *
 * @author Peter Wu
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseTest {

  protected void printParams(LinkedMultiValueMap<String, Object> params) {
    StringBuilder st = new StringBuilder("{\n");
    Set<String> keySet = params.keySet();
    int i = 0;
    for (String key : keySet) {
      for (Object o : params.get(key)) {
        st.append("\"").append(key).append("\": \"").append(o).append("\"");
      }
      if (i < keySet.size() - 1) {
        st.append(",");
      }
      st.append("\n");
      i++;
    }
    st.append("}");
    System.err.println(st);
  }
}
