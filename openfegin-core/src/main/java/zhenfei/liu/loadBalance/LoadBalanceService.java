package zhenfei.liu.loadBalance;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author lzf
 * desc 服务 interface
 * date 2019/12/12-10:57
 */
public interface LoadBalanceService {

    ServiceInstance getServer(List<ServiceInstance> serviceInstanceList);
}
