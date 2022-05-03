package cn.xihan.lib.easyhttp

import cn.xihan.lib.easyhttp.EasyUtils.getObjectTag
import cn.xihan.lib.easyhttp.request.*

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 网络请求类
 */
object EasyHttp {
    /**
     * Get 请求
     */
    fun get(): GetRequest {
        return GetRequest()
    }

    /**
     * Post 请求
     */
    fun post(): PostRequest {
        return PostRequest()
    }

    /**
     * Head 请求
     */
    fun head(): HeadRequest {
        return HeadRequest()
    }

    /**
     * Delete 请求
     */
    fun delete(): DeleteRequest {
        return DeleteRequest()
    }

    /**
     * Put 请求
     */
    fun put(): PutRequest {
        return PutRequest()
    }

    /**
     * Patch 请求
     */
    fun patch(): PatchRequest {
        return PatchRequest()
    }

    /**
     * Options 请求
     */
    fun options(): OptionsRequest {
        return OptionsRequest()
    }

    /**
     * Trace 请求
     */
    fun trace(): TraceRequest {
        return TraceRequest()
    }

    /**
     * 下载请求
     */
    fun download(): DownloadRequest {
        return DownloadRequest()
    }

    /**
     * 根据 TAG 取消请求任务
     */
    fun cancel(tag: Any?) {
        cancel(getObjectTag(tag))
    }

    fun cancel(tag: String?) {
        if (tag == null) {
            return
        }
        val client = EasyConfig.getInstance().getClient()

        // 清除排队等候的任务
        for (call in client.dispatcher.queuedCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }

        // 清除正在执行的任务
        for (call in client.dispatcher.runningCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
    }

    /**
     * 清除所有请求任务
     */
    fun cancel() {
        val client = EasyConfig.getInstance().getClient()

        // 清除排队等候的任务
        for (call in client.dispatcher.queuedCalls()) {
            call.cancel()
        }

        // 清除正在执行的任务
        for (call in client.dispatcher.runningCalls()) {
            call.cancel()
        }
    }
}