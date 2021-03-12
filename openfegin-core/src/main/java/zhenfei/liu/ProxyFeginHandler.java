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
public class ProxyFeginHandler<T> implements InvocationHandler {

    // 需要代理的对象
    private Class<T> target;
    private RequestService requestService;
    private String url;

    // 接受需要代理对象
    public ProxyFeginHandler(Class<T> target, RequestService requestService, String url) {
        this.target = target;
        this.requestService = requestService;
        this.url = url;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        validate(proxy,method,target);
        return requestService.invoke(url,method,args);
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
