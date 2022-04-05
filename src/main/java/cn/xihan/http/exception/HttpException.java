package cn.xihan.http.exception;



/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/05/19
 *    desc   : 网络请求异常
 */
public class HttpException extends Exception {

    private final String mMessage;
    private Throwable mThrowable;

    public HttpException(String message) {
        super(message);
        mMessage = message;
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
        mMessage = message;
        mThrowable = cause;
    }

    /**
     * 获取错误信息
     */
    @Override
    public String getMessage() {
        return mMessage;
    }


    @Override
    public StackTraceElement[] getStackTrace() {
        if (mThrowable != null) {
            return mThrowable.getStackTrace();
        }
        return super.getStackTrace();
    }


    @Override
    public synchronized Throwable getCause() {
        if (mThrowable != null) {
            return mThrowable.getCause();
        }
        return super.getCause();
    }
}