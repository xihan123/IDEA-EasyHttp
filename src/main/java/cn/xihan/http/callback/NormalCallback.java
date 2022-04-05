package cn.xihan.http.callback;



import java.io.IOException;
import java.lang.reflect.Type;

import cn.xihan.http.EasyLog;
import cn.xihan.http.EasyUtils;
import cn.xihan.http.config.IRequestInterceptor;

import cn.xihan.http.listener.OnHttpListener;
import cn.xihan.http.model.CacheMode;
import cn.xihan.http.request.HttpRequest;
import okhttp3.Call;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
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
    public void start() {
        CacheMode cacheMode = mHttpRequest.getRequestCache().getCacheMode();
        if (cacheMode != CacheMode.USE_CACHE_ONLY &&
                cacheMode != CacheMode.USE_CACHE_FIRST) {
            super.start();
            return;
        }

        try {
            Object result = mHttpRequest.getRequestHandler().readCache(mHttpRequest,
                    mReflectType, mHttpRequest.getRequestCache().getCacheTime());
            EasyLog.printLog(mHttpRequest, "ReadCache result：" + result);

            // 如果没有缓存，就请求网络
            if (result == null) {
                super.start();
                return;
            }

            // 读取缓存成功
            EasyUtils.post(() -> {
                if (mListener == null) {
                    return;
                }
                mListener.onStart(getCall());
                mListener.onSucceed(result, true);
                mListener.onEnd(getCall());
            });

            // 如果当前模式是先读缓存再写请求
            if (cacheMode == CacheMode.USE_CACHE_FIRST) {
                EasyUtils.postDelayed(() -> {

                    // 将回调置为空，避免出现两次回调
                    mListener = null;
                    super.start();
                }, 1);
            }

        } catch (Exception cacheException) {
            EasyLog.printLog(mHttpRequest, "ReadCache error");
            EasyLog.printThrowable(mHttpRequest, cacheException);
            super.start();
        }
    }

    @Override
    protected void onStart(Call call) {
        EasyUtils.post(() -> {
            if (mListener == null) {
                return;
            }
            mListener.onStart(call);
        });
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

        CacheMode cacheMode = mHttpRequest.getRequestCache().getCacheMode();
        if (cacheMode == CacheMode.USE_CACHE_ONLY ||
                cacheMode == CacheMode.USE_CACHE_FIRST ||
                cacheMode == CacheMode.USE_CACHE_AFTER_FAILURE) {
            try {
                boolean writeSucceed = mHttpRequest.getRequestHandler().writeCache(mHttpRequest, response, result);
                EasyLog.printLog(mHttpRequest, "WriteCache result：" + writeSucceed);
            } catch (Exception cacheException) {
                EasyLog.printLog(mHttpRequest, "WriteCache error");
                EasyLog.printThrowable(mHttpRequest, cacheException);
            }
        }

        EasyUtils.post(() -> {
            if (mListener == null) {
                return;
            }
            mListener.onSucceed(result, false);
            mListener.onEnd(getCall());
        });
    }

    @Override
    protected void onFailure(Exception exception) {
        // 打印错误堆栈
        EasyLog.printThrowable(mHttpRequest, exception);
        // 如果设置了只在网络请求失败才去读缓存
        if (exception instanceof IOException && mHttpRequest.getRequestCache().getCacheMode() == CacheMode.USE_CACHE_AFTER_FAILURE) {
            try {
                Object result = mHttpRequest.getRequestHandler().readCache(mHttpRequest,
                        mReflectType, mHttpRequest.getRequestCache().getCacheTime());
                EasyLog.printLog(mHttpRequest, "ReadCache result：" + result);
                if (result != null) {
                    EasyUtils.post(() -> {
                        if (mListener == null) {
                            return;
                        }
                        mListener.onSucceed(result, true);
                        mListener.onEnd(getCall());
                    });
                    return;
                }
            } catch (Exception cacheException) {
                EasyLog.printLog(mHttpRequest, "ReadCache error");
                EasyLog.printThrowable(mHttpRequest, cacheException);
            }
        }

        final Exception finalException = mHttpRequest.getRequestHandler().requestFail(mHttpRequest, exception);
        if (finalException != exception) {
            EasyLog.printThrowable(mHttpRequest, finalException);
        }

        EasyUtils.post(() -> {
            if (mListener == null) {
                return;
            }
            mListener.onFail(finalException);
            mListener.onEnd(getCall());
        });
    }
}