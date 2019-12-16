package zhenfei.liu.loadBalance;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author lzf
 * desc 随机
 * date 2019/12/12-11:24
 */
public class RandomLoadBalance implements LoadBalanceService {

    @Override
    public ServiceInstance getServer(List<ServiceInstance> serviceInstanceList) {
        if(serviceInstanceList.size() == 1){
            return serviceInstanceList.get(0);
        }
        java.util.Random random = new java.util.Random();
        int randomPos = random.nextInt(serviceInstanceList.size());
        return serviceInstanceList.get(randomPos);
    }
}
