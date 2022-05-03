package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.EasyConfig
import cn.xihan.lib.easyhttp.EasyLog.printKeyValue
import cn.xihan.lib.easyhttp.EasyLog.printLog
import cn.xihan.lib.easyhttp.EasyLog.printStackTrace
import cn.xihan.lib.easyhttp.EasyLog.printThrowable
import cn.xihan.lib.easyhttp.EasyUtils.encodeString
import cn.xihan.lib.easyhttp.EasyUtils.getObjectTag
import cn.xihan.lib.easyhttp.EasyUtils.isMultipartParameter
import cn.xihan.lib.easyhttp.EasyUtils.postDelayed
import cn.xihan.lib.easyhttp.annotation.HttpHeader
import cn.xihan.lib.easyhttp.annotation.HttpIgnore
import cn.xihan.lib.easyhttp.annotation.HttpRename
import cn.xihan.lib.easyhttp.callback.NormalCallback
import cn.xihan.lib.easyhttp.config.*
import cn.xihan.lib.easyhttp.listener.OnHttpListener
import cn.xihan.lib.easyhttp.model.*
import okhttp3.*

import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/07/20
 * desc   : 所有请求方式的基类
 */
abstract class HttpRequest<T> {
    /**
     * 获取请求接口对象
     */
    /** 请求接口配置  */
    var requestApi: IRequestApi? = null
        private set
    /**
     * 获取请求主机地址
     */
    /** 接口主机地址  */
    var requestHost: IRequestHost? = EasyConfig.getInstance().getServer()
        private set
    /**
     * 获取参数提交方式
     */
    /** 提交参数类型  */
    var requestType: IRequestType? = EasyConfig.getInstance().getServer()
        private set
    /**
     * 获取请求缓存策略
     */
    /** 接口缓存方式  */
    var requestCache: IRequestCache? = EasyConfig.getInstance().getServer()
        private set
    /**
     * 获取请求的 OkHttpClient 对象
     */
    /** OkHttp 客户端  */
    var requestClient: IRequestClient? = EasyConfig.getInstance().getServer()
        private set
    /**
     * 获取请求处理对象
     */
    /** 请求处理策略  */
    var requestHandler = EasyConfig.getInstance().getHandler()
        private set
    /**
     * 获取请求的拦截器（可能为空）
     */
    /** 请求拦截策略  */
    var requestInterceptor = EasyConfig.getInstance().getInterceptor()
        private set

    /** 请求执行代理类  */
    private var mCallProxy: CallProxy? = null
    /**
     * 获取请求标记
     */
    /** 请求标记  */
    var tag: String = ""
    /**
     * 获取延迟请求时间
     */
    /** 请求延迟  */
    protected var delayMillis: Long = 0
        private set

