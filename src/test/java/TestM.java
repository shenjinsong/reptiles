import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther: 大叔
 * @Time: 2019/10/16 11:15
 */
public class TestM {
    static ExecutorService executor = Executors.newFixedThreadPool(10000);



    static  void test(int i){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0");
        JSONObject json = new JSONObject();
        json.put("type",2);
//        json.put("phone","15421521512");
        json.put("password","123456");

        HttpEntity<String> request = new HttpEntity(json,httpHeaders);



        String url = "https://www.youxiuqingnian.com/sys/sys/user/login";

        ResponseEntity<Object> objectResponseEntity = new RestTemplate().postForEntity(url, request, Object.class);

        System.out.println(new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));
        System.out.println(Thread.currentThread().getName() + "   -   " + i);

    }

    public static void main(String[] args) {

        int i = 0;

        while (i++ < 1000){
            int l = i;
            executor.submit(() -> test(l));
        }
        System.out.println(new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()) + ": 所有线程创建完毕");
    }


}
