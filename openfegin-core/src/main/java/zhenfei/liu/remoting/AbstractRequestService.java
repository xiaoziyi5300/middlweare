package zhenfei.liu.remoting;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import zhenfei.liu.template.Template;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author lzf
 * desc
 * date 2019/12/13-17:11
 */
public abstract class AbstractRequestService implements RequestService{

    private Method method;
    private Class<?> clazz;
    private String path;

    @Override
    public abstract Object invoke(Template template, String paramer) throws Exception ;

    protected void instance(Template template){
        this.method = template.getMethod();
        this.clazz = template.getClazz();
        this.path = template.getRequest();
    }

    //返回接口返回结果类型
    protected Object getReturnType(){
        try{
            return this.clazz.newInstance();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InstantiationException ex){
            ex.printStackTrace();
        }
        return null;
    }
    // 获取接口请求类型 GET POST ....
    protected RequestMethod getMethodReuqesType(){
       return this.method.getDeclaredAnnotation(RequestMapping.class).method()[0];
    }
    //如果接口未设置MediaType 则默认返回application/json
    protected String getConsumes(){
        if(Objects.isNull((method.getDeclaredAnnotation(RequestMapping.class).consumes()))){
            return MediaType.APPLICATION_JSON_VALUE;
        }
        return method.getDeclaredAnnotation(RequestMapping.class).consumes()[0];
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getPath() {
        return path;
    }
}
