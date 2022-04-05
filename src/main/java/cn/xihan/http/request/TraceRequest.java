package cn.xihan.http.request;


import cn.xihan.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2021/04/29
 *    desc   : Trace 请求
 */
public final class TraceRequest extends UrlRequest<TraceRequest> {

    public TraceRequest() {

    }

    @Override
    public String getRequestMethod() {
        return HttpMethod.TRACE.toString();
    }
}