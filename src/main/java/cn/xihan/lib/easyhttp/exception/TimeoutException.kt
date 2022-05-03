package cn.xihan.lib.easyhttp.exception

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/06/25
 * desc   : 服务器超时异常
 */
class TimeoutException : HttpException {
    constructor(message: String) : super(message) {}
    constructor(message: String, cause: Throwable?) : super(message, cause) {}
}