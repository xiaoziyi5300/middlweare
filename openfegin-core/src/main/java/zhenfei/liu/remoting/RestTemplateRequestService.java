package zhenfei.liu.remoting;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import zhenfei.liu.template.Template;
import zhenfei.liu.util.SpringContextUtil;

import java.util.Objects;

/**
 * @author lzf
 * desc
 * date 2019/12/13-17:17
 */
public class RestTemplateRequestService extends AbstractRequestService {

    @Override
    public Object doInvoke(String requestUrl, String paramer) {
        //获取restTemplate 对象
        RestTemplate restTemplate = this.restTemplate();
        if(RequestMethod.GET.equals(super.getMethodReuqesType())){
            return (Object)restTemplate.getForObject(requestUrl + "?" + paramer,super.getClazz());
        }else{
            HttpHeaders requestHeaders = new HttpHeaders();
            if("application/json".equals(super.getConsumes())){
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            }else{
                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            }
            HttpEntity<String> requestEntity = new HttpEntity<String>(paramer, requestHeaders);
            return (Object)restTemplate.postForObject(requestUrl,requestEntity,super.getClazz());
        }
    }

    //获取 RestTemplate
    private RestTemplate restTemplate(){
        RestTemplate restTemplate = (RestTemplate) SpringContextUtil.getBean("restTemplate");
        if(Objects.isNull(restTemplate)){
            restTemplate = SpringContextUtil.getBean(RestTemplate.class);
        }
        if(Objects.isNull(restTemplate)){
            throw new RuntimeException("spring restTemplate must no be null");
        }
        return restTemplate;
    }
}
