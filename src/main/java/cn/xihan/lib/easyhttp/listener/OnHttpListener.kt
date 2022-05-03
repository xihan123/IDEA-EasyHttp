package cn.xihan.lib.easyhttp.listener

import okhttp3.Call

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 请求回调监听器
 */
interface OnHttpListener<T> {
    /**
     * 请求开始
     */
    fun onStart(call: Call?) {}

    /**
     * 请求成功
     *
     * @param cache         是否是通过缓存请求成功的
     */
    fun onSucceed(result: Any, cache: Boolean) {
        onSucceed(result as T)
    }

    /**
     * 请求成功
     */
    fun onSucceed(result: T)

    /**
     * 请求出错
     */
    fun onFail(e: Exception?)

    /**
     * 请求结束
     */
    fun onEnd(call: Call?) {}
}