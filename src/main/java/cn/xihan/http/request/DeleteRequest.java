package cn.xihan.http.request;


import cn.xihan.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2020/10/07
 *    desc   : Delete 请求
 *    doc    : Delete 请求该用 Url 还是 Body 来传递参数：
 *             <a href="https://stackoverflow.com/questions/299628/is-an-entity-body-allowed-for-an-http-delete-request">https://stackoverflow.com/questions/299628/is-an-entity-body-allowed-for-an-http-delete-request</a>
 */
public final class DeleteRequest extends UrlRequest<DeleteRequest> {

    public DeleteRequest() {

    }

    @Override
    public String getRequestMethod() {
        return HttpMethod.DELETE.toString();
    }
}