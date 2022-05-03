package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.EasyConfig

import okhttp3.OkHttpClient




/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2021/03/02
 * desc   : OkHttpClient 配置
 */
interface IRequestClient {
    /**
     * 获取 OkHttpClient
     */
    fun getOkHttpClient(): OkHttpClient {
        return EasyConfig.getInstance().getClient()
    }
}