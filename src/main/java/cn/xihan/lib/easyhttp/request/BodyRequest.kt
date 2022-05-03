package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.EasyConfig
import cn.xihan.lib.easyhttp.EasyLog.printJson
import cn.xihan.lib.easyhttp.EasyLog.printKeyValue
import cn.xihan.lib.easyhttp.EasyLog.printLine
import cn.xihan.lib.easyhttp.EasyLog.printLog
import cn.xihan.lib.easyhttp.EasyLog.printThrowable
import cn.xihan.lib.easyhttp.EasyUtils.convertObject
import cn.xihan.lib.easyhttp.EasyUtils.encodeString
import cn.xihan.lib.easyhttp.body.JsonBody
import cn.xihan.lib.easyhttp.body.ProgressBody
import cn.xihan.lib.easyhttp.body.TextBody
import cn.xihan.lib.easyhttp.body.UpdateBody
import cn.xihan.lib.easyhttp.listener.OnHttpListener
import cn.xihan.lib.easyhttp.listener.OnUpdateListener
import cn.xihan.lib.easyhttp.model.*
import okhttp3.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/10/07
 * desc   : 带 RequestBody 请求
 */
abstract class BodyRequest<T> : HttpRequest<T>() {
    private var mUpdateListener: OnUpdateListener<*>? = null
    private var mRequestBody: RequestBody? = null

    /**
     * 自定义 json 字符串
     */
    fun json(map: Map<*, *>?): T {
        return if (map == null) {
            this as T
        } else body(JsonBody(map))
    }

    fun json(list: List<*>?): T {
        return if (list == null) {
            this as T
        } else body(JsonBody(list))
    }

    fun json(json: String?): T {
        return if (json == null) {
            this as T
        } else body(JsonBody(json))
    }

    /**
     * 自定义文本字符串
     */
    fun text(text: String?): T {
        return if (text == null) {
            this as T
        } else body(TextBody(text))
    }

    /**
     * 自定义 RequestBody
     */
    fun body(body: RequestBody?): T {
        mRequestBody = body
        return this as T
    }

    /**
     * 执行异步请求（执行传入上传进度监听器）
     */
    override fun request(listener: OnHttpListener<*>?) {
        if (listener is OnUpdateListener<*>) {
            mUpdateListener = listener
        }
        if (mRequestBody != null) {
            mRequestBody = ProgressBody(this, mRequestBody!!, mUpdateListener)
        }
        super.request(listener)
    }

    override fun addHttpParams(params: HttpParams, key: String?, value: Any?, bodyType: BodyType?) {
        when (bodyType) {
            BodyType.JSON ->                 // Json 提交
                params.put(key!!, convertObject(value!!))
            BodyType.FORM ->                 // 表单提交
                params.put(key!!, value!!)
            else -> params.put(key!!, value!!)
        }
    }

    override fun addRequestParams(requestBuilder: Request.Builder, params: HttpParams, type: BodyType?) {
        val body = if (mRequestBody != null) mRequestBody!! else createRequestBody(params, type)
        requestBuilder.method(requestMethod, body)
    }

    override fun printRequestLog(request: Request, params: HttpParams, headers: HttpHeaders, type: BodyType?) {
        if (!EasyConfig.getInstance().isLogEnabled()) {
            return
        }
        printKeyValue(this, "RequestUrl", request.url.toString())
        printKeyValue(this, "RequestMethod", requestMethod)
        val body = request.body

        // 打印请求头和参数的日志
        if (!headers.isEmpty || !params.isEmpty) {
            printLine(this)
        }
        for (key in headers.keys) {
            printKeyValue(this, key, headers[key])
        }
        if (!headers.isEmpty && !params.isEmpty) {
            printLine(this)
        }
        if (body is FormBody ||
            body is MultipartBody ||
            body is ProgressBody
        ) {
            // 打印表单
            for (key in params.keys) {
                val value = params[key]
                if (value is Map<*, *>) {
                    // 如果这是一个 Map 集合
                    for (itemKey in value.keys) {
                        if (itemKey == null) {
                            continue
                        }
                        printKeyValue(itemKey.toString(), value[itemKey]!!)
                    }
                } else if (value is List<*>) {
                    // 如果这是一个 List 集合
                    for (i in value.indices) {
                        val itemValue = value[i]!!
                        printKeyValue("$key[$i]", itemValue)
                    }
                } else {
                    printKeyValue(key, value!!)
                }
            }
        } else if (body is JsonBody) {
            // 打印 Json
            printJson(this, body.toString())
        } else if (body != null) {
            // 打印文本
            printLog(this, body.toString())
        }
        if (!headers.isEmpty || !params.isEmpty) {
            printLine(this)
        }
    }

