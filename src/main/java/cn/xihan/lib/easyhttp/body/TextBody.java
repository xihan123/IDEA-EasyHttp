package cn.xihan.lib.easyhttp.body;


import cn.xihan.lib.easyhttp.model.ContentType;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import java.io.IOException;

/**
 * author : Android 轮子哥
 * github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 * time   : 2020/10/26
 * desc   : 文本参数提交
 */
public class TextBody extends RequestBody {

    /**
     * 字符串数据
     */
    private final String mText;

    /**
     * 字节数组
     */
    private final byte[] mBytes;

    public TextBody() {
        this("");
    }

    public TextBody(String text) {
        mText = text;
        mBytes = mText.getBytes();
    }

    @Override
    public MediaType contentType() {
        return ContentType.TEXT;
    }

    @Override
    public long contentLength() {
        // 需要注意：这里需要用字节数组的长度来计算
        return mBytes.length;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.write(mBytes, 0, mBytes.length);
    }

    @Override
    public String toString() {
        return mText;
    }
}