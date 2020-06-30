package zhenfei.liu;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import zhenfei.liu.loadBalance.LoadBalanceService;
import zhenfei.liu.loadBalance.RoundRobinLoadBalance;

import java.lang.reflect.Proxy;
import java.util.Objects;


/**
 * @author lzf
 * desc
 * date 2019/11/25-10:15
 */
@Data
public class MyFeignClientFactoryBean<T> implements FactoryBean<Object>, ApplicationContextAware, InitializingBean {
    private Class<T> type;
    private boolean addToConfig = true;
    private ApplicationContext applicationContext;
    private DiscoveryClient discoveryClient;
    private LoadBalanceService loadBalanceService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public T getObject() throws Exception {
        //封装请求参数
        ProxyFegin proxyFegin = new ProxyFegin<T>(type, discoveryClient, loadBalanceService);
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
