package cn.xihan.lib.easyhttp.request;


import cn.xihan.lib.easyhttp.EasyConfig;
import cn.xihan.lib.easyhttp.EasyHttp;
import cn.xihan.lib.easyhttp.EasyLog;
import cn.xihan.lib.easyhttp.EasyUtils;
import cn.xihan.lib.easyhttp.annotation.HttpHeader;
import cn.xihan.lib.easyhttp.annotation.HttpIgnore;
import cn.xihan.lib.easyhttp.annotation.HttpRename;
import cn.xihan.lib.easyhttp.callback.NormalCallback;
import cn.xihan.lib.easyhttp.config.*;
import cn.xihan.lib.easyhttp.listener.OnHttpListener;
import cn.xihan.lib.easyhttp.model.*;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/07/20
 *    desc   : 所有请求方式的基类
 */
@SuppressWarnings("unchecked")
public abstract class HttpRequest<T extends HttpRequest<?>> {


    /** 请求接口配置 */
    private IRequestApi mRequestApi;
    /** 接口主机地址 */
    private IRequestHost mRequestHost = EasyConfig.getInstance().getServer();
    /** 提交参数类型 */
    private IRequestType mRequestType = EasyConfig.getInstance().getServer();
    /** OkHttp 客户端 */
    private IRequestClient mRequestClient = EasyConfig.getInstance().getServer();
    /** 请求处理策略 */
    private IRequestHandler mRequestHandler = EasyConfig.getInstance().getHandler();
    /** 请求拦截策略 */
    private IRequestInterceptor mRequestInterceptor = EasyConfig.getInstance().getInterceptor();
    /** 线程调度器 */
    private ThreadSchedulers mThreadSchedulers = EasyConfig.getInstance().getThreadSchedulers();

    /** 请求执行代理类 */
    private CallProxy mCallProxy;

    /** 请求标记 */
    private String mTag;

    /** 请求延迟 */
    private long mDelayMillis;

    public HttpRequest() {


    }

