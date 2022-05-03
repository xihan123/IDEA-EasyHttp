package cn.xihan.lib.easyhttp.exception

import cn.xihan.lib.easyhttp.model.HttpData

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/06/25
 * desc   : 返回结果异常
 */
class ResultException : HttpException {
    val httpData: HttpData<*>

    constructor(message: String, data: HttpData<*>) : super(message) {
        httpData = data
    }

    constructor(message: String, cause: Throwable?, data: HttpData<*>) : super(message, cause) {
        httpData = data
    }
}