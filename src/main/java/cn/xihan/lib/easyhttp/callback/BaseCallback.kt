package cn.xihan.lib.easyhttp.callback

import cn.xihan.lib.easyhttp.EasyConfig
import cn.xihan.lib.easyhttp.EasyLog.printLog
import cn.xihan.lib.easyhttp.EasyUtils.closeStream
import cn.xihan.lib.easyhttp.EasyUtils.postDelayed
import cn.xihan.lib.easyhttp.model.CallProxy
import cn.xihan.lib.easyhttp.request.HttpRequest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/11/25
 * desc   : 接口回调基类
 */
abstract class BaseCallback(
    /** 请求配置  */
    private val mHttpRequest: HttpRequest<*>
) : Callback {
    /** 请求任务对象  */
    protected var call: CallProxy? = null
        private set

    /** 当前重试次数  */
    private var mRetryCount = 0
    fun setCall(call: CallProxy?): BaseCallback {
        this.call = call
        return this
    }

    open fun start() {
        call!!.enqueue(this)
        onStart(call)
    }

    override fun onResponse(call: Call, response: Response) {
        try {
            // 收到响应
            onResponse(response)
        } catch (e: Exception) {
            // 回调失败
            onFailure(e)
        } finally {
            // 关闭响应
            closeStream(response)
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        var call = call
        // 服务器请求超时重试
        if (e is SocketTimeoutException && mRetryCount < EasyConfig.getInstance().getRetryCount()) {
            // 设置延迟 N 秒后重试该请求
            postDelayed({
                mRetryCount++
                val newCall = call.clone()
                call = newCall
                newCall.enqueue(this@BaseCallback)
                // 请求超时，正在执行延迟重试
                printLog(
                    mHttpRequest, "The request timed out, a delayed retry is being performed, the number of retries: " +
                            mRetryCount + " / " + EasyConfig.getInstance().getRetryCount()
                )
            }, EasyConfig.getInstance().getRetryTime())
            return
        }
        onFailure(e)
    }

    /**
     * 请求开始
     */
    protected abstract fun onStart(call: Call?)

    /**
     * 请求成功
     */
    @Throws(Exception::class)
    protected abstract fun onResponse(response: Response?)

    /**
     * 请求失败
     */
    protected abstract fun onFailure(e: Exception)
}