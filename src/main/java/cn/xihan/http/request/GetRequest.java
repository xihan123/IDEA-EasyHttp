package cn.xihan.http.request;


import cn.xihan.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/07/20
 *    desc   : Get 请求
 */
public final class GetRequest extends UrlRequest<GetRequest> {

    public GetRequest() {

    }


    @Override
    public String getRequestMethod() {
        return HttpMethod.GET.toString();
    }
}