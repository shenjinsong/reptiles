package reptiles.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import reptiles.paramcheck.configurer.ErrorResultHandlerConfigurer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: わらい
 * @Time: 2020/9/22 16:27
 */
@Slf4j
@Configuration
public class ErrorResultHandler extends ErrorResultHandlerConfigurer {

    @Override
    public void handler(String param, String[] checkFields) throws IOException {
        Map<String, Object> map = new HashMap<>(1);
        map.put("code", "999999");
        map.put("msg", "请求参数错误");
        super.handler(map, HttpStatus.PRECONDITION_FAILED);
//        super.handler(param, checkFields);
    }
}
