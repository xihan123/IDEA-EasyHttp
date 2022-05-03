package cn.xihan.lib.easyhttp.exception

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/11/16
 * desc   : MD5 校验异常
 */
class MD5Exception(message: String, val mD5: String) : HttpException(message)