package reptiles.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import reptiles.paramcheck.handler.ErrorResultHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: わらい
 * @Time: 2020/9/22 16:27
 */
@Slf4j
@Configuration
public class ErrorResultHandlerImpl extends ErrorResultHandler{

    @Resource
    private HttpServletResponse response;

    @Override
    public void handler(String param, String[] checkFields) throws IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        Map<String, String> map = new HashMap<>(1);
        map.put("code", "999999");
        map.put("msg", "缺少必要参数");
        String json = JSONObject.toJSON(map).toString();
        out.write(json);
        out.flush();
        out.close();
    }
}
