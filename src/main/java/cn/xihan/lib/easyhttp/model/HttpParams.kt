package cn.xihan.lib.easyhttp.model

import cn.xihan.lib.easyhttp.EasyConfig


/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/07/20
 * desc   : 请求参数封装
 */
class HttpParams {
    /** 请求参数存放集合  */
    val params = HashMap(EasyConfig.getInstance().getParams())

    /** 是否有流参数  */
    var isMultipart = false
    fun put(key: String, value: Any) {
        params[key] = value
    }

    fun remove(key: String) {
        params.remove(key)
    }

    operator fun get(key: String): Any? {
        return params[key]
    }

    fun clear() {
        params.clear()
    }

    val isEmpty: Boolean
        get() = params.isEmpty()
    val keys: Set<String>
        get() = params.keys
}