package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.model.BodyType
import cn.xihan.lib.easyhttp.model.CacheMode

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 请求服务配置
 */
interface IRequestServer : IRequestHost, IRequestClient, IRequestType, IRequestCache {
    // 默认以表单的方式提交
    override val bodyType: BodyType?
        get() =// 默认以表单的方式提交
            BodyType.FORM

    // 默认的缓存方式
    override val cacheMode: CacheMode?
        get() =// 默认的缓存方式
            CacheMode.DEFAULT
}