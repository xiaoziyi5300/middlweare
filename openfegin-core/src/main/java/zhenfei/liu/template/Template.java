package zhenfei.liu.template;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;

/**
 * @author lzf
 * desc
 * date 2019/12/4-11:15
 */
@Data
public class Template {

    private Method method;
    private Class<?> clazz;
    private String consumes;

    private String url;
    private String path;
    private String request;
    private String data;
    private RequestMethod methodType;


    public Template(Method method, String url){
        this.method = method;
        this.url = url;
        this.path = method.getDeclaredAnnotation(RequestMapping.class).value()[0];
        this.methodType = method.getDeclaredAnnotation(RequestMapping.class).method()[0];
        this.consumes = method.getDeclaredAnnotation(RequestMapping.class).consumes()[0];
        this.request = this.url + this.path;
        clazz = method.getReturnType();
    }
}
