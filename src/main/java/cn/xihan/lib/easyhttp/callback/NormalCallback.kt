package cn.xihan.lib.easyhttp.callback

import cn.xihan.lib.easyhttp.EasyLog.printLog
import cn.xihan.lib.easyhttp.EasyLog.printThrowable
import cn.xihan.lib.easyhttp.EasyUtils.post
import cn.xihan.lib.easyhttp.EasyUtils.postDelayed
import cn.xihan.lib.easyhttp.listener.OnHttpListener
import cn.xihan.lib.easyhttp.model.CacheMode
import cn.xihan.lib.easyhttp.request.HttpRequest
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/11/25
 * desc   : 正常接口回调
 */
class NormalCallback(
    /** 请求配置  */
    private val mHttpRequest: HttpRequest<*>
) : BaseCallback(mHttpRequest) {
    /** 接口回调  */
    private var mListener: OnHttpListener<*>? = null

    /** 解析类型  */
    private var mReflectType: Type? = null
    fun setListener(listener: OnHttpListener<*>?): NormalCallback {
        mListener = listener
        mReflectType = mHttpRequest.requestHandler!!.getType(mListener)
        return this
    }

    override fun start() {
        val cacheMode = mHttpRequest.requestCache!!.cacheMode
        if (cacheMode !== CacheMode.USE_CACHE_ONLY &&
            cacheMode !== CacheMode.USE_CACHE_FIRST
        ) {
            super.start()
            return
        }
        try {
            val result = mHttpRequest.requestHandler!!.readCache(
                mHttpRequest,
                mReflectType, mHttpRequest.requestCache!!.cacheTime
            )
            printLog(mHttpRequest, "ReadCache result：$result")

            // 如果没有缓存，就请求网络
            if (result == null) {
                super.start()
                return
            }

            // 读取缓存成功
            post {
                if (mListener == null) {
                    return@post
                }
                mListener!!.onStart(call)
                mListener!!.onSucceed(result, true)
                mListener!!.onEnd(call)
            }

            // 如果当前模式是先读缓存再写请求
            if (cacheMode === CacheMode.USE_CACHE_FIRST) {
                postDelayed({


                    // 将回调置为空，避免出现两次回调
                    mListener = null
                    super.start()
                }, 1)
            }
        } catch (cacheException: Exception) {
            printLog(mHttpRequest, "ReadCache error")
            printThrowable(mHttpRequest, cacheException)
            super.start()
        }
    }

    override fun onStart(call: Call?) {
        post {
            if (mListener == null) {
                return@post
            }
            mListener!!.onStart(call)
        }
    }

    @Throws(Exception::class)
    override fun onResponse(response: Response?) {
        // 打印请求耗时时间
        var response = response
        printLog(
            mHttpRequest, "RequestConsuming：" +
                    (response!!.receivedResponseAtMillis - response.sentRequestAtMillis) + " ms"
        )
        val interceptor = mHttpRequest.requestInterceptor
        if (interceptor != null) {
            response = interceptor.interceptResponse(mHttpRequest, response)
        }

        // 解析 Bean 类对象
        val result = mHttpRequest.requestHandler!!.requestSucceed(
            mHttpRequest, response, mReflectType
        )
        val cacheMode = mHttpRequest.requestCache!!.cacheMode
        if (cacheMode === CacheMode.USE_CACHE_ONLY || cacheMode === CacheMode.USE_CACHE_FIRST || cacheMode === CacheMode.USE_CACHE_AFTER_FAILURE) {
            try {
                val writeSucceed = mHttpRequest.requestHandler!!.writeCache(mHttpRequest, response, result)
                printLog(mHttpRequest, "WriteCache result：$writeSucceed")
            } catch (cacheException: Exception) {
                printLog(mHttpRequest, "WriteCache error")
                printThrowable(mHttpRequest, cacheException)
            }
        }
        post {
            if (mListener == null) {
                return@post
            }
            mListener!!.onSucceed(result!!, false)
            mListener!!.onEnd(call)
        }
    }

    override fun onFailure(exception: Exception) {
        // 打印错误堆栈
        printThrowable(mHttpRequest, exception)
        // 如果设置了只在网络请求失败才去读缓存
        if (exception is IOException && mHttpRequest.requestCache!!.cacheMode === CacheMode.USE_CACHE_AFTER_FAILURE) {
            try {
                val result = mHttpRequest.requestHandler!!.readCache(
                    mHttpRequest,
                    mReflectType, mHttpRequest.requestCache!!.cacheTime
                )
                printLog(mHttpRequest, "ReadCache result：$result")
                if (result != null) {
                    post {
                        if (mListener == null) {
                            return@post
                        }
                        mListener!!.onSucceed(result, true)
                        mListener!!.onEnd(call)
                    }
                    return
                }
            } catch (cacheException: Exception) {
                printLog(mHttpRequest, "ReadCache error")
                printThrowable(mHttpRequest, cacheException)
            }
        }
        val finalException = mHttpRequest.requestHandler!!.requestFail(mHttpRequest, exception)
        if (finalException !== exception) {
            printThrowable(mHttpRequest, finalException)
        }
        post {
            if (mListener == null) {
                return@post
            }
            mListener!!.onFail(finalException)
            mListener!!.onEnd(call)
        }
    }
}