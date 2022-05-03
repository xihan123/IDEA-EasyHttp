package cn.xihan.lib.easyhttp.body

import cn.xihan.lib.easyhttp.EasyUtils.unescapeJson
import cn.xihan.lib.easyhttp.model.ContentType
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/12/28
 * desc   : Json 参数提交
 */
class JsonBody : RequestBody {
    /**
     * 获取 Json 字符串
     */
    /** Json 数据  */
    val json: String

    /** 字节数组  */
    private val mBytes: ByteArray

    constructor(map: Map<*, *>?) : this(JSONObject(map)) {}
    constructor(list: List<*>?) : this(JSONArray(list)) {}
    constructor(jsonObject: JSONObject) {
        json = unescapeJson(jsonObject.toString())
        mBytes = json.toByteArray()
    }

    constructor(jsonArray: JSONArray) {
        json = unescapeJson(jsonArray.toString())
        mBytes = json.toByteArray()
    }

    constructor(json: String) {
        this.json = json
        mBytes = json.toByteArray()
    }

    override fun contentType(): MediaType? {
        return ContentType.JSON
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
        return json
    }
}