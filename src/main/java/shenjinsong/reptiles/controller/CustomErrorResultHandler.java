package shenjinsong.reptiles.controller;

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
    public void handler(List<String> badFields, ParamCheck paramCheck) throws IOException {
        for (String badField : badFields) {
            String[] split = badField.split(Operator.OPR_EXPS);
            System.out.println();
        }
        super.handler(badFields, paramCheck);
    }
}
