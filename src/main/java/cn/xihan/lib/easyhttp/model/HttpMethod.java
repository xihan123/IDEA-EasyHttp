package cn.xihan.lib.easyhttp.model;



/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/05/19
 *    desc   : 请求方式
 */
public enum HttpMethod {

    /** GET 请求 */
    GET("GET"),

    /** Post 请求 */
    POST("POST"),

    /** Head 请求 */
    HEAD("HEAD"),

    /** Delete 请求 */
    DELETE("DELETE"),

    /** Put 请求 */
    PUT("PUT"),

    /** Patch 请求 */
    PATCH("PATCH"),

    /** Options 请求 */
    OPTIONS("OPTIONS"),

    /** Trace 请求 */
    TRACE("TRACE");

    /** 请求方式 */
    private final String mMethod;

    HttpMethod(String method) {
        this.mMethod = method;
    }

    @Override
    public String toString() {
        return mMethod;
    }
}