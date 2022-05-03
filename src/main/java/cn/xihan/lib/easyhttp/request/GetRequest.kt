package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.model.HttpMethod

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/07/20
 * desc   : Get 请求
 */
class GetRequest : UrlRequest<GetRequest>() {
    override val requestMethod: String
        get() = HttpMethod.GET.toString()
}