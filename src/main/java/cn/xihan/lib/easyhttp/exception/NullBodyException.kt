package cn.xihan.lib.easyhttp.exception

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/12/24
 * desc   : 空实体异常
 */
class NullBodyException : HttpException {
    constructor(message: String) : super(message) {}
    constructor(message: String, cause: Throwable?) : super(message, cause) {}
}