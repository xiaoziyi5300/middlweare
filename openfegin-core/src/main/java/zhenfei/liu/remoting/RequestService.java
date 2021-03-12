package zhenfei.liu.remoting;


import zhenfei.liu.template.Template;

import java.lang.reflect.Method;

/**
 * @author lzf
 * desc 请求方式接口
 * date 2019/12/13-17:08
 */
public interface RequestService {

    //请求调用远程接口
    Object invoke(String requestUrl,Method method,Object[] args )throws Exception;
}
