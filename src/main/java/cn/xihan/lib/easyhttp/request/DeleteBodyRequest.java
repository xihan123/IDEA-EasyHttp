package cn.xihan.lib.easyhttp.request;


import cn.xihan.lib.easyhttp.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2020/10/07
 *    desc   : Delete 请求（参数使用 Body 传递）
 */
public final class DeleteBodyRequest extends BodyRequest<DeleteBodyRequest> {

    public DeleteBodyRequest() {
        super();
    }

    @Override
    public String getRequestMethod() {
        return HttpMethod.DELETE.toString();
    }
}