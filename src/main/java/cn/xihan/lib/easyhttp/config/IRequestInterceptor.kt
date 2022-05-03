package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.model.HttpHeaders
import cn.xihan.lib.easyhttp.model.HttpParams
import cn.xihan.lib.easyhttp.request.HttpRequest
import okhttp3.Request
import okhttp3.Response

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/08/05
 * desc   : 请求参数拦截器
 */
interface IRequestInterceptor {
    /**
     * 拦截参数
     *
     * @param httpRequest   接口对象
     * @param params        请求参数
     * @param headers       请求头参数
     */
    fun interceptArguments(httpRequest: HttpRequest<*>, params: HttpParams, headers: HttpHeaders) {}

    /**
     * 拦截请求头
     *
     * @param httpRequest   接口对象
     * @param request       请求头对象
     * @return              返回新的请求头
     */
    fun interceptRequest(httpRequest: HttpRequest<*>?, request: Request?): Request? {
        return request
    }

    /**
     * 拦截器响应头
     *
     * @param httpRequest   接口对象
     * @param response      响应头对象
     * @return              返回新的响应头
     */
    fun interceptResponse(httpRequest: HttpRequest<*>?, response: Response?): Response? {
        return response
    }
}