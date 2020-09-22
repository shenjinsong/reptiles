package reptiles.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reptiles.paramcheck.handler.ErrorResultHandler;
import reptiles.paramcheck.annotation.ParamCheck;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: わらい
 * @Time: 2020/9/22 16:27
 */
@Service
public class ErrorResultHandlerImpl implements ErrorResultHandler{

    @Override
    public void responseOut(HttpServletResponse response) throws IOException {
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

    @Override
    public void recordErrLog(String param, HttpServletRequest request, ParamCheck paramCheck) {
        System.out.println("实现类的recordErrLog : " + Thread.currentThread().getId());

    }
}
