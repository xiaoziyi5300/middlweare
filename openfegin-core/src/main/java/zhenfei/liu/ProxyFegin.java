package zhenfei.liu;

import com.alibaba.fastjson.JSON;
import open.fegin.annotation.MyFeginClient;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import zhenfei.liu.loadBalance.LoadBalanceService;
import zhenfei.liu.loadBalance.RoundRobinLoadBalance;
import zhenfei.liu.remoting.RequestService;
import zhenfei.liu.remoting.RequestServiceFactory;
import zhenfei.liu.template.Template;
import zhenfei.liu.util.SpringContextUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author lzf
 * desc
 * date 2019/11/25-18:42
 */
public class ProxyFegin<T> implements InvocationHandler {


    private static final Class<org.springframework.web.bind.annotation.RequestParam> RequestParam = org.springframework.web.bind.annotation.RequestParam.class;
    private static final Class<RequestBody> RequestBody = org.springframework.web.bind.annotation.RequestBody.class;

    private DiscoveryClient discoveryClient;
    private LoadBalanceService loadBalanceService;

    // 需要代理的对象
    private Class<T> target;

    // 接受需要代理对象
    public ProxyFegin(Class<T> target, DiscoveryClient discoveryClient, LoadBalanceService loadBalanceService) {
        this.target = target;
        this.discoveryClient = discoveryClient;
        this.loadBalanceService = loadBalanceService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        validate(proxy,method,target);
        String appName = target.getAnnotation(MyFeginClient.class).name();
        String url = target.getAnnotation(MyFeginClient.class).url();
        String parame = this.getParameterName(method.getParameterAnnotations(),args);

        //获取具体的请求实例
        Template template = new Template(method,url);
        RequestService requestService = RequestServiceFactory.getRequestService();
        /**
         * 接口上有写url值 则直接分装请求 调用 忽略 application-name的值
         * 反之 使用application-name 从eurek server 获取serverList 根据负载均衡算法找一个实例
         */
        if(StringUtils.hasText(url)){
            String newUrl = url;
            if(!url.startsWith("http://")){
                newUrl = "http://" + url;
            }
            template.setUrl(newUrl);
            template.setRequest(newUrl + template.getPath());
            return requestService.invoke(template,parame);
        }else{
            List<ServiceInstance> serviceInstanceList= discoveryClient.getInstances(appName);
            if(CollectionUtils.isEmpty(serviceInstanceList)){
                throw new RuntimeException("no server with " + appName +" ..");
            }
            //如果loadBalanceService == null 则默认使用 轮询策略
            ServiceInstance serviceInstance = loadBalanceService.getServer(serviceInstanceList);
            template.setUrl(serviceInstance.getUri().toString());
            template.setRequest(serviceInstance.getUri().toString() + template.getRequest());
            return requestService.invoke(template,parame);
        }
    }


    private String getParameterName(Annotation[][] parameterAnnotations,Object[] args){
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
    //校验server interface 是否有对应的注解
    private void validate(Object proxy, Method method,Class<T> target){
        if(Objects.isNull(target.getAnnotation(MyFeginClient.class))){
            throw new RuntimeException("the annotation MyFeginClient not be null");
        }
        if(Objects.isNull(method.getDeclaredAnnotation(RequestMapping.class))){
            throw new RuntimeException("the annotation RequestMapping not be null");
        }
        if(!StringUtils.hasText(target.getAnnotation(MyFeginClient.class).name()) && !StringUtils.hasText(target.getAnnotation(MyFeginClient.class).url())){
            throw new RuntimeException("the MyFeginClient name or url must not be null");
        }
        RequestMethod[] methods = method.getDeclaredAnnotation(RequestMapping.class).method();
        for(RequestMethod requestMethod :methods){
            if(!RequestMethod.GET.equals(requestMethod) && !RequestMethod.POST.equals(requestMethod) ){
                throw new RuntimeException("request method mediaType  only surpport GET and POST");
            }
        }
    }
}
