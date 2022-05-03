package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.model.HttpMethod

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/10/07
 * desc   : Delete 请求
 * doc    : Delete 请求该用 Url 还是 Body 来传递参数：
 * [https://stackoverflow.com/questions/299628/is-an-entity-body-allowed-for-an-http-delete-request](https://stackoverflow.com/questions/299628/is-an-entity-body-allowed-for-an-http-delete-request)
 */
class DeleteRequest : UrlRequest<DeleteRequest?>() {
    override val requestMethod: String
        get() = HttpMethod.DELETE.toString()
}