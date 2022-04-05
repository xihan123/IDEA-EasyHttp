package cn.xihan.http.exception;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/12/24
 *    desc   : 空实体异常
 */
public final class NullBodyException extends HttpException {

    public NullBodyException(String message) {
        super(message);
    }

    public NullBodyException(String message, Throwable cause) {
        super(message, cause);
    }
}