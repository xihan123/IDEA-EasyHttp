package cn.xihan.lib.easyhttp.model

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 请求方式
 */
enum class HttpMethod(
    /** 请求方式  */
    private val mMethod: String
) {
    /** GET 请求  */
    GET("GET"),

    /** Post 请求  */
    POST("POST"),

    /** Head 请求  */
    HEAD("HEAD"),

    /** Delete 请求  */
    DELETE("DELETE"),

    /** Put 请求  */
    PUT("PUT"),

    /** Patch 请求  */
    PATCH("PATCH"),

    /** Options 请求  */
    OPTIONS("OPTIONS"),

    /** Trace 请求  */
    TRACE("TRACE");

    override fun toString(): String {
        return mMethod
    }
}