    public T api(Class<? extends IRequestApi> api) {
        try {
            return api(api.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public T api(String api) {
        return api(new RequestApi(api));
    }

    /**
     * 设置请求配置
     */
    public T api(IRequestApi api) {
        mRequestApi = api;
        if (api instanceof IRequestHost) {
            mRequestHost = (IRequestHost) api;
        }
        if (api instanceof IRequestClient) {
            mRequestClient = (IRequestClient) api;
        }
        if (api instanceof IRequestType) {
            mRequestType = (IRequestType) api;
        }
        if (api instanceof IRequestHandler) {
            mRequestHandler = (IRequestHandler) api;
        }
        if (api instanceof IRequestInterceptor) {
            mRequestInterceptor = (IRequestInterceptor) api;
        }
        return (T) this;
    }

    public T server(Class<? extends IRequestServer> api) {
        try {
            return server(api.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public T server(String host) {
        return server(new RequestServer(host));
    }

    /**
     * 替换默认的服务器配器（推荐使用 api 的方式来替代 server，具体实现可见 api 方法源码）
     */
    public T server(IRequestServer server) {
        mRequestHost = server;
        mRequestClient = server;
        mRequestType = server;
        return (T) this;
    }

    /**
     * 替换默认的请求处理策略
     */
    public T handler(IRequestHandler handler) {
        mRequestHandler = handler;
        return (T) this;
    }

    /**
     * 替换默认的拦截器策略
     */
    public T interceptor(IRequestInterceptor interceptor) {
        mRequestInterceptor = interceptor;
        return (T) this;
    }

    public T delay(long delay, TimeUnit unit) {
        return delay(unit.toMillis(delay));
    }

    /**
     * 设置请求延迟执行
     *
     * @param delayMillis       延迟毫秒数
     */
    public T delay(long delayMillis) {
        mDelayMillis = delayMillis;
        return (T) this;
    }

    /**
     * 设置请求的标记（可用于 {@link EasyHttp#cancel(String)}）
     */
    public T tag(Object tag) {
        return tag(EasyUtils.getObjectTag(tag));
    }

    public T tag(String tag) {
        mTag = tag;
        return (T) this;
    }

    /**
     * 设置线程调度器
     */
    public T schedulers(ThreadSchedulers schedulers) {
        mThreadSchedulers = schedulers;
        return (T) this;
    }

    /**
     * 创建连接对象
     */
    protected Call createCall() {

        BodyType type = mRequestType.getBodyType();

        HttpParams params = new HttpParams();
        HttpHeaders headers = new HttpHeaders();

        // 反射获取类的所有字段
        List<Field> fields = EasyUtils.getAllFields(mRequestApi.getClass());

        // 当前请求是否存在流参数
        params.setMultipart(EasyUtils.isMultipartParameter(fields));

        // 如果参数中包含流参数并且当前请求方式不是表单的话
        if (params.isMultipart() && type != BodyType.FORM) {
            // 就强制设置成以表单形式提交参数
            type = BodyType.FORM;
        }

        for (Field field : fields) {
            // 允许访问私有字段
            field.setAccessible(true);

            if (EasyUtils.isConstantField(field)) {
                continue;
            }

            try {
                // 获取字段的对象
                Object value = field.get(mRequestApi);

                // 获取字段的名称
                String key;
                HttpRename annotation = field.getAnnotation(HttpRename.class);
                if (annotation != null) {
                    key = annotation.value();
                } else {
                    key = field.getName();
                    // 如果是内部类则会出现一个字段名为 this$0 的外部类对象，会导致无限递归
                    // 这里要忽略掉，如果使用静态内部类则不会出现这个问题
                    // 另外还要规避 Kotlin 自动生成的伴生对象：
                    // https://github.com/getActivity/EasyHttp/issues/15
                    if (key.matches("this\\$\\d+") || "Companion".equals(key)) {
                        continue;
                    }
                }

                // 如果这个字段需要忽略，则进行忽略
                if (field.isAnnotationPresent(HttpIgnore.class)) {
                    if (field.isAnnotationPresent(HttpHeader.class)) {
                        headers.remove(key);
                    } else {
                        params.remove(key);
                    }
                    continue;
                }

                // 前提是这个字段值不能为空（基本数据类型有默认的值，而对象默认的值为 null）
                if (value == null) {
                    // 遍历下一个字段
                    continue;
                }

                // 如果这是一个请求头参数
                if (field.isAnnotationPresent(HttpHeader.class)) {
                    addHttpHeaders(headers, key, value);
                    continue;
                }

                addHttpParams(params, key, value, type);

            } catch (IllegalAccessException e) {
                EasyLog.printThrowable(this, e);
            }
        }

        String url = mRequestHost.getHost() + mRequestApi.getApi();
        if (mRequestInterceptor != null) {
            mRequestInterceptor.interceptArguments(this, params, headers);
        }

        Request request = createRequest(url, mTag, params, headers, type);

        if (mRequestInterceptor != null) {
            request = mRequestInterceptor.interceptRequest(this, request);
        }
        if (request == null) {
            throw new NullPointerException("The request object cannot be empty");
        }
        return mRequestClient.getOkHttpClient().newCall(request);
    }

    /**
     * 执行异步请求
     */
    public void request(OnHttpListener<?> listener) {
        if (mDelayMillis > 0) {
            // 打印请求延迟时间
            EasyLog.printKeyValue(this, "RequestDelay", String.valueOf(mDelayMillis));
        }

        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        EasyUtils.postDelayed(() -> {

            EasyLog.printStackTrace(this, stackTrace);

            mCallProxy = new CallProxy(createCall());
            new NormalCallback(this)
                    .setListener(listener)
                    .setCall(mCallProxy)
                    .start();

        }, mDelayMillis);
    }

    /**
     * 执行同步请求
     * @param responseClass                 需要解析泛型的对象
     * @return                              返回解析完成的对象
     * @throws Exception                    如果请求失败或者解析失败则抛出异常
     */
    public <Bean> Bean execute(ResponseClass<Bean> responseClass) throws Exception {
        if (mDelayMillis > 0) {
            // 打印请求延迟时间
            EasyLog.printKeyValue(this, "RequestDelay", String.valueOf(mDelayMillis));
            Thread.sleep(mDelayMillis);
        }

        EasyLog.printStackTrace(this, new Throwable().getStackTrace());

        Type reflectType = mRequestHandler.getType(responseClass);

        // 必须将 Call 对象创建放到这里来，否则无法显示请求日志
        mCallProxy = new CallProxy(createCall());

        try {
            Response response = mCallProxy.execute();
            Object result = mRequestHandler.requestSucceed(this, response, reflectType);

            return (Bean) result;

        } catch (Exception exception) {

            EasyLog.printThrowable(this, exception);

            Exception finalException = mRequestHandler.requestFail(this, exception);
            if (finalException != exception) {
                EasyLog.printThrowable(this, finalException);
            }
            throw finalException;
        }
    }

    /**
     * 取消请求
     */
    public T cancel() {
        if (mCallProxy != null) {
            mCallProxy.cancel();
        }
        return (T) this;
    }

    /**
     * 获取请求标记
     */
    public String getTag() {
        return mTag;
    }

    /**
     * 获取请求接口对象
     */
    public IRequestApi getRequestApi() {
        return mRequestApi;
    }

    /**
     * 获取请求主机地址
     */
    public IRequestHost getRequestHost() {
        return mRequestHost;
    }

    /**
     * 获取参数提交方式
     */
    public IRequestType getRequestType() {
        return mRequestType;
    }

    /**
     * 获取请求的 OkHttpClient 对象
     */
    public IRequestClient getRequestClient() {
        return mRequestClient;
    }

    /**
     * 获取请求处理对象
     */
    public IRequestHandler getRequestHandler() {
        return mRequestHandler;
    }

    /**
     * 获取请求的方式
     */
    public abstract String getRequestMethod();

    /**
     * 获取请求的拦截器（可能为空）
     */
    public IRequestInterceptor getRequestInterceptor() {
        return mRequestInterceptor;
    }

    /**
     * 获取当前线程的调度器
     */
    public ThreadSchedulers getThreadSchedulers() {
        return mThreadSchedulers;
    }

    /**
     * 获取延迟请求时间
     */
    protected long getDelayMillis() {
        return mDelayMillis;
    }

    /**
     * 打印键值对
     */
    protected void printKeyValue(String key, Object value) {
        if (value instanceof Enum) {
            // 如果这是一个枚举类型
            EasyLog.printKeyValue(this, key, "\"" + value + "\"");
        } else if (value instanceof String) {
            EasyLog.printKeyValue(this, key, "\"" + value + "\"");
        } else {
            EasyLog.printKeyValue(this, key, String.valueOf(value));
        }
    }

    /**
     * 添加请求头
     */
    protected void addHttpHeaders(HttpHeaders headers, String key, Object value) {
        if (value instanceof Map) {
            Map<?, ?> map = ((Map<?, ?>) value);
            for (Object o : map.keySet()) {
                if (o != null && map.get(o) != null) {
                    headers.put(String.valueOf(o), String.valueOf(map.get(o)));
                }
            }
        } else {
            headers.put(key, String.valueOf(value));
        }
    }

    /**
     * 添加请求参数
     */
    protected abstract void addHttpParams(HttpParams params, String key, Object value, BodyType type);

    /**
     * 创建请求的对象
     */
    protected Request createRequest(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
        Request.Builder requestBuilder = createRequestBuilder(url, tag);
        addRequestHeader(requestBuilder, headers);
        addRequestParams(requestBuilder, params, type);

        Request request = requestBuilder.build();
        printRequestLog(request, params, headers, type);
        return request;
    }

    /**
     * 创建请求构建对象
     */
    public Request.Builder createRequestBuilder(String url, String tag) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        return requestBuilder;
    }

    /**
     * 添加请求头
     */
    protected void addRequestHeader(Request.Builder requestBuilder, HttpHeaders headers) {
        if (headers.isEmpty()) {
            return;
        }

        for (String key : headers.getKeys()) {
            String value = headers.get(key);
            try {
                requestBuilder.addHeader(key, value);
            } catch (IllegalArgumentException e) {
                // 请求头中的 key 和 value 如果包含中文需要经过编码，否则 OkHttp 会报错
                requestBuilder.addHeader(EasyUtils.encodeString(key), EasyUtils.encodeString(value));
                // java.lang.IllegalArgumentException: Unexpected char 0x6211 at 0 in KeyName value: KeyValue
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加请求参数
     */
    protected abstract void addRequestParams(Request.Builder requestBuilder, HttpParams params, BodyType type);

    /**
     * 打印请求日志
     */
    protected abstract void printRequestLog(Request request, HttpParams params, HttpHeaders headers, BodyType type);
}