package zhenfei.liu.remoting;

import com.alibaba.fastjson.JSON;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import zhenfei.liu.template.Template;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author lzf
 * desc
 * date 2019/12/13-17:11
 */
public abstract class AbstractRequestService implements RequestService{

    private static final Class<org.springframework.web.bind.annotation.RequestParam> RequestParam = org.springframework.web.bind.annotation.RequestParam.class;
    private static final Class<RequestBody> RequestBody = org.springframework.web.bind.annotation.RequestBody.class;


    private Method method;
    private Class<?> clazz;
    private String path;

    @Override
    public  Object invoke(String requestUrl, Method method,Object[] args) throws Exception{
        this.method = method;
        Template template = new Template(method,requestUrl);
        this.clazz = template.getClazz();
        this.path = template.getRequest();
        return doInvoke(requestUrl,this.getParameterName(method,args));
    }
    //execute method
    public abstract Object doInvoke(String requestUrl, String paramer)throws Exception;

    // 获取接口请求类型 GET POST ....
    protected RequestMethod getMethodReuqesType(){
       return this.method.getDeclaredAnnotation(RequestMapping.class).method()[0];
    }
    //如果接口未设置MediaType 则默认返回application/json
    protected String getConsumes(){
        if(Objects.isNull((method.getDeclaredAnnotation(RequestMapping.class).consumes()))){
            return MediaType.APPLICATION_JSON_VALUE;
        }
        return method.getDeclaredAnnotation(RequestMapping.class).consumes()[0];
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getPath() {
        return path;
    }

    private String getParameterName(Method method, Object[] args){
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int length = parameterAnnotations.length;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<length;i++){
            for (Annotation parameterAnnotation : parameterAnnotations[i]) {
                if("org.springframework.web.bind.annotation.RequestBody".equals(parameterAnnotation.annotationType().getName())){
                    RequestBody requestBody = RequestBody.cast(parameterAnnotation);
                    return JSON.toJSONString(args[i]);
                }else if("org.springframework.web.bind.annotation.RequestParam".equals(parameterAnnotation.annotationType().getName())){
                    RequestParam requestParam = RequestParam.cast(parameterAnnotation);
                    if(!"null".equals(args[i])){
                        sb.append(requestParam.value()).append("=").append(args[i]);
                        if(i < length -1 ){
                            sb.append("&");
                        }
                    }
                }else{

                }
            }
        }
        if(sb.toString().endsWith("&")){
            return sb.toString().substring(0,sb.toString().length()-1);
        }
        return sb.toString();
    }
}
