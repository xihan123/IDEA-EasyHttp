package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.EasyLog.printKeyValue
import cn.xihan.lib.easyhttp.EasyLog.printStackTrace
import cn.xihan.lib.easyhttp.EasyUtils.postDelayed
import cn.xihan.lib.easyhttp.callback.DownloadCallback
import cn.xihan.lib.easyhttp.config.DownloadApi
import cn.xihan.lib.easyhttp.config.RequestServer
import cn.xihan.lib.easyhttp.listener.OnDownloadListener
import cn.xihan.lib.easyhttp.listener.OnHttpListener
import cn.xihan.lib.easyhttp.model.*
import okhttp3.*
import java.io.File

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/07/20
 * desc   : 下载请求
 */
class DownloadRequest : HttpRequest<DownloadRequest>() {
    private var mRealRequest: HttpRequest<*>

    /** 下载请求方式  */
    private var mMethod = HttpMethod.GET

    /** 保存的文件  */
    private var mFile: File? = null

    /** 校验的 md5  */
    private var mMd5: String? = null

    /** 下载监听回调  */
    private var mListener: OnDownloadListener? = null

    /** 请求执行对象  */
    private var mCallProxy: CallProxy? = null

    init {
        mRealRequest = GetRequest()
    }

    /**
     * 设置请求方式
     */
    fun method(method: HttpMethod): DownloadRequest {
        mMethod = method
        mRealRequest = when (mMethod) {
            HttpMethod.GET ->                 // 如果这个下载请求方式是 Get
                GetRequest()
            HttpMethod.POST ->                 // 如果这个下载请求方式是 Post
                PostRequest()
            else -> throw IllegalStateException("method nonsupport")
        }
        return this
    }

    /**
     * 设置下载地址
     */
    fun url(url: String?): DownloadRequest {
        server(RequestServer(url!!))
        api(DownloadApi(""))
        return this
    }

    /**
     * 设置保存的路径
     */
    fun file(filePath: String?): DownloadRequest {
        return file(File(filePath))
    }

    fun file(file: File?): DownloadRequest {
        mFile = file
        return this
    }

    /**
     * 设置 MD5 值
     */
    fun md5(md5: String?): DownloadRequest {
        mMd5 = md5
        return this
    }

    /**
     * 设置下载监听
     */
    fun listener(listener: OnDownloadListener?): DownloadRequest {
        mListener = listener
        return this
    }

    /**
     * 开始下载
     */
    fun start(): DownloadRequest {
        val delayMillis = delayMillis
        if (delayMillis > 0) {
            // 打印请求延迟时间
            printKeyValue(this, "RequestDelay", delayMillis.toString())
        }
        val stackTrace = Throwable().stackTrace
        postDelayed({
            printStackTrace(this, stackTrace)
            mCallProxy = CallProxy(createCall())
            DownloadCallback(this)
                .setFile(mFile)
                .setMd5(mMd5)
                .setListener(mListener)
                .setCall(mCallProxy)
                .start()
        }, delayMillis)
        return this
    }

    /**
     * 取消下载
     */
    fun stop(): DownloadRequest {
        if (mCallProxy != null) {
            mCallProxy!!.cancel()
        }
        return this
    }

    override fun request(listener: OnHttpListener<*>?) {
        // 请调用 start 方法
        throw IllegalStateException("Call the start method")
    }

    override fun <Bean> execute(responseClass: ResponseClass<Bean>?): Bean? {
        // 请调用 start 方法
        throw IllegalStateException("Call the start method")
    }

    override fun cancel(): DownloadRequest {
        // 请调用 stop 方法
        throw IllegalStateException("Call the start method")
    }

    override val requestMethod: String
        get() = mMethod.toString()

    override fun addHttpParams(params: HttpParams, key: String?, value: Any?, type: BodyType?) {

    }

    override fun addRequestParams(requestBuilder: Request.Builder, params: HttpParams, type: BodyType?) {

    }

    override fun printRequestLog(request: Request, params: HttpParams, headers: HttpHeaders, type: BodyType?) {
       
    }


}