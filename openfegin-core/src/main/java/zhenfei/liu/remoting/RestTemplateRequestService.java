package zhenfei.liu.remoting;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import zhenfei.liu.template.Template;

import java.util.Objects;

/**
 * @author lzf
 * desc
 * date 2019/12/13-17:17
 */
public class RestTemplateRequestService extends AbstractRequestService {


    private RestTemplate restTemplate;

    public RestTemplateRequestService() {
    }

    ;

    public RestTemplateRequestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Object doInvoke(Template template, String paramer) {
        if(RequestMethod.GET.equals(super.getMethodReuqesType())){
            return (Object) restTemplate.getForObject(template.getRequest() + "?" + paramer, super.getClazz());
        }else{
            HttpHeaders requestHeaders = new HttpHeaders();
            if("application/json".equals(super.getConsumes())){
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            }else{
                requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            }
            HttpEntity<String> requestEntity = new HttpEntity<String>(paramer, requestHeaders);
            return (Object) restTemplate.postForObject(template.getRequest(), requestEntity, super.getClazz());
        }
    }
}
