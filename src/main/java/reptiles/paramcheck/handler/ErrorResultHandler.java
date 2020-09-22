package reptiles.paramcheck.handler;

import org.springframework.scheduling.annotation.Async;
import reptiles.paramcheck.annotation.ParamCheck;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: わらい
 * @Time: 2020/9/22 16:26
 */
public interface ErrorResultHandler {


    void responseOut(HttpServletResponse response) throws IOException;

    @Async
    void recordErrLog(String param, HttpServletRequest request, ParamCheck paramCheck);

}
