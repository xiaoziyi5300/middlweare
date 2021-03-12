package zhenfei.liu;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import zhenfei.liu.loadBalance.LoadBalanceService;
import zhenfei.liu.loadBalance.RoundRobinLoadBalance;
import zhenfei.liu.remoting.RequestService;

import java.lang.reflect.Proxy;
import java.util.List;


/**
 * @author lzf
 * desc
 * date 2019/11/25-10:15
 */
@Data
@Component
public class MyFeignClientFactoryBean<T> implements FactoryBean<Object>, ApplicationContextAware, InitializingBean {

    private Class<T> type;
    private String url;
    private String name;
    private ApplicationContext applicationContext;
    private DiscoveryClient discoveryClient;
    private LoadBalanceService loadBalanceService;

    @Autowired
    private RequestService requestService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public T getObject() throws Exception {

        String newUrl = url;
        if(StringUtils.hasText(url)){
            if(!url.startsWith("http://")){
                newUrl = "http://" + url;
            }
        }else {
            List<ServiceInstance> serviceInstanceList= discoveryClient.getInstances(name);
            if(CollectionUtils.isEmpty(serviceInstanceList)){
                throw new RuntimeException("no server with " + name +" ..");
            }
            //如果loadBalanceService == null 则默认使用 轮询策略
            ServiceInstance serviceInstance = loadBalanceService.getServer(serviceInstanceList);
            newUrl = serviceInstance.getUri().toString();
        }
        //封装请求参数
        ProxyFeginHandler proxyFegin = new ProxyFeginHandler<T>(type, requestService,newUrl);
        T userService =  (T)Proxy.newProxyInstance(
                proxyFegin.getClass().getClassLoader(), new Class[]{type},
                proxyFegin);
        return userService;
    }
    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        discoveryClient = applicationContext.getBean("discoveryClient", DiscoveryClient.class);
        try {
            loadBalanceService = applicationContext.getBean(LoadBalanceService.class);
        } catch (BeansException e) {
            loadBalanceService = new RoundRobinLoadBalance();
        }
    }

}
