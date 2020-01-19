package zhenfei.liu.remoting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.springframework.util.StringUtils;
import zhenfei.liu.template.Template;
import zhenfei.liu.util.HttpClientHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lzf
 * desc
 * date 2019/12/16-10:39
 */
public class HttpClientRequestService extends AbstractRequestService {


    @Override
    public Object invoke(Template template, String paramer) {
        super.instance(template);
        String result = "";
        try{
            HttpClient httpClient = this.newHttpClientInstance();
            if ("GET".equals(super.getMethodReuqesType().name())) {
                result = HttpClientHelper.sendGet(httpClient, template.getRequest() + "?" + paramer);
            } else {
                result = HttpClientHelper.sendPost(httpClient, super.getPath(), paramer, super.getConsumes());
            }
            if (StringUtils.hasText(result)) {
                if ("status".indexOf(result) > 0) {
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (!"200".equals(jsonObject.get("status").toString())) {
                        throw new RuntimeException("the request url: " + template.getRequest() + " has " + jsonObject.get("error").toString());
                    }
                }
                //如果是基本类型 直接返回 无须进行json 转化
                /*if("java.lang.String".equals(super.getClazz().getName())
                    || "java.lang.Integer".equals(super.getClazz().getName())
                    || "java.lang.Boolean".equals(super.getClazz().getName())
                    || "java.lang.Long".equals(super.getClazz().getName())
                     ){
                    return result;
                }
                if( "java.util.Map".equals(super.getClazz().getName())){
                    return JSON.parseObject(result, Map.class);
                }
                Object obj =super.getClazz().newInstance();
                return JSON.parseObject(result, obj.getClass());*/
                return coventResult(super.getClazz().getName(), result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取HttpClient 实例
    private HttpClient newHttpClientInstance() {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        return httpClient;
    }


    private Object coventResult(String resultType, String result) {
        Object obj = null;
        switch (resultType) {
            case "java.lang.String":
            case "java.lang.Integer":
            case "java.lang.Boolean":
            case "java.lang.Long":
                obj = result;
                break;
            case "java.util.Map":
                obj = JSON.parseObject(result, Map.class);
                break;
            case "java.util.List":
                obj = JSON.parseObject(result, List.class);
                break;
            default:
                break;
        }
        return obj;
    }
}
