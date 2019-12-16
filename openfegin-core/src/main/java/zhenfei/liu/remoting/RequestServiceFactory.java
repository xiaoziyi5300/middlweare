package zhenfei.liu.remoting;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import zhenfei.liu.util.SpringContextUtil;


/**
 * @author lzf
 * desc RequestService 工厂
 * date 2019/12/16-11:10
 */
public class RequestServiceFactory {

    private static RequestService requestService = new HttpClientRequestService();

    //如果没有指定RequestService 实现  则默认使用httpclient
    public static RequestService getRequestService(){
        RequestService service = null;
        try{
            //从springContext上下文获取RequestService 使用默认的名称来获取 如果没获取到 使用class获取 如果都没获取到 使用默认的RequestService
            service = (RequestService) SpringContextUtil.getBean("requestService");
            if(null == service){
                service = (RequestService) SpringContextUtil.getBean(RequestService.class);
            }
        }catch (NoSuchBeanDefinitionException e){
            service = requestService;
        }
        return service;
    }

}
