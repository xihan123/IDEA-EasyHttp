package cn.xihan.lib.easyhttp.model

import cn.xihan.lib.easyhttp.EasyConfig


/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/07/20
 * desc   : 请求头封装
 */
class HttpHeaders {
    /** 请求头存放集合  */
    val headers = HashMap(EasyConfig.getInstance().getHeaders())
    fun put(key: String, value: String) {
        headers[key] = value
    }

    fun remove(key: String) {
        headers.remove(key)
    }

    operator fun get(key: String): String? {
        return headers[key]
    }

    fun clear() {
        headers.clear()
    }

    val isEmpty: Boolean
        get() = headers.isEmpty()

    val keys: Set<String>
        get() = headers.keys
}