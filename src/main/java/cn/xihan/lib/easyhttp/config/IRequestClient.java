package cn.xihan.lib.easyhttp.config;


import cn.xihan.lib.easyhttp.EasyConfig;
import okhttp3.OkHttpClient;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
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