    /**
     * 组装 RequestBody 对象
     */
    private fun createRequestBody(params: HttpParams, type: BodyType?): RequestBody {
        val requestBody: RequestBody
        requestBody = if (params.isMultipart && !params.isEmpty) {
            val bodyBuilder = MultipartBody.Builder()
            bodyBuilder.setType(MultipartBody.FORM)
            for (key in params.keys) {
                val value = params[key]
                if (value is Map<*, *>) {
                    // 如果这是一个 Map 集合
                    val map = value
                    for (itemKey in map.keys) {
                        if (itemKey == null) {
                            continue
                        }
                        val itemValue = map[itemKey] ?: continue
                        addFormData(bodyBuilder, itemKey.toString(), itemValue)
                    }
                } else if (value is List<*>) {
                    // 如果这是一个 List 集合
                    for (itemValue in value) {
                        if (itemValue == null) {
                            continue
                        }
                        addFormData(bodyBuilder, key, itemValue)
                    }
                } else {
                    addFormData(bodyBuilder, key, value)
                }
            }
            try {
                bodyBuilder.build()
            } catch (ignored: IllegalStateException) {
                // 如果参数为空则会抛出异常：Multipart body must have at least one part.
                FormBody.Builder().build()
            }
        } else if (type === BodyType.JSON) {
            JsonBody(params.params)
        } else {
            val bodyBuilder = FormBody.Builder()
            if (!params.isEmpty) {
                for (key in params.keys) {
                    val value = params[key]
                    if (value is List<*>) {
                        for (itemValue in value) {
                            if (itemValue == null) {
                                continue
                            }
                            bodyBuilder.add(key, itemValue.toString())
                        }
                        continue
                    }
                    bodyBuilder.add(key, value.toString())
                }
            }
            bodyBuilder.build()
        }
        return if (mUpdateListener == null) requestBody else ProgressBody(this, requestBody, mUpdateListener)
    }

    /**
     * 添加参数
     */
    private fun addFormData(bodyBuilder: MultipartBody.Builder, key: String, `object`: Any?) {
        if (`object` is File) {
            // 如果这是一个 File 对象
            val file = `object`
            val fileName = file.name

            // 文件名必须不能带中文，所以这里要编码
            val encodeFileName = encodeString(fileName)
            try {
                val part = MultipartBody.Part.createFormData(key, encodeFileName, UpdateBody(file))
                bodyBuilder.addPart(part)
            } catch (e: FileNotFoundException) {
                // 文件不存在，将被忽略上传
                printLog(
                    this, "File does not exist, will be ignored upload: " +
                            key + " = " + file.path
                )
            }
        } else if (`object` is InputStream) {
            // 如果这是一个 InputStream 对象
            try {
                bodyBuilder.addPart(MultipartBody.Part.createFormData(key, null, UpdateBody(`object`, key)))
            } catch (e: IOException) {
                printThrowable(this, e)
            }
        } else if (`object` is RequestBody) {
            // 如果这是一个自定义的 RequestBody 对象
            val requestBody = `object`
            if (requestBody is UpdateBody) {
                bodyBuilder.addPart(
                    MultipartBody.Part.createFormData(
                        key, encodeString(
                            requestBody.keyName
                        ), requestBody
                    )
                )
            } else {
                bodyBuilder.addPart(MultipartBody.Part.createFormData(key, null, requestBody))
            }
        } else if (`object` is MultipartBody.Part) {
            // 如果这是一个自定义的 MultipartBody.Part 对象
            bodyBuilder.addPart(`object`)
        } else {
            // 如果这是一个普通参数
            bodyBuilder.addFormDataPart(key, `object`.toString())
        }
    }
}