    fun api(api: Class<out IRequestApi?>): T {
        return try {
            api(api.newInstance())
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    fun api(api: String): T {
        return api(RequestApi(api))
    }

    /**
     * 设置请求配置
     */
    fun api(api: IRequestApi?): T {
        requestApi = api
        if (api is IRequestHost) {
            requestHost = api
        }
        if (api is IRequestClient) {
            requestClient = api
        }
        if (api is IRequestType) {
            requestType = api
        }
        if (api is IRequestCache) {
            requestCache = api
        }
        if (api is IRequestHandler) {
            requestHandler = api
        }
        if (api is IRequestInterceptor) {
            requestInterceptor = api
        }
        return this as T
    }

    fun server(api: Class<out IRequestServer?>): T {
        return try {
            server(api.newInstance())
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    fun server(host: String?): T {
        return server(RequestServer(host!!))
    }

    /**
     * 替换默认的服务器配器（推荐使用 api 的方式来替代 server，具体实现可见 api 方法源码）
     */
    fun server(server: IRequestServer?): T {
        requestHost = server
        requestClient = server
        requestType = server
        requestCache = server
        return this as T
    }

    /**
     * 替换默认的请求处理策略
     */
    fun handler(handler: IRequestHandler?): T {
        requestHandler = handler
        return this as T
    }

    /**
     * 替换默认的拦截器策略
     */
    fun interceptor(interceptor: IRequestInterceptor?): T {
        requestInterceptor = interceptor
        return this as T
    }

    fun delay(delay: Long, unit: TimeUnit): T {
        return delay(unit.toMillis(delay))
    }

    /**
     * 设置请求延迟执行
     *
     * @param delayMillis       延迟毫秒数
     */
    fun delay(delayMillis: Long): T {
        this.delayMillis = delayMillis
        return this as T
    }

    /**
     * 设置请求的标记（可用于 [EasyHttp.cancel]）
     */
    fun tag(tag: Any?): T {
        return tag(getObjectTag(tag))
    }

    fun tag(tag: String): T {
        this.tag = tag
        return this as T
    }

    /**
     * 创建连接对象
     */
    protected fun createCall(): Call {
        var type = requestType!!.bodyType
        val params = HttpParams()
        val headers = HttpHeaders()
        val fields: MutableList<Field> = ArrayList()
        var clazz: Class<*>? = requestApi!!.javaClass
        do {
            val declaredFields = clazz!!.declaredFields
            fields.addAll(0, Arrays.asList(*declaredFields))
            // 遍历获取父类的字段
            clazz = clazz.superclass
        } while (clazz != null && Any::class.java != clazz)

        // 当前请求是否存在流参数
        params.isMultipart = isMultipartParameter(fields)

        // 如果参数中包含流参数并且当前请求方式不是表单的话
        if (params.isMultipart && type !== BodyType.FORM) {
            // 就强制设置成以表单形式提交参数
            type = BodyType.FORM
        }
        for (field in fields) {
            // 允许访问私有字段
            field.isAccessible = true
            val modifiers = field.modifiers
            // 如果这是一个常量字段，则直接忽略掉，例如 Parcelable 接口中的 CREATOR 字段
            // https://github.com/getActivity/EasyHttp/issues/112
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                continue
            }
            try {
                // 获取字段的对象
                val value = field[requestApi]

                // 获取字段的名称
                var key: String
                val annotation = field.getAnnotation(HttpRename::class.java)
                if (annotation != null) {
                    key = annotation.value
                } else {
                    key = field.name
                    // 如果是内部类则会出现一个字段名为 this$0 的外部类对象，会导致无限递归，这里要忽略掉，如果使用静态内部类则不会出现这个问题
                    // 和规避 Kotlin 自动生成的伴生对象：https://github.com/getActivity/EasyHttp/issues/15
                    if (key.matches(Regex("this\\$\\d+")) || "Companion" == key) {
                        continue
                    }
                }

                // 如果这个字段需要忽略，则进行忽略
                if (field.isAnnotationPresent(HttpIgnore::class.java)) {
                    if (field.isAnnotationPresent(HttpHeader::class.java)) {
                        headers.remove(key)
                    } else {
                        params.remove(key)
                    }
                    continue
                }

                // 前提是这个字段值不能为空（基本数据类型有默认的值，而对象默认的值为 null）
                if (value == null) {
                    // 遍历下一个字段
                    continue
                }

                // 如果这是一个请求头参数
                if (field.isAnnotationPresent(HttpHeader::class.java)) {
                    addHttpHeaders(headers, key, value)
                    continue
                }
                addHttpParams(params, key, value, type)
            } catch (e: IllegalAccessException) {
                printThrowable(this, e)
            }
        }
        val url = requestHost!!.host + requestApi!!.api
        if (requestInterceptor != null) {
            requestInterceptor!!.interceptArguments(this, params, headers)
        }
        var request: Request? = createRequest(url, tag, params, headers, type)
        if (requestInterceptor != null) {
            request = requestInterceptor!!.interceptRequest(this, request)
        }
        if (request == null) {
            throw NullPointerException("The request object cannot be empty")
        }
        return requestClient!!.getOkHttpClient().newCall(request)
    }

    /**
     * 执行异步请求
     */
    open fun request(listener: OnHttpListener<*>?) {
        if (delayMillis > 0) {
            // 打印请求延迟时间
            printKeyValue(this, "RequestDelay", delayMillis.toString())
        }
        val stackTrace = Throwable().stackTrace
        postDelayed({
            printStackTrace(this, stackTrace)
            mCallProxy = CallProxy(createCall())
            NormalCallback(this)
                .setListener(listener)
                .setCall(mCallProxy)
                .start()
        }, delayMillis)
    }

    /**
     * 执行同步请求
     * @param responseClass                 需要解析泛型的对象
     * @return                              返回解析完成的对象
     * @throws Exception                    如果请求失败或者解析失败则抛出异常
     */
    @Throws(Exception::class)
    open fun <Bean> execute(responseClass: ResponseClass<Bean>?): Bean? {
        if (delayMillis > 0) {
            // 打印请求延迟时间
            printKeyValue(this, "RequestDelay", delayMillis.toString())
            Thread.sleep(delayMillis)
        }
        printStackTrace(this, Throwable().stackTrace)
        val reflectType = requestHandler!!.getType(responseClass)

        // 必须将 Call 对象创建放到这里来，否则无法显示请求日志
        mCallProxy = CallProxy(createCall())
        val cacheMode = requestCache!!.cacheMode
        if (cacheMode === CacheMode.USE_CACHE_ONLY ||
            cacheMode === CacheMode.USE_CACHE_FIRST
        ) {
            try {
                val result = requestHandler!!.readCache(this, reflectType, requestCache!!.cacheTime)
                printLog(this, "ReadCache result：$result")
                if (cacheMode === CacheMode.USE_CACHE_FIRST) {
                    // 使用异步请求来刷新缓存
                    NormalCallback(this)
                        .setCall(mCallProxy)
                        .start()
                }
                if (result != null) {
                    return result as Bean
                }
            } catch (cacheException: Exception) {
                printLog(this, "ReadCache error")
                printThrowable(this, cacheException)
            }
        }
        return try {
            val response = mCallProxy!!.execute()
            val result = requestHandler!!.requestSucceed(this, response, reflectType)
            if (cacheMode === CacheMode.USE_CACHE_ONLY || cacheMode === CacheMode.USE_CACHE_AFTER_FAILURE) {
                try {
                    val writeSucceed = requestHandler!!.writeCache(this, response, result)
                    printLog(this, "WriteCache result：$writeSucceed")
                } catch (cacheException: Exception) {
                    printLog(this, "WriteCache error")
                    printThrowable(this, cacheException)
                }
            }
            result as Bean?
        } catch (exception: Exception) {
            printThrowable(this, exception)

            // 如果设置了只在网络请求失败才去读缓存
            if (exception is IOException && cacheMode === CacheMode.USE_CACHE_AFTER_FAILURE) {
                try {
                    val result = requestHandler!!.readCache(this, reflectType, requestCache!!.cacheTime)
                    printLog(this, "ReadCache result：$result")
                    if (result != null) {
                        return result as Bean
                    }
                } catch (cacheException: Exception) {
                    printLog(this, "ReadCache error")
                    printThrowable(this, cacheException)
                }
            }
            val finalException = requestHandler!!.requestFail(this, exception)
            if (finalException !== exception) {
                printThrowable(this, finalException)
            }
            throw finalException!!
        }
    }

    /**
     * 取消请求
     */
    open fun cancel(): T {
        if (mCallProxy != null) {
            mCallProxy!!.cancel()
        }
        return this as T
    }

    /**
     * 获取请求的方式
     */
    abstract val requestMethod: String

    /**
     * 打印键值对
     */
    protected fun printKeyValue(key: String?, value: Any) {
        if (value is Enum<*>) {
            // 如果这是一个枚举类型
            printKeyValue(this, key, "\"" + value + "\"")
        } else if (value is String) {
            printKeyValue(this, key, "\"" + value + "\"")
        } else {
            printKeyValue(this, key, value.toString())
        }
    }

    /**
     * 添加请求头
     */
    protected fun addHttpHeaders(headers: HttpHeaders, key: String?, value: Any) {
        if (value is Map<*, *>) {
            val map = value
            for (o in map.keys) {
                if (o != null && map[o] != null) {
                    headers.put(o.toString(), map[o].toString())
                }
            }
        } else {
            headers.put(key!!, value.toString())
        }
    }

    /**
     * 添加请求参数
     */
    protected abstract fun addHttpParams(params: HttpParams, key: String?, value: Any?, type: BodyType?)

    /**
     * 创建请求的对象
     */
    protected fun createRequest(
        url: String,
        tag: String,
        params: HttpParams,
        headers: HttpHeaders,
        type: BodyType?
    ): Request {
        val requestBuilder: Request.Builder = createRequestBuilder(url, tag)
        addRequestHeader(requestBuilder, headers)
        addRequestParams(requestBuilder, params, type)
        val request: Request = requestBuilder.build()
        printRequestLog(request, params, headers, type)
        return request
    }

    /**
     * 创建请求构建对象
     */
    fun createRequestBuilder(url: String, tag: String): Request.Builder {
        val requestBuilder = Request.Builder()
        requestBuilder.url(url)
        requestBuilder.tag(tag)

        // 如果设置了不缓存数据
        if (requestCache!!.cacheMode === CacheMode.NO_CACHE) {
            requestBuilder.cacheControl(CacheControl.Builder().noCache().build())
        }
        return requestBuilder
    }

    /**
     * 添加请求头
     */
    protected fun addRequestHeader(requestBuilder: Request.Builder, headers: HttpHeaders) {
        if (headers.isEmpty) {
            return
        }
        for (key in headers.keys) {
            val value = headers[key]
            try {
                requestBuilder.addHeader(key, value!!)
            } catch (e: IllegalArgumentException) {
                // 请求头中的 key 和 value 如果包含中文需要经过编码，否则 OkHttp 会报错
                requestBuilder.addHeader(encodeString(key), encodeString(value!!))
                // java.lang.IllegalArgumentException: Unexpected char 0x6211 at 0 in KeyName value: KeyValue
                e.printStackTrace()
            }
        }
    }

    /**
     * 添加请求参数
     */
    protected abstract fun addRequestParams(requestBuilder: Request.Builder, params: HttpParams, type: BodyType?)

    /**
     * 打印请求日志
     */
    protected abstract fun printRequestLog(request: Request, params: HttpParams, headers: HttpHeaders, type: BodyType?)
}