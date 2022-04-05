package cn.xihan.http.model;



import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/12/14
 *    desc   : 请求对象代理
 */
public final class CallProxy implements Call {

    private Call mCall;

    public CallProxy(Call call) {
        mCall = call;
    }

    public void setCall(Call call) {
        mCall = call;
    }


    @NotNull
    @Override
    public Request request() {
        return mCall.request();
    }


    @NotNull
    @Override
    public Response execute() throws IOException {
        return mCall.execute();
    }

    @Override
    public void enqueue(@NotNull Callback responseCallback) {
        mCall.enqueue(responseCallback);
    }

    @Override
    public void cancel() {
        mCall.cancel();
    }

    @Override
    public boolean isExecuted() {
        return mCall.isExecuted();
    }

    @Override
    public boolean isCanceled() {
        return mCall.isCanceled();
    }


    @NotNull
    @Override
    public Timeout timeout() {
        return mCall.timeout();
    }


    @NotNull
    @Override
    public Call clone() {
        return mCall.clone();
    }
}