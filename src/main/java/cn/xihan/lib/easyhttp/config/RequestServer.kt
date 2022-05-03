package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.annotation.HttpIgnore

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 服务器简单配置
 */
class RequestServer(
    /** 主机地址  */
    @field:HttpIgnore private val mHost: String
) : IRequestServer {
    override val host: String
        get() = mHost


    override fun toString(): String {
        return mHost
    }
}