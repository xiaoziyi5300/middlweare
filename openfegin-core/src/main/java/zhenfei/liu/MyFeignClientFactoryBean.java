package zhenfei.liu;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;


/**
 * @author lzf
 * desc
 * date 2019/11/25-10:15
 */
@Data
public class MyFeignClientFactoryBean<T> implements FactoryBean<Object>,ApplicationContextAware {
    private Class<T> type;
    private boolean addToConfig = true;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public T getObject() throws Exception {
        //封装请求参数
        ProxyFegin proxyFegin = new ProxyFegin<T>(type);
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
}
