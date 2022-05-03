package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.model.CacheMode

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2021/05/22
 * desc   : 请求缓存配置
 */
interface IRequestCache {
    /**
     * 获取缓存的模式
     */
    val cacheMode: CacheMode?

    /**
     * 获取缓存的有效时长（以毫秒为单位）
     */
    val cacheTime: Long
        get() = Long.MAX_VALUE
}