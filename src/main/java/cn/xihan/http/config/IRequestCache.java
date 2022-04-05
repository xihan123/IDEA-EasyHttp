package cn.xihan.http.config;


import cn.xihan.http.model.CacheMode;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2021/05/22
 *    desc   : 请求缓存配置
 */
public interface IRequestCache {

    /**
     * 获取缓存的模式
     */

    CacheMode getCacheMode();

    /**
     * 获取缓存的有效时长（以毫秒为单位）
     */
    long getCacheTime();
}