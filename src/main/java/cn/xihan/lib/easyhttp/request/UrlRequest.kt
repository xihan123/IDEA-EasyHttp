package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.EasyConfig
import cn.xihan.lib.easyhttp.EasyLog.printKeyValue
import cn.xihan.lib.easyhttp.EasyLog.printLine
import cn.xihan.lib.easyhttp.model.*
import okhttp3.*


/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/10/07
 * desc   : 不带 RequestBody 的请求
 */
abstract class UrlRequest<T> : HttpRequest<T>() {
    override fun addHttpParams(params: HttpParams, key: String?, value: Any?, type: BodyType?) {
        if (key.isNullOrEmpty() && value == null) {
            return
        }
        params.put(key!!, value!!)
    }

    override fun addRequestParams(requestBuilder: Request.Builder, params: HttpParams, type: BodyType?) {
        val urlBuilder: HttpUrl.Builder = requestBuilder.build().url.newBuilder()
        // 添加参数
        if (!params.isEmpty) {
            for (key in params.keys) {
                val value = params[key]
                if (value is List<*>) {
                    // 如果这是一个 List 集合
                    for (itemValue in value) {
                        if (itemValue == null) {
                            continue
                        }
                        // Get 请求参数重复拼接：https://blog.csdn.net/weixin_38355349/article/details/104499948
                        urlBuilder.addQueryParameter(key, itemValue.toString())
                    }
                } else if (value is HashMap<*, *>) {
                    // 如果这是一个 Map 集合
                    val map = value as Map<*, *>
                    for (itemKey in map.keys) {
                        if (itemKey == null) {
                            continue
                        }
                        val itemValue = map[itemKey] ?: continue
                        urlBuilder.addQueryParameter(key, itemValue.toString())
                    }
                } else {
                    urlBuilder.addQueryParameter(key, value.toString())
                }
            }
        }
        val link: HttpUrl = urlBuilder.build()
        requestBuilder.url(link)
        requestBuilder.method(requestMethod, null)
    }

    override fun printRequestLog(request: Request, params: HttpParams, headers: HttpHeaders, type: BodyType?) {
        if (!EasyConfig.getInstance().isLogEnabled()) {
            return
        }
        printKeyValue(this, "RequestUrl", request.url.toString())
        printKeyValue(this, "RequestMethod", requestMethod)
        if (!headers.isEmpty || !params.isEmpty) {
            printLine(this)
        }
        for (key in headers.keys) {
            printKeyValue(this, key, headers[key])
        }
        if (!headers.isEmpty && !params.isEmpty) {
            printLine(this)
        }
        for (key in params.keys) {
            val value = params[key]
            if (value is List<*>) {
                // 如果这是一个 List 集合
                val list = value
                for (i in list.indices) {
                    printKeyValue("$key[$i]", list[i]!!)
                }
            } else if (value is HashMap<*, *>) {
                // 如果这是一个 Map 集合
                val map = value as Map<*, *>
                for (itemKey in map.keys) {
                    if (itemKey == null) {
                        continue
                    }
                    printKeyValue(itemKey.toString(), map[itemKey]!!)
                }
            } else {
                printKeyValue(key, params[key].toString())
            }
        }
        if (!headers.isEmpty || !params.isEmpty) {
            printLine(this)
        }
    }
}