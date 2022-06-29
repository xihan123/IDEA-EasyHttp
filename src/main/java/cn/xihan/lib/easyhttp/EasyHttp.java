package cn.xihan.lib.easyhttp;


import cn.xihan.lib.easyhttp.request.*;
import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * author : Android 轮子哥
 * github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 * time   : 2019/05/19
 * desc   : 网络请求类
 */
@SuppressWarnings("unused")
public final class EasyHttp {

    /**
     * Get 请求
     */
    public static GetRequest get() {
        return new GetRequest();
    }

    /**
     * Post 请求
     */
    public static PostRequest post() {
        return new PostRequest();
    }

    /**
     * Head 请求
     */
    public static HeadRequest head() {
        return new HeadRequest();
    }

    /**
     * Delete 请求
     */
    public static DeleteRequest delete() {
        return new DeleteRequest();
    }

    /**
     * Delete 请求（参数使用 Body 传递）
     */
    public static DeleteBodyRequest deleteBody() {
        return new DeleteBodyRequest();
    }

    /**
     * Put 请求
     */
    public static PutRequest put() {
        return new PutRequest();
    }

    /**
     * Patch 请求
     */
    public static PatchRequest patch() {
        return new PatchRequest();
    }

    /**
     * Options 请求
     */
    public static OptionsRequest options() {
        return new OptionsRequest();
    }

    /**
     * Trace 请求
     */
    public static TraceRequest trace() {
        return new TraceRequest();
    }

    /**
     * 下载请求
     */
    public static DownloadRequest download() {
        return new DownloadRequest();
    }

    /**
     * 根据 TAG 取消请求任务
     */
    public static void cancel(Object tag) {
        cancel(EasyUtils.getObjectTag(tag));
    }

    public static void cancel(String tag) {
        if (tag == null) {
            return;
        }

        OkHttpClient client = EasyConfig.getInstance().getClient();

        // 清除排队等候的任务
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

        // 清除正在执行的任务
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 清除所有请求任务
     */
    public static void cancel() {
        OkHttpClient client = EasyConfig.getInstance().getClient();

        // 清除排队等候的任务
        for (Call call : client.dispatcher().queuedCalls()) {
            call.cancel();
        }

        // 清除正在执行的任务
        for (Call call : client.dispatcher().runningCalls()) {
            call.cancel();
        }
    }
}