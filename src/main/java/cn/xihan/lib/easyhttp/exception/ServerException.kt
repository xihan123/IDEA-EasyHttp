package cn.xihan.lib.easyhttp.exception

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/12/01
 * desc   : 服务器连接异常
 */
class ServerException : HttpException {
    constructor(message: String) : super(message) {}
    constructor(message: String, cause: Throwable?) : super(message, cause) {}
}