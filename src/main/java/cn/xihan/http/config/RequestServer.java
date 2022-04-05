package cn.xihan.http.config;


import cn.xihan.http.annotation.HttpIgnore;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/05/19
 *    desc   : 服务器简单配置
 */
public final class RequestServer implements IRequestServer {

    /** 主机地址 */
    @HttpIgnore
    private final String mHost;

    public RequestServer(String host) {
        mHost = host;
    }


    @Override
    public String getHost() {
        return mHost;
    }


    @Override
    public String toString() {
        return mHost;
    }
}