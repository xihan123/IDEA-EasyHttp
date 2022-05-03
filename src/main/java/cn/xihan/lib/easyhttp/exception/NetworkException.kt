package cn.xihan.lib.easyhttp.exception

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/06/25
 * desc   : 网络连接异常
 */
class NetworkException : HttpException {
    constructor(message: String) : super(message) {}
    constructor(message: String, cause: Throwable?) : super(message, cause) {}
}