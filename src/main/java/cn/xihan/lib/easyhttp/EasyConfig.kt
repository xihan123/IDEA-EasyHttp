package cn.xihan.lib.easyhttp

import cn.xihan.lib.easyhttp.config.*
import okhttp3.OkHttpClient
import java.net.MalformedURLException
import java.net.URL


class EasyConfig(
    client: OkHttpClient,
) {
    /** OkHttp 客户端  */
    private var mClient: OkHttpClient = client

    /** 通用参数  */
    private var mParams: HashMap<String, Any> = HashMap()

    /** 通用请求头  */
    private var mHeaders: HashMap<String, String> =  HashMap()

    /** 日志开关  */
    private var mLogEnabled = true

    /** 日志 TAG  */
    private var mLogTag = "EasyHttp"

    /** 重试次数  */
    private var mRetryCount = 0

    /** 重试时间  */
    private var mRetryTime: Long = 2000

    /** 服务器配置  */
    private var mServer: IRequestServer? = null

    /** 请求处理器  */
    private var mHandler: IRequestHandler? = null

    /** 请求拦截器  */
    private var mInterceptor: IRequestInterceptor? = null

    /** 日志打印策略  */
    private var mLogStrategy: ILogStrategy? = null

    fun setServer(host: String): EasyConfig {
        return setServer(RequestServer(host))
    }

    fun setServer(server: IRequestServer): EasyConfig {
        mServer = server
        return this
    }

    fun setHandler(handler: IRequestHandler): EasyConfig {
        mHandler = handler
        return this
    }

    fun setInterceptor(interceptor: IRequestInterceptor): EasyConfig {
        mInterceptor = interceptor
        return this
    }

    fun setClient(client: OkHttpClient): EasyConfig {
        mClient = client
        return this
    }

    fun setParams(params: HashMap<String, Any>): EasyConfig {
        mParams = params
        return this
    }

    fun setHeaders(headers: HashMap<String, String>): EasyConfig {
        mHeaders = headers
        return this
    }

    fun addHeader(key: String, value: String): EasyConfig {
        mHeaders[key] = value
        return this
    }

    fun removeHeader(key: String): EasyConfig {
        mHeaders.remove(key)
        return this
    }

    fun addParam(key: String, value: String): EasyConfig {
        mParams[key] = value
        return this
    }

    fun removeParam(key: String): EasyConfig {
        mParams.remove(key)
        return this
    }

    fun setLogStrategy(strategy: ILogStrategy): EasyConfig {
        mLogStrategy = strategy
        return this
    }

    fun setLogEnabled(enabled: Boolean): EasyConfig {
        mLogEnabled = enabled
        return this
    }

    fun setLogTag(tag: String): EasyConfig {
        mLogTag = tag
        return this
    }

    fun setRetryCount(count: Int): EasyConfig {
        require(count >= 0) {
            // 重试次数必须大于等于 0 次
            "The number of retries must be greater than 0"
        }
        mRetryCount = count
        return this
    }

    fun setRetryTime(time: Long): EasyConfig {
        require(time >= 0) {
            // 重试时间必须大于等于 0 毫秒
            "The retry time must be greater than 0"
        }
        mRetryTime = time
        return this
    }


    fun getServer(): IRequestServer? {
        return mServer
    }

    fun getHandler(): IRequestHandler? {
        return mHandler
    }

    fun getInterceptor(): IRequestInterceptor? {
        return mInterceptor
    }

    fun getClient(): OkHttpClient {
        return mClient
    }

    fun getParams(): HashMap<String, Any> {
        return mParams
    }

    fun getHeaders(): HashMap<String, String> {
        return mHeaders
    }

    fun getLogStrategy(): ILogStrategy? {
        return mLogStrategy
    }

    fun isLogEnabled(): Boolean {
        return mLogEnabled && mLogStrategy != null
    }

    fun getLogTag(): String {
        return mLogTag
    }

    fun getRetryCount(): Int {
        return mRetryCount
    }

    fun getRetryTime(): Long {
        return mRetryTime
    }

    fun into() {
        requireNotNull(mServer) { "The host configuration cannot be empty" }
        requireNotNull(mHandler) { "The object being processed by the request cannot be empty" }
        try {
            // 校验主机和路径的 url 是否合法
            URL(mServer!!.host)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw IllegalArgumentException("The configured host path url address is not correct")
        }
        if (mLogStrategy == null) {
            mLogStrategy = LogStrategy()
        }
        EasyConfig.setInstance(this)
    }

    companion object{

        private var sConfig: EasyConfig? = null

        fun getInstance(): EasyConfig {
            checkNotNull(sConfig) {
                // 当前没有初始化配置
                "You haven't initialized the configuration yet"
            }
            return sConfig!!
        }

        private fun setInstance(config: EasyConfig) {
            sConfig = config
        }

        fun with(client: OkHttpClient): EasyConfig {
            return EasyConfig(client)
        }

    }


}