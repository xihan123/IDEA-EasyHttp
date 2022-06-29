package cn.xihan.lib.easyhttp.callback;


import cn.xihan.lib.easyhttp.EasyLog;
import cn.xihan.lib.easyhttp.EasyUtils;
import cn.xihan.lib.easyhttp.config.IRequestInterceptor;
import cn.xihan.lib.easyhttp.listener.OnHttpListener;
import cn.xihan.lib.easyhttp.request.HttpRequest;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/11/25
 *    desc   : 正常接口回调
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class NormalCallback extends BaseCallback {

    /** 请求配置 */
    private final HttpRequest mHttpRequest;
    /** 接口回调 */
    private OnHttpListener mListener;
    /** 解析类型 */
    private Type mReflectType;

    public NormalCallback(HttpRequest request) {
        super(request);
        mHttpRequest = request;
    }

    public NormalCallback setListener(OnHttpListener listener) {
        mListener = listener;
        mReflectType = mHttpRequest.getRequestHandler().getType(mListener);
        return this;
    }

    @Override
    protected void onStart(Call call) {
        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), this::callOnStart);
    }

    @Override
    protected void onResponse(Response response) throws Exception {
        // 打印请求耗时时间
        EasyLog.printLog(mHttpRequest, "RequestConsuming：" +
                (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) + " ms");

        IRequestInterceptor interceptor = mHttpRequest.getRequestInterceptor();
        if (interceptor != null) {
            response = interceptor.interceptResponse(mHttpRequest, response);
        }

        // 解析 Bean 类对象
        final Object result = mHttpRequest.getRequestHandler().requestSucceed(
                mHttpRequest, response, mReflectType);

        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> callOnSucceed(result));
    }

    @Override
    protected void onFailure(Exception exception) {
        // 打印错误堆栈
        EasyLog.printThrowable(mHttpRequest, exception);

        final Exception finalException = mHttpRequest.getRequestHandler().requestFail(mHttpRequest, exception);
        if (finalException != exception) {
            EasyLog.printThrowable(mHttpRequest, finalException);
        }

        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> callOnFail(finalException));
    }

    private void callOnStart() {
        if (mListener == null) {
            return;
        }
        mListener.onStart(getCall());
    }

    private void callOnSucceed(Object result) {
        if (mListener == null) {
            return;
        }
        mListener.onSucceed(result, false);
        mListener.onEnd(getCall());
    }

    private void callOnFail(Exception e) {
        if (mListener == null) {
            return;
        }
        mListener.onFail(e);
        mListener.onEnd(getCall());
    }

    @Override
    protected void closeResponse(Response response) {
        if (Response.class.equals(mReflectType) ||
                ResponseBody.class.equals(mReflectType) ||
                InputStream.class.equals(mReflectType)) {
            // 如果反射是这几个类型，则不关闭 Response，否则会导致拉取不到里面的流
            return;
        }
        super.closeResponse(response);
    }
}