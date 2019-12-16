package zhenfei.liu.remoting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.springframework.util.StringUtils;
import zhenfei.liu.template.Template;
import zhenfei.liu.util.HttpClientHelper;

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
            if("GET".equals(super.getMethodReuqesType())){
                result = HttpClientHelper.sendGet(httpClient,template.getRequest()+"?" +paramer);
            }else{
                result = HttpClientHelper.sendPost(httpClient,super.getPath(),paramer,super.getConsumes());
            }
            if(StringUtils.hasText(result)){
                if("status".indexOf(result) > 0){
                    JSONObject jsonObject = JSON.parseObject(result);
                    if(!"200".equals(jsonObject.get("status").toString())){
                        throw new RuntimeException("the request url: " +template.getRequest() +" has " +jsonObject.get("error").toString());
                    }
                }
                //
                if("java.lang.String".equals(super.getClazz().getName())){
                    return result;
                }
                Object obj =super.getClazz().newInstance();
                return JSON.parseObject(result, obj.getClass());
            }
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InstantiationException ex){
            ex.printStackTrace();
        }
        return null;
    }
    //获取HttpClient 实例
    private HttpClient newHttpClientInstance(){
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        return httpClient;
    }

}
