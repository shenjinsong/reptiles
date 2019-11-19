package eqianbao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: 大叔
 * @Time: 2019/7/10 17:03
 */
@SuppressWarnings("ALL")
public class ESignDemo {

    static RestTemplate restTemplate = new RestTemplate(new HttpsClientRequestFactory());

    static String

            TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJnSWQiOiJjNjc3ZGI3NTQwNWI0Y2JjYjE5NDFkMzU2ODI4MTQ5NyIsImFwcElkIjoiMTExMTU2MzUxNyIsIm9JZCI6ImI5ODdkOTE1ZjJmZDRkOGNhZTJkNTM2Y2YzM2IyNTQwIiwidGltZXN0YW1wIjoxNTYyOTE3OTA1MTI1fQ.f1wIObVbCBsvJqeHvrvUDHBBF4W6Q7v9D5fZe0W5R3I",
            REFRESH_TOKEN = "28b39cde3cab057202186ece9b4deb47",

            APP_ID = "1111563517",
            SECRET = "95439b0863c241c63a861b87d1e647b7",

            URL = "https://smlopenapi.esign.cn",
            PER_ACC_NO = "1f2673bb18b4401bbdf5adac5fc3f895",
            ORG_ACC_NO = "1f2673bb18b4401bbdf5adac5fc3f895",
            FLOW_ID = "ef02fe9ff69e45f3bc6795c70dedb597";


