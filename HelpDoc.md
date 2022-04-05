# 目录

* [集成文档](#集成文档)

    * [服务器配置](#服务器配置)

    * [框架初始化](#框架初始化)

* [使用文档](#使用文档)

    * [配置接口](#配置接口)

    * [发起请求](#发起请求)

    * [上传文件](#上传文件)

    * [下载文件](#下载文件)

    * [同步请求](#同步请求)

    * [请求缓存](#请求缓存)

* [疑难解答](#疑难解答)

    * [如何设置 Cookie](#如何设置-cookie)

    * [如何添加或者删除全局参数](#如何添加或者删除全局参数)

    * [如何定义全局的动态参数](#如何定义全局的动态参数)

    * [如何在请求中忽略某个全局参数](#如何在请求中忽略某个全局参数)

    * [如何获取服务器配置](#如何获取服务器配置)

    * [如何修改接口的服务器配置](#如何修改接口的服务器配置)

    * [如何配置多域名](#如何配置多域名)

    * [如何修改参数的提交方式](#如何修改参数的提交方式)

    * [如何对接口进行加密或者解密](#如何对接口进行加密或者解密)

    * [如何忽略某个参数](#如何忽略某个参数)

    * [如何传入请求头](#如何传入请求头)

    * [如何重命名参数或者请求头的名称](#如何重命名参数或者请求头的名称)

    * [如何上传文件](#如何上传文件)

    * [如何上传文件列表](#如何上传文件列表)

    * [如何设置超时重试](#如何设置超时重试)

    * [如何设置请求超时时间](#如何设置请求超时时间)

    * [如何设置不打印日志](#如何设置不打印日志)

    * [如何取消已发起的请求](#如何取消已发起的请求)

    * [如何延迟发起一个请求](#如何延迟发起一个请求)

    * [如何对接口路径进行动态化拼接](#如何对接口路径进行动态化拼接)

    * [Https 如何配置信任所有证书](#https-如何配置信任所有证书)

    * [我不想一个接口写一个类怎么办](#我不想一个接口写一个类怎么办)

    * [我想用 Json 数组作为参数进行上传该怎么办](#我想用-json-数组作为参数进行上传该怎么办)

    * [接口参数的 Key 值是动态变化的该怎么办](#接口参数的-key-值是动态变化的该怎么办)

    * [如何设置自定义的 UA 标识](#如何设置自定义的-ua-标识)

    * [我想自定义一个 RequestBody 进行请求该怎么办](#我想自定义一个-requestbody-进行请求该怎么办)


# 集成文档

#### 服务器配置

```java
public class RequestServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.baidu.com/";
    }

    @NonNull
    @Override
    public BodyType getBodyType() {
        // 参数以 Json 格式提交（默认是表单）
        return BodyType.JSON;
    }
}
```

#### 框架初始化

* 需要配置请求结果处理，具体封装可以参考 [RequestHandler](app/src/main/java/com/hjq/easy/demo/http/model/RequestHandler.java)

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .build();

EasyConfig.with(okHttpClient)
        // 是否打印日志
        .setLogEnabled(false)
        // 设置服务器配置
        .setServer(server)
        // 设置请求处理策略
        .setHandler(new RequestHandler())
        // 设置拦截器
        .setInterceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(HttpRequest<?> httpRequest, HttpParams params, HttpHeaders headers) {
                        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    }
                })
        // 设置请求重试次数
        .setRetryCount(3)
        // 添加全局请求参数
        //.addParam("token", "6666666")
        // 添加全局请求头
        //.addHeader("time", "20191030")
        // 启用配置
        .into();
```

* 上述是创建配置，更新配置可以使用

```java
EasyConfig.getInstance()
        .addParam("token", data.getData().getToken());
```


# 使用文档

#### 配置接口

```java
public final class LoginApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "user/login";
    }

    /** 用户名 */
    private String userName;

    /** 登录密码 */
    private String password;

    public LoginApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public LoginApi setPassword(String password) {
        this.password = password;
        return this;
    }
}
```

* 可为这个类的字段加上一些注解

    * @HttpHeader：标记这个字段是一个请求头参数

    * @HttpIgnore：标记这个字段不会被发送给后台

    * @HttpRename：重新定义这个字段发送给后台的参数或者请求头名称

* 可在这个类实现一些接口

    * implements IRequestHost：实现这个接口之后可以重新指定这个请求的主机地址

    * implements IRequestType：实现这个接口之后可以重新指定这个请求的提交方式

    * implements IRequestCache：实现这个接口之后可以重新指定这个请求的缓存模式

    * implements IRequestClient：实现这个接口之后可以重新指定这个请求所用的 OkHttpClient 对象

* 字段作为请求参数的衡量标准

    * 假设某个字段的属性值为空，那么这个字段将不会作为请求参数发送给后台

    * 假设果某个字段类型是 String，属性值是空字符串，那么这个字段就会作为请求参数，如果是空对象则不会

    * 假设某个字段类型是 int，因为基本数据类型没有空值，所以这个字段一定会作为请求参数，但是可以换成 Integer 对象来避免，因为 Integer 的默认值是 null

* 我举个栗子：[https://www.baidu.com/api/user/getInfo](https://www.baidu.com/)，那么标准的写法就是

```java
public final class XxxApi implements IRequestServer, IRequestApi {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.baidu.com/";
    }

    @NonNull
    @Override
    public String getApi() {
        return "user/getInfo";
    }
}
```

#### 发起请求

* 需要配置请求状态及生命周期处理，具体封装可以参考 [BaseActivity](app/src/main/java/com/hjq/http/demo/BaseActivity.java)

```java
EasyHttp.post()
        .api(new LoginApi()
                .setUserName("Android 轮子哥")
                .setPassword("123456"))
        .request(new HttpCallback<HttpData<LoginBean>>(activity) {

            @Override
            public void onSucceed(HttpData<LoginBean> data) {
                ToastUtils.show("登录成功");
            }
        });
```

* 这里展示 post 用法，另外 EasyHttp 还支持 get、head、delete、put、patch 请求方式，这里不再过多演示

#### 上传文件

```java
public final class UpdateImageApi implements IRequestApi, IRequestType {

    @NonNull
    @Override
    public String getApi() {
        return "upload/";
    }

    @NonNull
    @Override
    public BodyType getBodyType() {
        // 上传文件需要使用表单的形式提交
        return BodyType.FORM;
    }

    /** 本地图片 */
    private File image;

    public UpdateImageApi(File image) {
        this.image = image;
    }

    public UpdateImageApi setImage(File image) {
        this.image = image;
        return this;
    }
}
```

```java
EasyHttp.post()
        .api(new UpdateImageApi(file))
        .request(new OnUpdateListener<Void>() {

            @Override
            public void onStart(Call call) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(int progress) {
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onSucceed(Void result) {
                ToastUtils.show("上传成功");
            }

            @Override
            public void onFail(Exception e) {
                ToastUtils.show("上传失败");
            }

            @Override
            public void onEnd(Call call) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
```

* **需要注意的是：如果上传的文件过多或者过大，可能会导致请求超时，可以重新设置本次请求超时时间，超时时间建议根据文件大小而定，具体设置超时方式文档有介绍，可以在本页面直接搜索。**

* 当然除了可以使用 `File` 类型的对象进行上传，还可以使用 `FileContentResolver`、`InputStream`、`RequestBody`、`MultipartBody.Part` 类型的对象进行上传，如果你需要批量上传，请使用 `List<File>`、`List<FileContentResolver>`、`List<InputStream>`、`List<RequestBody>`、`List<MultipartBody.Part>`、 类型的对象来做批量上传。

#### 下载文件

* 下载缓存策略：在指定下载文件 md5 或者后台有返回 md5 的情况下，下载框架默认开启下载缓存模式，如果这个文件已经存在手机中，并且经过 md5 校验文件完整，框架就不会重复下载，而是直接回调下载监听。减轻服务器压力，减少用户等待时间。

```java
EasyHttp.download()
        .method(HttpMethod.GET)
        .file(new File(Environment.getExternalStorageDirectory(), "微信.apk"))
        //.url("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
        .url("http://dldir1.qq.com/weixin/android/weixin708android1540.apk")
        .md5("2E8BDD7686474A7BC4A51ADC3667CABF")
        .listener(new OnDownloadListener() {

            @Override
            public void onStart(File file) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(File file, int progress) {
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onComplete(File file) {
                ToastUtils.show("下载完成：" + file.getPath());
                installApk(MainActivity.this, file);
            }

            @Override
            public void onError(File file, Exception e) {
                ToastUtils.show("下载出错：" + e.getMessage());
            }

            @Override
            public void onEnd(File file) {
                mProgressBar.setVisibility(View.GONE);
            }

        }).start();
```

#### 请求缓存

* 需要先实现读取和写入缓存的接口，已内置可以跳过，这里以 FastKV 为例

```java
public final class HttpCacheManager {

   private volatile static FastKV sFastKv;


   /**
    * 获取单例的 FastKV 实例
    */
   public static FastKV getFastKv() {
      if(sFastKv == null) {
         synchronized (RequestHandler.class) {
            if (sFastKv == null) {
                // 存放位置 文件名
               sFastKv = new FastKV.Builder(System.getProperty("user.dir") + "\\cache","http_cache_id").build();
            }
         }
      }
      return sFastKv;
   }

   /**
    * 生成缓存的 key
    */
   public static String generateCacheKey(HttpRequest<?> httpRequest) {
      IRequestApi requestApi = httpRequest.getRequestApi();
      return "用户 id" + "\n" + requestApi.getApi() + "\n" + GsonFactory.getSingletonGson().toJson(requestApi);
   }
}
```


```java
public final class RequestHandler implements IRequestHandler {

   
    ..................

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
```

* 首先请求缓存模式有四种方式，都在 `CacheMode` 这个枚举类中

```java
public enum CacheMode {

    /**
     * 默认（按照 Http 协议来缓存）
     */
    DEFAULT,

    /**
     * 不使用缓存（禁用 Http 协议缓存）
     */
    NO_CACHE,

    /**
     * 只使用缓存
     *
     * 已有缓存的情况下：读取缓存 -> 回调成功
     * 没有缓存的情况下：请求网络 -> 写入缓存 -> 回调成功
     */
    USE_CACHE_ONLY,

    /**
     * 优先使用缓存
     *
     * 已有缓存的情况下：先读缓存 —> 回调成功 —> 请求网络 —> 刷新缓存
     * 没有缓存的情况下：请求网络 -> 写入缓存 -> 回调成功
     */
    USE_CACHE_FIRST,

    /**
     * 只在网络请求失败才去读缓存
     */
    USE_CACHE_AFTER_FAILURE
}
```

* 为某个接口设置缓存模式

```java
public final class XxxApi implements IRequestApi, IRequestCache {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/";
    }

    @NonNull
    @Override
    public CacheMode getCacheMode() {
        // 设置优先使用缓存
        return CacheMode.USE_CACHE_FIRST;
    }
}
```

* 全局设置缓存模式

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public CacheMode getCacheMode() {
        // 只在请求失败才去读缓存
        return CacheMode.USE_CACHE_AFTER_FAILURE;
    }
}
```

# 疑难解答

#### 如何设置 Cookie

* EasyHttp 是基于 OkHttp 封装的，而 OkHttp 本身就是支持设置 Cookie，所以用法和 OkHttp 是一样的

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .cookieJar(new XxxCookieJar())
        .build();

EasyConfig.with(okHttpClient)
        .setXxx()
        .into();
```

#### 如何添加或者删除全局参数

* 添加全局请求参数

```java
EasyConfig.getInstance().addParam("key", "value");
```

* 移除全局请求参数

```java
EasyConfig.getInstance().removeParam("key");
```

* 添加全局请求头

```java
EasyConfig.getInstance().addHeader("key", "value");
```

* 移除全局请求头

```java
EasyConfig.getInstance().removeHeader("key");
```

#### 如何定义全局的动态参数

```java
EasyConfig.getInstance().setInterceptor(new IRequestInterceptor() {

    @Override
    public void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {
        headers.put("key", "value");
        params.put("key", "value");
    }
});
```

#### 如何在请求中忽略某个全局参数

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    @HttpIgnore
    private String token;
}
```

#### 如何获取服务器配置

```java
IRequestServer server = EasyConfig.getInstance().getServer();
// 获取当前全局的服务器主机地址
String host = server.getHost();
```

#### 如何修改接口的服务器配置

* 先定义一个服务器配置

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }
}
```

* 再将它应用到全局配置中

```java
EasyConfig.getInstance().setServer(new XxxServer());
```

* 如果只是针对某个接口可以这样配置

```java
public final class XxxApi extends XxxServer implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
}
```

* 如果不想单独定义一个类，也可以这样写

```java
public final class XxxApi implements IRequestServer, IRequestApi {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
}
```

#### 如何配置多域名

* 先定义一个普通接口的测试服和正式服的配置

```java
public class TestServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.test.xxxxxxx.com/";
    }
}
```

```java
public class ReleaseServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }
}
```

* 再将它应用到全局配置中

```java
IRequestServer server;
if (BuildConfig.DEBUG) {
    server = new TestServer();
} else {
    server = new ReleaseServer();
}
EasyConfig.getInstance().setServer(server);
```

* 假设要为 H5 业务模块设定特定服务器配置，可以这样做

```java
public class H5Server implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        IRequestServer server = EasyConfig.getInstance().getServer();
        if (server instanceof TestServer) {
            return "https://www.test.h5.xxxxxxx.com/";
        }
        return "https://www.h5.xxxxxxx.com/";
    }
}
```

* 在配置接口的时候继承 H5Server 就可以了，其他 H5 模块的配置也是雷同

```java
public final class UserAgreementApi extends H5Server implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "user/agreement";
    }
}
```

#### 如何修改参数的提交方式

* 以表单的形式提交参数（默认）

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public BodyType getBodyType() {
        return BodyType.FORM;
    }
}
```

* 以 Json 的形式提交参数

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public BodyType getBodyType() {
        return BodyType.JSON;
    }
}
```

* 当然也支持对某个接口进行单独配置

```java
public final class XxxApi implements IRequestApi, IRequestType {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @NonNull
    @Override
    public BodyType getBodyType() {
        return BodyType.JSON;
    }
}
```

* 表单和 Json 方式提交的优缺点对比

|  场景  | 表单方式  | Json 方式 |
| :----: | :------: |  :-----: |
|   多级参数  |  不支持  |   支持  |
|   文件上传  |   支持  |  不支持  |

#### 如何对接口进行加密或者解密

* 关于这个问题，其实可以利用框架中提供的 IRequestInterceptor 接口来实现，通过重写接口中的对应方法进行拦截，修改对象的内容从而达到加密的效果。

```java
public interface IRequestInterceptor {

    /**
     * 拦截参数
     *
     * @param httpRequest   接口对象
     * @param params        请求参数
     * @param headers       请求头参数
     */
    default void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {}

    /**
     * 拦截请求头
     *
     * @param httpRequest   接口对象
     * @param request       请求头对象
     * @return              返回新的请求头
     */
    @NonNull
    default Request interceptRequest(@NonNull HttpRequest<?> httpRequest, @NonNull Request request) {
        return request;
    }

    /**
     * 拦截器响应头
     *
     * @param httpRequest   接口对象
     * @param response      响应头对象
     * @return              返回新的响应头
     */
    @NonNull
    default Response interceptResponse(@NonNull HttpRequest<?> httpRequest, @NonNull Response response) {
        return response;
    }
}
```

```java
// 在框架初始化的时候设置拦截器
EasyConfig.with(okHttpClient)
        // 设置请求参数拦截器
        .setInterceptor(new XxxInterceptor())
        .into();
```

* 如果你只想对某个接口进行加解密，可以让 Api 类单独实现 IRequestInterceptor 接口，这样它就不会走全局的配置。

#### 如何忽略某个参数

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    @HttpIgnore
    private String address;
}
```

#### 如何传入请求头

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    @HttpHeader
    private String time;
}
```

#### 如何重命名参数或者请求头的名称

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    @HttpRename("k")
    private String keyword;
}
```

#### 如何上传文件

* 使用 File 对象上传

```java
public final class XxxApi implements IRequestApi {
    
    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private File file;
}
```

* 使用 InputStream 对象上传

```java
public final class XxxApi implements IRequestApi {
    
    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private InputStream inputStream;
}
```

* 使用 RequestBody 对象上传

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private RequestBody requestBody;
}
```

#### 如何上传文件列表

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private List<File> files;
}
```

#### 如何设置超时重试

```java
// 设置请求重试次数
EasyConfig.getInstance().setRetryCount(3);
// 设置请求重试时间
EasyConfig.getInstance().setRetryTime(1000);
```

#### 如何设置请求超时时间

* 全局配置（针对所有接口都生效）

```java
OkHttpClient.Builder builder = new OkHttpClient.Builder();
builder.readTimeout(5000, TimeUnit.MILLISECONDS);
builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
builder.connectTimeout(5000, TimeUnit.MILLISECONDS);

EasyConfig.with(builder.build())
        .into();
```

* 局部配置（只在某个接口上生效）

```java
public final class XxxApi implements IRequestApi, IRequestClient {

    @NonNull
    @Override
    public String getApi() {
        return "xxxx/";
    }

    @NonNull
    @Override
    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = EasyConfig.getInstance().getOkHttpClient().newBuilder();
        builder.readTimeout(5000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
        return builder.build();
    }
}
```

#### 如何设置不打印日志

```java
EasyConfig.getInstance().setLogEnabled(false);
```

#### 如何取消已发起的请求

```java
// 取消和这个 LifecycleOwner 关联的请求
EasyHttp.cancel(LifecycleOwner lifecycleOwner);
// 取消指定 Tag 标记的请求
EasyHttp.cancel(Object tag);
// 取消所有请求
EasyHttp.cancel();
```

#### 如何延迟发起一个请求

```java
EasyHttp.post(MainActivity.this)
        .api(new XxxApi())
        // 延迟 5 秒后请求
        .delay(5000)
        .request(new HttpCallback<HttpData<XxxBean>>(MainActivity.this) {

            @Override
            public void onSucceed(HttpData<XxxBean> result) {
                
            }
        });
```

#### 如何对接口路径进行动态化拼接

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "article/query/" + pageNumber + "/json";
    }

    @HttpIgnore
    private int pageNumber;

    public XxxApi setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }
}
```

#### Https 如何配置信任所有证书

* 在初始化 OkHttp 的时候这样设置

```java
HttpSslConfig sslConfig = HttpSslFactory.generateSslConfig();
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .sslSocketFactory(sslConfig.getsSLSocketFactory(), sslConfig.getTrustManager())
        .hostnameVerifier(HttpSslFactory.generateUnSafeHostnameVerifier())
        .build();
```
* 但是不推荐这样做，因为这样是不安全的，意味着每个请求都不会用 Https 去校验

* 当然框架中也提供了一些生成的证书的 API，具体请参见 com.hjq.http.ssl 包下的类

#### 我不想一个接口写一个类怎么办

* 先定义一个 URL 管理类，将 URL 配置到这个类中

```java
public final class HttpUrls {

    /** 获取用户信息 */
    public static final String GET_USER_INFO =  "user/getUserInfo";
}
```

* 然后在 EasyHttp 引入接口路径

```java
EasyHttp.post()
        .api(HttpUrls.GET_USER_INFO)
        .request(new HttpCallback<HttpData<XxxBean>>(this) {

            @Override
            public void onSucceed(HttpData<XxxBean> result) {

            }
        });
```

* 不过这种方式只能应用于没有参数的接口，有参数的接口还是需要写一个类，因为框架只会在 Api 类中去解析参数。

* 虽然 EasyHttp 开放了这种写法，但是身为作者的我并不推荐你这样写，因为这样写会导致扩展性很差，比如后续加参数，还要再改回来，并且无法对接口进行动态化配置。


#### 我想用 Json 数组作为参数进行上传该怎么办

* 由于 Api 类最终会转换成一个 JsonObject 类型的字符串，如果你需要上传 JsonArray 类型的字符串，请使用以下方式实现

```java
List<Xxx> parameter = new ArrayList<>();
list.add(xxx);
list.add(xxx);
String json = gson.toJson(parameter);

EasyHttp.post()
        .api(new XxxApi())
        .body(new JsonBody(json))
        .request(new HttpCallback<HttpData<Xxx>>(this) {

            @Override
            public void onSucceed(HttpData<Xxx> result) {

            }
        });
```

* 但是我个人不推荐将 JsonArray 作为参数的根部类型，因为这样的接口后续的扩展性极差。

#### 接口参数的 Key 值是动态变化的该怎么办

* 框架是通过反射解析 Api 类中的字段来作为参数的，字段名作为参数的 Key 值，字段值作为参数的 Value 值，由于 Java 无法动态更改类的字段名，所以无法通过正常的手段进行修改，你如果有这种需求，请通过以下方式进行实现

```java
HashMap<String, Object> parameter = new HashMap<>();

// 添加全局参数
HashMap<String, Object> globalParams = EasyConfig.getInstance().getParams();
Set<String> keySet = globalParams.keySet();
for (String key : keySet) {
    parameter.put(key, globalParams.get(key));
}

// 添加自定义参数
parameter.put("key1", value1);
parameter.put("key2", value2);

String json = gson.toJson(parameter);

EasyHttp.post()
        .api(new XxxApi())
        .body(new JsonBody(json))
        .request(new HttpCallback<HttpData<Xxx>>(this) {

            @Override
            public void onSucceed(HttpData<Xxx> result) {

            }
        });
```

#### 如何设置自定义的 UA 标识

* 首先 UA 是 User Agent 的简称，当我们没有设置自定义 UA 标识的时候，那么 OkHttp 会在 BridgeInterceptor 拦截器添加一个默认的 UA 标识，那么如何在 EasyHttp 设置自定义 UA 标识呢？其实很简单，UA 标识本质上其实就是一个请求头，在 EasyHttp 中添加一个请求头为 `"User-Agent` 的参数即可，至于怎么添加请求头，前面的文档已经有介绍了，这里不再赘述。

#### 我想自定义一个 RequestBody 进行请求该怎么办

* 在一些极端的情况下，框架无法满足使用的前提下，这个时候需要自定义 `RequestBody` 来实现，那么怎么使用自定义 `RequestBody` 呢？框架其实有开放方法，具体使用示例如下：

```java
EasyHttp.post()
        .api(new XxxApi())
        .body(RequestBody body)
        .request(new HttpCallback<HttpData<Xxx>>(this) {

            @Override
            public void onSucceed(HttpData<Xxx> result) {
                
            }
        });
```

* 需要注意的是：由于 Post 请求是将参数放置到 `RequestBody` 上面，而一个请求只能设置一个 `RequestBody`，如果你设置了自定义 `body(RequestBody body)`，那么框架将不会去将 `XxxApi` 类中的字段解析成参数。另外除了 Post 请求，Put 请求和 Patch 请求也可以使用这种方式进行设置，这里不再赘述。

