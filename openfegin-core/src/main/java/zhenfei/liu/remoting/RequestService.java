package zhenfei.liu.remoting;


import zhenfei.liu.template.Template;

/**
 * @author lzf
 * desc 请求方式接口
 * date 2019/12/13-17:08
 */
public interface RequestService {

    //请求调用远程接口
    Object invoke(Template template, String paramer)throws Exception;
}
