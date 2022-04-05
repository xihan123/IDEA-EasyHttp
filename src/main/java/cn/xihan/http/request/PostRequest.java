package cn.xihan.http.request;


import cn.xihan.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/07/20
 *    desc   : 主体请求
 */
public final class PostRequest extends BodyRequest<PostRequest> {

    public PostRequest() {

    }


    @Override
    public String getRequestMethod() {
        return HttpMethod.POST.toString();
    }
}