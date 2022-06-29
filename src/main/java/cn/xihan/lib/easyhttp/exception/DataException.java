package cn.xihan.lib.easyhttp.exception;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/06/25
 *    desc   : 数据解析异常
 */
public final class DataException extends HttpException {

    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}