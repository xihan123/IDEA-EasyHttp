package cn.xihan.http.config;


import cn.xihan.http.model.BodyType;
import cn.xihan.http.model.CacheMode;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/05/19
 *    desc   : 请求服务配置
 */
public interface IRequestServer extends
        IRequestHost, IRequestClient,
        IRequestType, IRequestCache {


    @Override
    default BodyType getBodyType() {
        // 默认以表单的方式提交
        return BodyType.FORM;
    }


    @Override
    default CacheMode getCacheMode() {
        // 默认的缓存方式
        return CacheMode.DEFAULT;
    }

    @Override
    default long getCacheTime() {
        return Long.MAX_VALUE;
    }
}