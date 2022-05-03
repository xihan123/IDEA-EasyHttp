package cn.xihan.lib.easyhttp.request

import cn.xihan.lib.easyhttp.model.HttpMethod

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2021/04/29
 * desc   : Options 请求
 */
class OptionsRequest : UrlRequest<OptionsRequest?>() {
    override val requestMethod: String
        get() = HttpMethod.OPTIONS.toString()
}