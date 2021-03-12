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

   public Integer defaultConnectionTimeout = 20000;

   public final  HttpClient httpClient = new HttpClient();

   public HttpClientRequestService(){
       httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(defaultConnectionTimeout);
   };

   public HttpClientRequestService(int connectionTime){
       defaultConnectionTimeout = connectionTime == 0 ? defaultConnectionTimeout :connectionTime;
   }

    @Override
    public Object doInvoke(Template template, String paramer) {
        String result = "";
        try{
            if ("GET".equals(super.getMethodReuqesType().name())) {
                result = HttpClientHelper.sendGet(httpClient, template.getRequest() + "?" + paramer);
            } else {
                result = HttpClientHelper.sendPost(httpClient, template.getRequest(), paramer, super.getConsumes());
            }
            if (StringUtils.hasText(result)) {
                if ("status".indexOf(result) > 0) {
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (!"200".equals(jsonObject.get("status").toString())) {
                        throw new RuntimeException("the request url: " + template.getRequest() + " has " + jsonObject.get("error").toString());
                    }
                }
                return coventResult(super.getClazz().getName(), result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
