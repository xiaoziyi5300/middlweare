package zhenfei.liu.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author lzf
 * desc
 * date 2019/11/27-16:50
 */
public class HttpClientHelper {

    private static Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);


    /**
     * 发起POST请求
     *
     * @param url       url
     * @param paramJson 参数的json格式
     */
    public static String sendPost(HttpClient httpClient,String url,String paramJson,String contentType) {
        logger.info("开始发起POST请求，请求地址为{}，参数为{}", url, paramJson);
        // 创建httpClient实例对象
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(url);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        postMethod.addRequestHeader("Content-Type", contentType);
        try {
            //json格式的参数解析
            RequestEntity entity = new StringRequestEntity(paramJson, contentType, "UTF-8");
            postMethod.setRequestEntity(entity);

            httpClient.executeMethod(postMethod);
            String result = postMethod.getResponseBodyAsString();
            postMethod.releaseConnection();
            return result;
        } catch (IOException e) {
            logger.error("POST请求发出失败，请求的地址为{}，参数为{}，错误信息为{}", url, paramJson, e.getMessage(), e);
        }finally {
            postMethod.releaseConnection();
        }
        return null;
    }
    /**
     * 发起GET请求
     *
     * @param urlParam url请求，包含参数
     */
    public static String sendGet(HttpClient httpClient,String urlParam) {
        logger.info("开始发起GET请求，请求地址为{}", urlParam);
        // 创建httpClient实例对象
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        // 创建GET请求方法实例对象
        GetMethod getMethod = new GetMethod(urlParam);
        // 设置post请求超时时间
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        getMethod.addRequestHeader("Content-Type", "application/json");
        try {
            httpClient.executeMethod(getMethod);
            String result = getMethod.getResponseBodyAsString();
            getMethod.releaseConnection();
            logger.info("返回信息为{}", result);
            return result;
        } catch (IOException e) {
            logger.error("GET请求发出失败，请求的地址为{}，错误信息为{}", urlParam, e.getMessage(), e);
        }finally {
            getMethod.releaseConnection();
        }
        return null;
    }
}
