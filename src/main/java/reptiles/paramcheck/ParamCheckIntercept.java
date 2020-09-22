package reptiles.paramcheck;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import reptiles.paramcheck.annotation.ParamCheck;
import reptiles.paramcheck.handler.ErrorResultHandler;
import reptiles.paramcheck.util.SpringContextUtil;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private static ErrorResultHandler errorResultHandler;


    private static synchronized ErrorResultHandler errorResultHandler() {

        if (errorResultHandler == null) {
            try{
                errorResultHandler = SpringContextUtil.getBean(ErrorResultHandler.class);
            }catch (NoSuchBeanDefinitionException e){
                log.error("ParamCheck lacks the error result handling implementation class and needs to implement the ErrorResultHandler interface");
                throw e;
            }
        }

        return errorResultHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!handler.getClass().isAssignableFrom(HandlerMethod.class)){
            return true;
        }

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
            executor.execute(() -> errorResultHandler().recordErrLog(threadLocal.get(), request, paramCheck));
            errorResultHandler().responseOut(response);
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
            String jsonStr = JSON.toJSONString(request.getParameterMap());
            threadLocal.set(jsonStr);
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            return checkParam(paramCheck, jsonObject);
        }
    }


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


    private boolean checkParam(ParamCheck paramCheck, JSONObject jsonObject) {
        for (String checkStr : paramCheck.value()) {
            if (invalid(OPR.matcher(checkStr).find(), checkStr, jsonObject)) {
                log.warn("参数校验：{} 不通过, 参数：{}", checkStr, jsonObject.toJSONString());
                return false;
            }
        }
        return true;
    }


    private boolean invalid(boolean containOpr, String checkStr, JSONObject param) {
        // 包含运算符做特殊检验
        checkStr = checkStr.replaceAll(" ", "");
        if (containOpr) {
            String[] checkStrs = checkStr.split(OPR_EXPS);
            if (checkStr.contains("|")) {
                return Arrays.stream(checkStrs).allMatch(str -> containErrorValue(param.get(str), null));

            } else if (checkStr.contains("-")) {
                String[] val = checkStr.split("-");
                String field = val[0];
                Integer length = null;
                try {
                    length = Integer.valueOf(val[1]);
                }catch (Exception e){
                    log.warn("@ParamCheck '字符长度限制'使用方法[{}]错误, 校验不生效", checkStr);
                }

                Object o = param.get(field);

                if (o instanceof JSONArray) {
                    JSONArray o1 = (JSONArray) o;
                    // 数组时逐个判断
                    Integer finalLength = length;
                    return o1.stream().anyMatch(o2 -> containErrorValue(o2, finalLength));
                } else {
                    return containErrorValue(param.getString(field), length);
                }

            }
            return true;

        } else {
            return containErrorValue(param.get(checkStr), null);
        }
    }

    private static boolean containErrorValue(Object obj, Integer length) {
        log.info("校验参数值：" + JSON.toJSONString(obj) + "，长度：" + length);
        if (ObjectUtils.isEmpty(obj)) {
            return true;
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (Object o : jsonArray) {
                if (containErrorValue(o, length)) {
                    return true;
                }
            }
        }

        if (ERROR_VALUE.matcher(JSON.toJSONString(obj).toLowerCase()).find()){
            return true;
        }

        return length != null && obj.toString().length() > length;
    }

}
