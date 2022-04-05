package cn.xihan.http.config;


import cn.xihan.http.annotation.HttpIgnore;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/05/19
 *    desc   : 请求接口简单配置类
 */
public class RequestApi implements IRequestApi {

    /** 接口地址 */
    @HttpIgnore
    private final String mApi;

    public RequestApi(String api) {
        mApi = api;
    }


    @Override
    public String getApi() {
        return mApi;
    }


    @Override
    public String toString() {
        return mApi;
    }
}