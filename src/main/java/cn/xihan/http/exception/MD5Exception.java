package cn.xihan.http.exception;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2019/11/16
 *    desc   : MD5 校验异常
 */
public final class MD5Exception extends HttpException {

    private final String mMD5;

    public MD5Exception(String message, String md5) {
        super(message);
        mMD5 = md5;
    }

    public String getMD5() {
        return mMD5;
    }
}