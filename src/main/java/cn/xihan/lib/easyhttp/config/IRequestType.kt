package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.model.BodyType

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/01/01
 * desc   : 请求接口配置
 */
interface IRequestType {
    /**
     * 获取参数的提交类型
     */
    val bodyType: BodyType?
}