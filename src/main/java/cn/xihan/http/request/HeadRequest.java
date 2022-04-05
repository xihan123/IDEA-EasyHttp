package cn.xihan.http.request;


import cn.xihan.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2020/10/07
 *    desc   : Head 请求
 */
public final class HeadRequest extends UrlRequest<HeadRequest> {

    public HeadRequest() {

    }


    @Override
    public String getRequestMethod() {
        return HttpMethod.HEAD.toString();
    }
}