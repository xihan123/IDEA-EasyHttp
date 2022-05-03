package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.model.HttpMethod

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/10/07
 * desc   : Put 请求
 */
class PutRequest : BodyRequest<PutRequest?>() {
    override val requestMethod: String
        get() = HttpMethod.PUT.toString()
}