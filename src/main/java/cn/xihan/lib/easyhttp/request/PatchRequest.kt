package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.model.HttpMethod

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/10/07
 * desc   : Patch 请求
 */
class PatchRequest : BodyRequest<PatchRequest?>() {
    override val requestMethod: String
        get() = HttpMethod.PATCH.toString()
}