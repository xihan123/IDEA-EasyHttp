package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.model.HttpMethod

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/07/20
 * desc   : 主体请求
 */
class PostRequest : BodyRequest<PostRequest?>() {
    override val requestMethod: String
        get() = HttpMethod.POST.toString()
}