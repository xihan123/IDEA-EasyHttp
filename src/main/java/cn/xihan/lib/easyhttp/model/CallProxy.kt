package cn.xihan.lib.easyhttp.model

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.Timeout
import java.io.IOException

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/12/14
 * desc   : 请求对象代理
 */
class CallProxy(private var mCall: Call) : Call {
    fun setCall(call: Call) {
        mCall = call
    }

    override fun request(): Request {
        return mCall.request()
    }

    @Throws(IOException::class)
    override fun execute(): Response {
        return mCall.execute()
    }

    override fun enqueue(responseCallback: Callback) {
        mCall.enqueue(responseCallback)
    }

    override fun cancel() {
        mCall.cancel()
    }

    override fun isExecuted(): Boolean {
        return mCall.isExecuted()
    }

    override fun isCanceled(): Boolean {
        return mCall.isCanceled()
    }

    override fun timeout(): Timeout {
        return mCall.timeout()
    }

    override fun clone(): Call {
        return mCall.clone()
    }
}