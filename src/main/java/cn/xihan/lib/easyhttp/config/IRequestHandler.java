package cn.xihan.lib.easyhttp.config;


import cn.xihan.lib.easyhttp.EasyUtils;
import cn.xihan.lib.easyhttp.request.HttpRequest;
import okhttp3.Response;

import java.lang.reflect.Type;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/11/25
 *    desc   : 请求处理器
 */
public interface IRequestHandler {

    /**
     * 请求成功时回调
     *
     * @param httpRequest   请求接口对象
     * @param response      响应对象
     * @param type          解析类型
     * @return              返回结果
     *
     * @throws Exception    如果抛出则回调失败
     */
    Object requestSucceed(HttpRequest<?> httpRequest, Response response, Type type) throws Exception;

    /**
     * 请求失败
     *
     * @param httpRequest   请求接口对象
     * @param e             错误对象
     * @return              错误对象
     */
    Exception requestFail(HttpRequest<?> httpRequest, Exception e);

    /**
     * 下载失败
     *
     * @param httpRequest   请求接口对象
     * @param e             错误对象
     * @return              错误对象
     */
    default Exception downloadFail(HttpRequest<?> httpRequest, Exception e) {
        return requestFail(httpRequest, e);
    }

    /**
     * 解析泛型
     */
    default Type getType(Object object) {
        return EasyUtils.getGenericType(object);
    }
}