package zhenfei.liu.loadBalance;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author lzf
 * desc 轮询
 * date 2019/12/12-11:28
 */
public class RoundRobinLoadBalance implements LoadBalanceService {

    private static Integer pos = 0;

    @Override
    public ServiceInstance getServer(List<ServiceInstance> serviceInstanceList) {
        ServiceInstance serviceInstance = null;
        synchronized(pos){
            if (pos >= serviceInstanceList.size())
                pos = 0;
            serviceInstance = serviceInstanceList.get(pos);
            pos ++ ;
        }
        return serviceInstance;
    }
}
