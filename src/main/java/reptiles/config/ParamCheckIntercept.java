package reptiles.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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


    private static Pattern ERROR_VALUE = Pattern.compile("null|undefined");

    private static String OPR_EXPS = "[<>\\-=|]";

    private static Pattern OPR = Pattern.compile(OPR_EXPS);

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

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
        System.out.println("threadLocal 变量取值：" + threadLocal.get());
        if (!checkSuccess) {
            log.info("参数不正确");
            this.recordErrMsg();
            this.responseOut(response);
            return false;
        }
        log.info("参数校验通过");
        return true;

    }

    // 参数校验通过返回：true
    private boolean checkReqParams(ParamCheck paramCheck, ServletRequest request, boolean isRequestBody) {

        log.info("校验的参数：" + JSON.toJSONString(paramCheck.value()));
        // 链接中包含参数，和请求体中参数校验过程
        if (isRequestBody) {
            return this.checkReqBodyParams(paramCheck, request);
        } else {
            String jsonStr = JSON.toJSONString(request.getParameterMap());
            threadLocal.set(jsonStr);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            return checkParam(paramCheck, jsonObject);
        }
    }

    // 检查通过返回：true
    private boolean checkReqBodyParams(ParamCheck paramCheck, ServletRequest servletRequest) {

        JSONObject jsonObject = null;

        try (InputStreamReader inputStreamReader = new InputStreamReader(servletRequest.getInputStream(), StandardCharsets.UTF_8)) {

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // 将请求中的请求体通过流读成字符
            String jsonStr = bufferedReader.lines().collect(Collectors.joining());
            // 将参数放到线程变量中。后面记录参数值需要
            threadLocal.set(jsonStr);
            // 传入的json数据序列化为Json对象
            jsonObject = JSONObject.parseObject(jsonStr);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject == null) {
            return false;
        }

        return checkParam(paramCheck, jsonObject);
    }

    // 检查通过返回：true
    private boolean checkParam(ParamCheck paramCheck, JSONObject jsonObject) {
        for (String checkStr : paramCheck.value()) {
            if (invalid(OPR.matcher(checkStr).find(), checkStr, jsonObject)) {
                return false;
            }
        }
        return true;
    }

    // 参数不合法： true
    private boolean invalid(boolean containOpr, String checkStr, JSONObject param) {
        // 包含运算符做特殊检验
        checkStr = checkStr.replaceAll(" ", "");
        if (containOpr) {
            String[] checkStrs = checkStr.split(OPR_EXPS);
            if (checkStr.contains("|")) {
                for (String str : checkStrs) {
                    if (!containErrorValue(param.get(str))) {
                        return false;
                    }
                }
                return true;

            } else if (checkStr.contains("-")) {
                String[] val = checkStr.split("-");
                String field = val[0];
                String length = val[1];
                Object o = param.get(field);
                String par;
                if (o instanceof JSONArray) {
                    JSONArray o1 = (JSONArray) o;
                    par = o1.get(0).toString();
                } else {
                    par = param.getString(field);
                }

                try {
                    // 校验长度、校验是否合法
                    if (containErrorValue(par) || par.length() > Integer.valueOf(length)) {
                        log.warn(field + " 字符超过长度: " + length + "; 或者参数不合法");
                        return true;
                    }
                    return false;

                } catch (Exception e) {
                    log.error("@ParamCheck Parameter configuration error：" + checkStr + " , please check the grammar! Specifies the length usage :  value - 10");
                    return true;
                }
            }
            return true;

        } else {
            return containErrorValue(param.get(checkStr));
        }
    }


    /**
     * 校验参数是否无效
     * 是否非空、null、underfined
     *
     * @param obj
     * @return
     */
    private static boolean containErrorValue(Object obj) {

        if (ObjectUtils.isEmpty(obj)) {
            return true;
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (Object o : jsonArray) {
                System.out.println(o);
                if (ObjectUtils.isEmpty(o)) {
                    return true;
                }
            }
        }

        return ERROR_VALUE.matcher(JSON.toJSONString(obj).toLowerCase()).find();
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

    private void recordErrMsg() {
        // TODO 记录错误信息
    }
}
