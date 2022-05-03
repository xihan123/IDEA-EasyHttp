package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.EasyUtils.getGenericType
import cn.xihan.lib.easyhttp.request.HttpRequest
import okhttp3.Response
import java.lang.reflect.Type

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/11/25
 * desc   : 请求处理器
 */
interface IRequestHandler {
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
    @Throws(Exception::class)
    fun requestSucceed(httpRequest: HttpRequest<*>?, response: Response?, type: Type?): Any?

    /**
     * 请求失败
     *
     * @param httpRequest   请求接口对象
     * @param e             错误对象
     * @return              错误对象
     */
    fun requestFail(httpRequest: HttpRequest<*>?, e: Exception?): Exception?

    /**
     * 读取缓存
     *
     * @param httpRequest   请求接口对象
     * @param cacheTime     缓存的有效期（以毫秒为单位）
     * @return              返回新的请求对象
     */
    fun readCache(httpRequest: HttpRequest<*>?, type: Type?, cacheTime: Long): Any? {
        return null
    }

    /**
     * 写入缓存
     *
     * @param httpRequest   请求接口对象
     * @param result        请求结果对象
     * @return              缓存写入结果
     */
    fun writeCache(httpRequest: HttpRequest<*>?, response: Response?, result: Any?): Boolean {
        return false
    }

    /**
     * 清空缓存
     */
    fun clearCache() {}

    /**
     * 解析泛型
     */
    fun getType(`object`: Any?): Type? {
        return getGenericType(`object`)
    }
}