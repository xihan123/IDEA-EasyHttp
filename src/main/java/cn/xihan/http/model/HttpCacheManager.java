package cn.xihan.http.model;


import cn.xihan.fastkv.FastKV;
import cn.xihan.gson.factory.GsonFactory;
import cn.xihan.http.config.IRequestApi;
import cn.xihan.http.request.HttpRequest;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2022/03/22
 *    desc   : Http 缓存管理器
 */
public final class HttpCacheManager {

   private volatile static FastKV sFastKv;


   /**
    * 获取单例的 FastKV 实例
    */
   public static FastKV getFastKv() {
      if(sFastKv == null) {
         synchronized (RequestHandler.class) {
            if (sFastKv == null) {
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