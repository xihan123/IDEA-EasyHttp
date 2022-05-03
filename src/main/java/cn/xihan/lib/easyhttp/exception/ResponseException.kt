package cn.xihan.lib.easyhttp.exception

import okhttp3.Response

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/06/25
 * desc   : 服务器响应异常
 */
class ResponseException : HttpException {
    val response: Response

    constructor(message: String, response: Response) : super(message) {
        this.response = response
    }

    constructor(message: String, cause: Throwable?, response: Response) : super(message, cause) {
        this.response = response
    }
}