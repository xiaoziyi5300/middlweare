package zhenfei.liu.remoting;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author lzf
 * desc RequestService 工厂
 * date 2019/12/16-11:10
 */
@Configuration
public class RequestServiceFactory {

    @Bean
    @ConditionalOnMissingClass
    public RequestService httpRequestService(){
        return new HttpClientRequestService();
    }

}
