package cn.xihan.lib.easyhttp.exception

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/06/25
 * desc   : 数据解析异常
 */
class DataException : HttpException {
    constructor(message: String) : super(message) {}
    constructor(message: String, cause: Throwable?) : super(message, cause) {}
}