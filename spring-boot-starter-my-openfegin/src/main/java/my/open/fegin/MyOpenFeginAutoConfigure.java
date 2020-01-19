package my.open.fegin;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zhenfei.liu.loadBalance.LoadBalanceService;
import zhenfei.liu.loadBalance.RoundRobinLoadBalance;
import zhenfei.liu.remoting.HttpClientRequestService;
import zhenfei.liu.remoting.RequestService;

/**
 * @author lzf
 * desc
 * date 2019/12/16-15:57
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "open.fegin.client", value = "enabled", havingValue = "true")
public class MyOpenFeginAutoConfigure {


    /**
     * 默认http invoke client
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestService requestService() {
        return new HttpClientRequestService();
    }

    /**
     * 负载均衡配置
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(org.springframework.cloud.client.ServiceInstance.class)
    public LoadBalanceService loadBalanceService() {
        return new RoundRobinLoadBalance();
    }
}
