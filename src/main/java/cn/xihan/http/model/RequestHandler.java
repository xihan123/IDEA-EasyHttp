package cn.xihan.http.model;


import cn.xihan.gson.factory.GsonFactory;
import cn.xihan.http.EasyLog;
import cn.xihan.http.EasyUtils;
import cn.xihan.http.config.IRequestHandler;
import cn.xihan.http.exception.*;
import cn.xihan.http.request.HttpRequest;
import com.google.gson.JsonSyntaxException;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/05/19
 *    desc   : 请求处理类
 */
public final class RequestHandler implements IRequestHandler {



    public RequestHandler() {

    }


    @Override
    public Object requestSucceed(HttpRequest<?> httpRequest, Response response,
                                 Type type) throws Exception {
        if (Response.class.equals(type)) {
            return response;
        }

        if (!response.isSuccessful()) {
            // 返回响应异常
            throw new ResponseException(EasyUtils.RESPONSE_ERROR + ", responseCode: " +
                    response.code() + ", message: " + response.message(), response);
        }

        if (Headers.class.equals(type)) {
            return response.headers();
        }

        ResponseBody body = response.body();
        if (body == null) {
            throw new NullBodyException(EasyUtils.DATA_ERROR);
        }

        if (ResponseBody.class.equals(type)) {
            return body;
        }

        // 如果是用数组接收，判断一下是不是用 byte[] 类型进行接收的
        if(type instanceof GenericArrayType) {
            Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
            if (byte.class.equals(genericComponentType)) {
                return body.bytes();
            }
        }

        if (InputStream.class.equals(type)) {
            return body.byteStream();
        }

        String text;
        try {
            text = body.string();
        } catch (IOException e) {
            // 返回结果读取异常
            throw new DataException(EasyUtils.RESPONSE_ERROR, e);
        }

        // 打印这个 Json 或者文本
        EasyLog.printJson(httpRequest, text);

        if (String.class.equals(type)) {
            return text;
        }

        final Object result;

        try {
            result = GsonFactory.getSingletonGson().fromJson(text, type);
        } catch (JsonSyntaxException e) {
            // 返回结果读取异常
            throw new DataException(EasyUtils.DATA_ERROR, e);
        }

        if (result instanceof HttpData) {
            HttpData<?> model = (HttpData<?>) result;

            if (model.isRequestSucceed()) {
                // 代表执行成功
                return result;
            }

            if (model.isTokenFailure()) {
                // 代表登录失效，需要重新登录
                throw new TokenException(EasyUtils.LOGIN_INVALID);
            }

            // 代表执行失败
            throw new ResultException(model.getMessage(), model);
        }
        return result;
    }


    @Override
    public Exception requestFail(HttpRequest<?> httpRequest, Exception e) {
        if (e instanceof HttpException) {
            if (e instanceof TokenException) {
                // 登录信息失效，跳转到登录页

            }
            return e;
        }

        if (e instanceof SocketTimeoutException) {
            return new TimeoutException(EasyUtils.TIME_OUT, e);
        }

        if (e instanceof UnknownHostException) {

            // 判断网络是否连接
            if (EasyUtils.isNetworkAvailable()) {
                // 有连接就是服务器的问题
                return new ServerException(EasyUtils.CONNECT_ERROR, e);
            }
            // 没有连接就是网络异常
            return new NetworkException(EasyUtils.ERROR, e);
        }

        if (e instanceof IOException) {
            return new CancelException(EasyUtils.REQUEST_CANCEL, e);
        }

        return new HttpException(e.getMessage(), e);
    }


    @Override
    public Object readCache(HttpRequest<?> httpRequest, Type type, long cacheTime) {
        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
        String cacheValue = HttpCacheManager.getFastKv().getString(cacheKey, null);
        if (cacheValue == null || "".equals(cacheValue) || "{}".equals(cacheValue)) {
            return null;
        }
        EasyLog.printLog(httpRequest, "----- readCache cacheKey -----");
        EasyLog.printJson(httpRequest, cacheKey);
        EasyLog.printLog(httpRequest, "----- readCache cacheValue -----");
        EasyLog.printJson(httpRequest, cacheValue);
        return GsonFactory.getSingletonGson().fromJson(cacheValue, type);
    }

    @Override
    public boolean writeCache(HttpRequest<?> httpRequest, Response response, Object result) {
        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
        String cacheValue = GsonFactory.getSingletonGson().toJson(result);
        if (cacheValue == null || "".equals(cacheValue) || "{}".equals(cacheValue)) {
            return false;
        }
        EasyLog.printLog(httpRequest, "----- writeCache cacheKey -----");
        EasyLog.printJson(httpRequest, cacheKey);
        EasyLog.printLog(httpRequest, "----- writeCache cacheValue -----");
        EasyLog.printJson(httpRequest, cacheValue);
        HttpCacheManager.getFastKv().putString(cacheKey, cacheValue);
        return HttpCacheManager.getFastKv().getString(cacheKey, null) != null;
    }

    @Override
    public void clearCache() {
        HttpCacheManager.getFastKv().clear();
    }
}