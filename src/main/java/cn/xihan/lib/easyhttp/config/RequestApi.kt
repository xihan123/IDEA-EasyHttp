package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.annotation.HttpIgnore

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 请求接口简单配置类
 */
open class RequestApi(
    /** 接口地址  */
    @field:HttpIgnore override val api: String
) : IRequestApi {

    override fun toString(): String {
        return api
    }
}