package shenjinsong.reptiles.controller;

import com.alibaba.fastjson.JSONObject;
import com.warai.paramcheck.ErrorResultHandler;
import com.warai.paramcheck.Operator;
import com.warai.paramcheck.annotation.ParamCheck;

import java.io.IOException;
import java.util.List;

/**
 * @Auther: わらい
 * @Time: 2020/9/22 16:27
 */
public class CustomErrorResultHandler extends ErrorResultHandler {

    @Override
    public void handler(JSONObject params, List<String> badFields, ParamCheck paramCheck) throws IOException {
        super.handler(params, badFields, paramCheck);
    }
}
