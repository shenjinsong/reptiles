package reptiles.paramcheck.configurer;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: わらい
 * @Time: 2020/9/22 16:26
 */
public abstract class ErrorResultHandlerConfigurer {

    @Resource
    private HttpServletResponse response;

    public void handler(String param, String [] checkFields) throws IOException {
        Map<String, Object> map = new HashMap<>(1);
        map.put("code", "999999");
        map.put("msg", "请求参数错误");
        map.put("field", checkFields);
        map.put("param", param);
        this.handler(map, HttpStatus.PRECONDITION_FAILED);
    }

    public void handler(Map responseMsg, HttpStatus status) throws IOException {
        new ParamException(responseMsg,  status).build();
    }

    public class ParamException extends Exception{

        private Map msg;
        private HttpStatus status;

        ParamException(Map msg, HttpStatus status){
            this.msg = msg;
            this.status = status;
        }

        void build() throws IOException {
            response.setStatus(status.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            PrintWriter out = response.getWriter();
            String json = JSON.toJSONString(msg);
            out.write(json);
            out.flush();
            out.close();
        }



    }
}
