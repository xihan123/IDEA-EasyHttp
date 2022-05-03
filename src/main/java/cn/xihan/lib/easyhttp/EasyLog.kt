package cn.xihan.lib.easyhttp

import cn.xihan.lib.easyhttp.request.HttpRequest
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy
import java.util.concurrent.TimeUnit

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/05/10
 * desc   : 日志打印类
 */
object EasyLog {
    /** 创建线程池来打印日志，解决出现大日志阻塞线程的情况  */
    private val EXECUTOR = ThreadPoolExecutor(
        1, 1,
        0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue(),
        Executors.defaultThreadFactory(), DiscardPolicy()
    )

    /**
     * 打印分割线
     */
    @JvmStatic
    fun printLine(httpRequest: HttpRequest<*>?) {
        if (!EasyConfig.getInstance().isLogEnabled()) {
            return
        }
        EXECUTOR.execute { EasyConfig.getInstance().getLogStrategy()!!.printLine(getLogTag(httpRequest)) }
    }

    /**
     * 打印日志
     */
    @JvmStatic
    fun printLog(httpRequest: HttpRequest<*>, log: String) {
        if (!EasyConfig.getInstance().isLogEnabled()) {
            return
        }
        EXECUTOR.execute { EasyConfig.getInstance().getLogStrategy()!!.printLog(getLogTag(httpRequest), log) }
    }

    /**
     * 打印 Json
     */
    @JvmStatic
    fun printJson(httpRequest: HttpRequest<*>?, json: String?) {
        if (!EasyConfig.getInstance().isLogEnabled()) {
            return
        }
        EXECUTOR.execute { EasyConfig.getInstance().getLogStrategy()!!.printJson(getLogTag(httpRequest), json) }
    }

    /**
     * 打印键值对
     */
    @JvmStatic
    fun printKeyValue(httpRequest: HttpRequest<*> ,key: String?, value: String?) {
        if (!EasyConfig.getInstance().isLogEnabled()) {
            return
        }
        EXECUTOR.execute { EasyConfig.getInstance().getLogStrategy()!!.printKeyValue(getLogTag(httpRequest), key, value) }
    }

    /**
     * 打印异常
     */
    @JvmStatic
    fun printThrowable(httpRequest: HttpRequest<*>, throwable: Throwable?) {
        if (!EasyConfig.getInstance().isLogEnabled()) {
            return
        }
        EXECUTOR.execute { EasyConfig.getInstance().getLogStrategy()!!.printThrowable(getLogTag(httpRequest), throwable) }
    }


    /**
     * 打印堆栈
     */
    @JvmStatic
    fun printStackTrace(httpRequest: HttpRequest<*>?, stackTrace: Array<StackTraceElement>) {
        if (!EasyConfig.getInstance().isLogEnabled()) {
            return
        }
        EXECUTOR.execute { EasyConfig.getInstance().getLogStrategy()!!.printStackTrace(getLogTag(httpRequest), stackTrace) }
    }

    private fun getLogTag(httpRequest: HttpRequest<*>?): String {
        val logTag = EasyConfig.getInstance().getLogTag()
        return if (httpRequest == null) {
            logTag
        } else logTag + " " + httpRequest.requestApi?.javaClass?.simpleName
    }
}