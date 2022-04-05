package cn.xihan.http.config;



import cn.xihan.http.model.HttpHeaders;
import cn.xihan.http.model.HttpParams;
import cn.xihan.http.request.HttpRequest;
import okhttp3.Request;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2020/08/05
 *    desc   : 请求参数拦截器
 */
public interface IRequestInterceptor {

    /**
     * 拦截参数
     *
     * @param httpRequest   接口对象
     * @param params        请求参数
     * @param headers       请求头参数
     */
    default void interceptArguments(HttpRequest<?> httpRequest, HttpParams params, HttpHeaders headers) {}

    /**
     * 拦截请求头
     *
     * @param httpRequest   接口对象
     * @param request       请求头对象
     * @return              返回新的请求头
     */

    default Request interceptRequest(HttpRequest<?> httpRequest, Request request) {
        return request;
    }

    /**
     * 拦截器响应头
     *
     * @param httpRequest   接口对象
     * @param response      响应头对象
     * @return              返回新的响应头
     */

    default Response interceptResponse(HttpRequest<?> httpRequest, Response response) {
        return response;
    }
}