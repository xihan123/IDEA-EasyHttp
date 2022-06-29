package cn.xihan.lib.easyhttp.exception;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/06/25
 *    desc   : 网络连接异常
 */
public final class NetworkException extends HttpException {

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}