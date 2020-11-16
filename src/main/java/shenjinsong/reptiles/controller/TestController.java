package shenjinsong.reptiles.controller;


import com.warai.paramcheck.annotation.ParamCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: わらい
 * @Time: 2020/10/15 16:32
 */
@RestController
public class TestController {

    @ParamCheck(value = "test < 5", errorCode = "000")
    @GetMapping("/test")
    public Object test1(){
        return "test1";
    }

    @ParamCheck("test|test1")
    @GetMapping("/get")
    public String tee(String test, String test1, HttpServletRequest request){
        String ipAddr = IpUtil.getIpAddr(request);
        return test + test1;
    }

    @ParamCheck({"val ~ 1", "oar", "sisis <2"})
    @PostMapping("/test2")
    public String twtiw(@RequestBody TestVO testVO){

        return testVO.getVal();
    }

}