    /**
     * 获取accessToken
     */
    static void getAccessToken() {

        String url = URL + "/v1/oauth2/access_token?appId={appId}&secret={secret}&grantType={grantType}";

        Map<String, String> map = new HashMap<>();
        map.put("appId", APP_ID);
        map.put("secret", SECRET);
        map.put("grantType", "client_credentials");

        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class, map);
        JSONObject data = jsonObject.getJSONObject("data");
        TOKEN = data.getString("token");
        System.out.println(TOKEN);
        REFRESH_TOKEN = data.getString("refreshToken");
        System.out.println(REFRESH_TOKEN);
    }

    /**
     * 刷新accessToken
     */
    static void regreshToken() {

        String url = URL + "/v1/oauth2/refresh_token?appId={appId}&grantType={grantType}&refreshToken={refreshToken}";

        Map<String, String> map = new HashMap<>();
        map.put("appId", APP_ID);
        map.put("grantType", "refresh_token");
        map.put("refreshToken", REFRESH_TOKEN);

        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class, map);
        JSONObject data = jsonObject.getJSONObject("data");
        TOKEN = data.getString("token");
        REFRESH_TOKEN = data.getString("refreshToken");
    }

    /**
     * 封装请求的头部
     *
     * @return
     */
    static HttpHeaders header() {
        HttpHeaders headers = new HttpHeaders();

        headers.add("X-Tsign-Open-App-Id", APP_ID);
        headers.add("X-Tsign-Open-Token", TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    /**
     * 创建个人账号
     */
    static void createPersAcc() {

        String url = URL + "/v1/accounts/createByThirdPartyUserId";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("thirdPartyUserId", "shenjinsong");
        jsonObject.put("name", "胖大叔");

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), header());
        JSONObject result = restTemplate.postForObject(url, httpEntity, JSONObject.class);

    }

    /**
     * 创建机构账号
     */
    static void createOrgAcc() {

        String url = URL + "/v1/organizations/createByThirdPartyUserId";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("thirdPartyUserId", "shenjinsong");
        jsonObject.put("creator", PER_ACC_NO);
        jsonObject.put("name", "深圳幻想伏特加科技有限公司");

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), header());
        JSONObject result = restTemplate.postForObject(url, httpEntity, JSONObject.class);

        System.out.println(result);
    }

    /**
     * 创建待签文件（获取文件路径） {"code":0,"data":{"fileId":"3ff7f3cf5d0441b699c80ac9f72df84c","uploadUrl":"https://esignoss.oss-cn-hangzhou.aliyuncs.com/1111564182/de92763f-25de-44f9-92c5-383d970d747d/test.pdf?Expires=1562899519&OSSAccessKeyId=STS.NHgBCSoyPf8FyQPVrnwXr5oLL&Signature=YwFyQOc/NbvBciFZ7xo7m/tvWgI%3D&callback-var=eyJ4OmZpbGVfa2V5IjoiJGM4OTI2OTM2LTczYWUtNGMzOS1iOTU0LWU3NTNkZDg1ZDMyOCQzODYzNjY0ODcifQ%3D%3D%0A&callback=eyJjYWxsYmFja1VybCI6Imh0dHA6Ly80Ny45OS4yMjQuMjM1OjgwODAvZmlsZS1zeXN0ZW0vY2FsbGJhY2svYWxpb3NzIiwiY2FsbGJhY2tCb2R5IjogIntcIm1pbWVUeXBlXCI6JHttaW1lVHlwZX0sXCJzaXplXCI6ICR7c2l6ZX0sXCJidWNrZXRcIjogJHtidWNrZXR9LFwib2JqZWN0XCI6ICR7b2JqZWN0fSxcImV0YWdcIjogJHtldGFnfSxcImZpbGVfa2V5XCI6JHt4OmZpbGVfa2V5fX0iLCJjYWxsYmFja0JvZHlUeXBlIjogImFwcGxpY2F0aW9uL2pzb24ifQ%3D%3D%0A&security-token=CAIS%2BAF1q6Ft5B2yfSjIr4vSCfnngqZx0fqte3fhsnI7e9de2qrnrjz2IHtKdXRvBu8Xs/4wnmxX7f4YlqB6T55OSAmcNZEoRHPIBtXkMeT7oMWQweEurv/MQBqyaXPS2MvVfJ%2BOLrf0ceusbFbpjzJ6xaCAGxypQ12iN%2B/m6/Ngdc9FHHPPD1x8CcxROxFppeIDKHLVLozNCBPxhXfKB0ca0WgVy0EHsPnvm5DNs0uH1AKjkbRM9r6ceMb0M5NeW75kSMqw0eBMca7M7TVd8RAi9t0t1/IVpGiY4YDAWQYLv0rda7DOltFiMkpla7MmXqlft%2BhzcgeQY0pc/RqAAbP2fcNMdRIclxsW4yxqe2jDan8TZ2tuCHkWbzq43Upm0m1raC98V6tZ3bDU/ZtQ2dDb2NhDCeyjm%2BnoTSao%2BD1PVfkqZoBKUWaijlIV8dZ60i4rZBjMCqYpUks9Gx%2BWgA3KQ2qbYqpddtxr9%2By2zppimhkebvaLFAwVQuRa5tc%2B"},"message":"成功"}
     */
    static void createFile() throws IOException, NoSuchAlgorithmException {

        File file = new File("C:\\Users\\Administrator\\Desktop\\test.pdf");
        FileInputStream fileInputStream = new FileInputStream(file);
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer, 0, 1024)) != -1) {
            md5.update(buffer, 0, length);
        }

        byte[] md5Bytes = md5.digest();
        String fileStr = new String(Base64.getEncoder().encode(md5Bytes), StandardCharsets.UTF_8);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contentMd5", fileStr);
        jsonObject.put("contentType", MediaType.APPLICATION_PDF_VALUE);
        jsonObject.put("fileName", "test.pdf");
        jsonObject.put("fileSize", file.length());
        jsonObject.put("accountId", ORG_ACC_NO);

        String url = URL + "/v1/files/getUploadUrl";

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), header());
        JSONObject result = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        System.out.println(result);
    }

    /**
     * 签署流程 {"code":0,"data":{"flowId":"ef02fe9ff69e45f3bc6795c70dedb597"},"message":"成功"}
     */
    static void signflows() {

        String url = URL + "/v1/signflows";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("businessScene", "scene_test");

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toJSONString(), header());
        JSONObject result = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        System.out.println(result);
    }

    /**
     * 查询签署流程
     */
    static void querySignFlows() {

        String url = URL + "/v1/signflows/{flowId}";

        Map<String, String> map = new HashMap<>();
        map.put("flowId", FLOW_ID);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(header()), JSONObject.class, map);

        System.out.println(responseEntity);

    }

    /**
     * 开启签署流程
     */
    static void startSignFlows(){

        String url = URL + "/v1/signflows/{flowId}/start";
        Map<String, String> map = new HashMap<>();
        map.put("flowId", FLOW_ID);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(header()), JSONObject.class, map);

        System.out.println(responseEntity);

    }

    /**
     * 撤销签署流程
     */
    static void revokeSignFlows(){

        String url = URL + "/v1/signflows/{flowId}/revoke";
        Map<String, String> map = new HashMap<>();
        map.put("flowId", FLOW_ID);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(header()), JSONObject.class, map);
        System.out.println(responseEntity);

    }

    /**
     * 归档签署流程
     */
    static void archiveSignFlows(){

        String url = URL + "/v1/signflows/{flowId}/archive";
        Map<String, String> map = new HashMap<>();
        map.put("flowId", FLOW_ID);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(header()), JSONObject.class, map);

        System.out.println(responseEntity);

    }

    /**
     * 添加流程文档
     */
    static void addDocu2Flows(){

        String url = URL + "/v1/signflows/{flowId}/documents";
        Map<String, Object> map = new HashMap<>();
        map.put("flowId", FLOW_ID);

        Map<String, Object> param = new HashMap<>();
        param.put("fileId","1221");

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(param);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("docs", jsonArray);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(jsonObject.toJSONString(),header()), JSONObject.class, map);

        System.out.println(responseEntity);

    }

    public static void main(String[] args) {

        addDocu2Flows();
    }


}
