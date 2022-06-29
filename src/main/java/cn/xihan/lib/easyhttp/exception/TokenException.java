package cn.xihan.lib.easyhttp.exception;



/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/05/19
 *    desc   : Token 失效异常
 */
public final class TokenException extends HttpException {

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}