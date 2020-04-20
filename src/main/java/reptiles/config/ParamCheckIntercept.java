package reptiles.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 使用拦截器判断参数
 *
 * @author わらい
 * @date 2020/04/20
 */
@Slf4j
public class ParamCheckIntercept extends HandlerInterceptorAdapter {

    private String requestParams = "";

    private static Pattern pattern = Pattern.compile("null|undefined");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 判断是有@PramCheck 注解和注解中是否存在需要校验的字段， 没有直接返回
        ParamCheck paramCheck = method.getAnnotation(ParamCheck.class);
        if (paramCheck == null || ObjectUtils.isEmpty(paramCheck.value())) {
            return true;
        }

        Annotation[][] parameterAnnotationArrays = method.getParameterAnnotations();

        // 判断请求参数是否是请求体传过来
        boolean isRequestBody = false;
        query:
        for (Annotation[] parameterAnnotation : parameterAnnotationArrays) {
            for (Annotation annotation : parameterAnnotation) {
                Class<? extends Annotation> aClass = annotation.annotationType();
                isRequestBody = RequestBody.class.equals(aClass);
                if (isRequestBody) {
                    break query;
                }
            }
        }

        // 拿到可重用的流
        ServletRequest servletRequest = new RequestReaderHttpServletRequestWrapper(request);

        // 检查参数
        boolean checkSuccess = this.checkReqParams(paramCheck, servletRequest, isRequestBody);

        if (!checkSuccess) {
            log.info("缺少必要的参数");
            // TODO 缺失必要参数时可记录接口...
            this.responseOut(response);
            return false;
        }
        log.info("参数校验通过");
        return true;

    }


    private boolean checkReqParams(ParamCheck paramCheck, ServletRequest request, boolean isRequestBody) {

        log.info("校验的参数：" + JSON.toJSONString(paramCheck.value()));
        // 链接中包含参数，和请求体中参数校验过程
        if (isRequestBody) {
            return this.checkReqBodyParams(paramCheck, request);
        } else {
            requestParams = JSON.toJSONString(request.getParameterMap());
            return Arrays.stream(paramCheck.value()).map(request::getParameter).noneMatch(ParamCheckIntercept::invalid);
        }
    }


    private boolean checkReqBodyParams(ParamCheck paramCheck, ServletRequest servletRequest) {

        JSONObject jsonObject = null;

        try (InputStreamReader inputStreamReader = new InputStreamReader(servletRequest.getInputStream(), StandardCharsets.UTF_8)) {

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // 将请求中的请求体通过流读成字符
            requestParams = bufferedReader.lines().collect(Collectors.joining());
            // 传入的json数据序列化为Json对象
            jsonObject = JSONObject.parseObject(requestParams);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject == null) {
            return false;
        }

        return Arrays.stream(paramCheck.value()).map(jsonObject::get).noneMatch(ParamCheckIntercept::invalid);
    }


    /**
     * 校验参数是否无效
     * 是否非空、null、underfined
     *
     * @param obj
     * @return
     */
    private static boolean invalid(Object obj) {

        // 非空
        if (ObjectUtils.isEmpty(obj)) {
            return true;
        }

        log.info("参数类型：" + obj.getClass());

        // 校验参数 (null，undefined)
        return pattern.matcher(JSON.toJSONString(obj).toLowerCase()).find();
    }

    /**
     * 回写给客户端
     *
     * @param response
     * @throws IOException
     */
    private void responseOut(HttpServletResponse response) throws IOException {
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
