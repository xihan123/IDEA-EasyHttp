package cn.xihan.http.config;



import cn.xihan.http.EasyConfig;
import okhttp3.OkHttpClient;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2021/03/02
 *    desc   : OkHttpClient 配置
 */
public interface IRequestClient {

    /**
     * 获取 OkHttpClient
     */

    default OkHttpClient getOkHttpClient() {
        return EasyConfig.getInstance().getClient();
    }
}