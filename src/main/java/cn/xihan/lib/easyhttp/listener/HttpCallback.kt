package cn.xihan.lib.easyhttp.listener

import okhttp3.Call

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 请求回调包装类
 */
class HttpCallback<T>(private val mListener: OnHttpListener<T>?) : OnHttpListener<T> {
    override fun onStart(call: Call?) {
        if (mListener == null) {
            return
        }
        mListener.onStart(call)
    }

    override fun onSucceed(result: Any, cache: Boolean) {
        onSucceed(result as T)
    }

    override fun onSucceed(result: T) {
        if (mListener == null) {
            return
        }
        mListener.onSucceed(result)
    }

    override fun onFail(e: Exception?) {
        if (mListener == null) {
            return
        }
        mListener.onFail(e)
    }

    override fun onEnd(call: Call?) {
        if (mListener == null) {
            return
        }
        mListener.onEnd(call)
    }
}