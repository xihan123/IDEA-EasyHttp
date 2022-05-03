package cn.xihan.lib.easyhttp.body

import cn.xihan.lib.easyhttp.model.ContentType
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.IOException

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/10/26
 * desc   : 文本参数提交
 */
class TextBody @JvmOverloads constructor(
    /** 字符串数据  */
    private val mText: String = ""
) : RequestBody() {
    /** 字节数组  */
    private val mBytes: ByteArray = mText.toByteArray()

    override fun contentType(): MediaType? {
        return ContentType.TEXT
    }

    override fun contentLength(): Long {
        // 需要注意：这里需要用字节数组的长度来计算
        return mBytes.size.toLong()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        sink.write(mBytes, 0, mBytes.size)
    }

    override fun toString(): String {
        return mText
    }
}