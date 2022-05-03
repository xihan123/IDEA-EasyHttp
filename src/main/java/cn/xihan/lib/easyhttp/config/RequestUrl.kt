package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.annotation.HttpIgnore

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2022/03/03
 * desc   : 请求 url 简单配置类
 */
class RequestUrl(
    /** 主机地址  */
    @field:HttpIgnore private val mHost: String,
    /** 接口地址  */
    @field:HttpIgnore private val mApi: String
) : IRequestServer, IRequestApi {
    constructor(url: String) : this(url, "") {}


    override val api: String
        get() = mApi
    override val host: String
        get() = mHost

    override fun toString(): String {
        return mHost + mApi
    }
}