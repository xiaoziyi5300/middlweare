package open.fegin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lzf
 * desc
 * date 2019/11/22-14:28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyFeginClient {

    //application name
    String name()default "";
    //invoke http url
    String url()default "";
}
