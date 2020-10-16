package reptiles.controller;

import com.warai.paramcheck.annotation.ParamCheck;
import com.warai.paramcheck.configurer.ErrorResultHandlerConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Auther: わらい
 * @Time: 2020/9/22 16:27
 */
@Slf4j
@Configuration
public class ErrorResultHandler extends ErrorResultHandlerConfigurer {

    @Override
    public void handler(String param, ParamCheck paramCheck) throws IOException {
        super.handler(param, paramCheck);
    }

}
