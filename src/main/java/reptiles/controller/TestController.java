package reptiles.controller;

import com.warai.paramcheck.annotation.ParamCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: わらい
 * @Time: 2020/10/15 16:32
 */
@RestController
public class TestController {

    @ParamCheck("test")
    @GetMapping("/test")
    public Object test1(){
        return "test1";
    }
}
