package cn.xihan.lib.easyhttp

import cn.xihan.lib.easyhttp.annotation.HttpIgnore
import cn.xihan.lib.easyhttp.annotation.HttpRename
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.reflect.*
import java.net.InetAddress
import java.net.URLEncoder
import java.nio.file.Files
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/11/17
 * desc   : 请求工具类
 */
object EasyUtils {
    // 加载中字符串
    const val LOADING = "加载中..."

    // 请求出错字符串
    const val ERROR = "请求出错"

    // 登录失效字符串
    const val LOGIN_INVALID = "登录失效"

    // 服务器连接超时字符串
    const val TIME_OUT = "连接超时"

    // 服务器响应异常字符串
    const val RESPONSE_ERROR = "响应异常"

    // 服务器连接异常字符串
    const val CONNECT_ERROR = "连接异常"

    // 请求被中断字符串
    const val REQUEST_CANCEL = "请求被中断"

    // 服务器数据返回异常字符串
    const val DATA_ERROR = "数据异常"
    @JvmStatic
    fun post(runnable: Runnable?) {
        Thread(runnable).start()
    }

    // 延迟执行
    @JvmStatic
    fun postDelayed(runnable: Runnable?, delay: Long) {
        try {
            Thread.sleep(delay)
            Thread(runnable).start()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 关闭流
     */
    @JvmStatic
    fun closeStream(closeable: Closeable?) {
        if (closeable == null) {
            return
        }
        try {
            closeable.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 判断对象是否为 Bean 类
     */
    fun isBeanType(`object`: Any?): Boolean {
        if (`object` == null) {
            return false
        }
        return if (`object` is Enum<*>) {
            false
        } else !(`object` is Number || `object` is CharSequence || `object` is Boolean ||
                `object` is File || `object` is InputStream || `object` is RequestBody ||
                `object` is Char || `object` is JSONObject || `object` is JSONArray)
        // Number：Long、Integer、Short、Double、Float、Byte
        // CharSequence：String、StringBuilder、StringBuilder
    }

    /**
     * 判断是否包含存在流参数
     */
    @JvmStatic
    fun isMultipartParameter(fields: List<Field>): Boolean {
        for (field in fields) {
            // 允许访问私有字段
            field.isAccessible = true
            val modifiers = field.modifiers
            // 如果这是一个常量字段，则直接忽略掉，例如 Parcelable 接口中的 CREATOR 字段
            // https://github.com/getActivity/EasyHttp/issues/112
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                continue
            }

            // 获取对象的类型
            var clazz = field.type

            // 获取对象上面实现的接口
            val interfaces = clazz.interfaces
            for (i in 0..interfaces.size) {
                val temp: Class<*>? = if (i == interfaces.size) {
                    clazz
                } else {
                    interfaces[i]
                }
                if (MutableList::class.java == temp) {
                    // 如果实现了 List 接口，则取第一个位置的泛型
                    if (isMultipartClass(getFieldGenericType(field, 0))) {
                        return true
                    }
                } else if (MutableMap::class.java == temp) {
                    // 如果实现了 Map 接口，则取第二个位置的泛型
                    if (isMultipartClass(getFieldGenericType(field, 1))) {
                        return true
                    }
                }
            }
            do {
                if (isMultipartClass(clazz)) {
                    return true
                }
                // 获取对象的父类类型
                clazz = clazz.superclass
            } while (Any::class.java != clazz)
        }
        return false
    }

    /**
     * 获取字段中携带的泛型类型
     *
     * @param field             字段对象
     * @param position          泛型的位置
     */
    fun getFieldGenericType(field: Field, position: Int): Type? {
        val type = field.genericType as? ParameterizedType ?: return null

        // 获取泛型数组
        val actualTypeArguments = type.actualTypeArguments
        // 如果泛型的位置超过数组的长度，证明这个位置的泛型不存在
        if (position >= actualTypeArguments.size) {
            return null
        }

        // 获取指定位置上的泛型
        val actualType = actualTypeArguments[position]
        // 如果这是一个通配符类型
        if (actualType is WildcardType) {
            // 获取上界通配符
            val upperBounds = actualType.upperBounds
            return if (upperBounds.isEmpty()) {
                null
            } else upperBounds[0]
        }
        return actualType
    }

    /**
     * 判断 Type 是否为流类型
     */
    fun isMultipartClass(type: Type?): Boolean {
        return if (type == null) {
            false
        } else File::class.java == type || InputStream::class.java == type || RequestBody::class.java == type || MultipartBody.Part::class.java == type
    }

    /**
     * 将 List 集合转 JsonArray 对象
     */
    fun listToJsonArray(list: List<*>?): JSONArray {
        val jsonArray = JSONArray()
        if (list == null || list.isEmpty()) {
            return jsonArray
        }
        for (value in list) {
            if (value == null) {
                continue
            }
            jsonArray.put(convertObject(value))
        }
        return jsonArray
    }

    /**
     * 将 Map 集合转成 JsonObject 对象
     */
    fun mapToJsonObject(map: Map<*, *>?): JSONObject {
        val jsonObject = JSONObject()
        if (map == null || map.isEmpty()) {
            return jsonObject
        }
        val keySet = map.keys
        for (key in keySet) {
            val value = map[key] ?: continue
            try {
                jsonObject.put(key.toString(), convertObject(value))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return jsonObject
    }

    /**
     * 将 Bean 类转成 HashMap 对象
     */
    fun beanToHashMap(`object`: Any?): HashMap<String, Any>? {
        if (`object` == null) {
            return null
        }
        if (`object` is Enum<*>) {
            return null
        }
        val fields = `object`.javaClass.declaredFields
        val data = HashMap<String, Any>(fields.size)
        for (field in fields) {
            // 允许访问私有字段
            field.isAccessible = true
            val modifiers = field.modifiers
            // 如果这是一个常量字段，则直接忽略掉，例如 Parcelable 接口中的 CREATOR 字段
            // https://github.com/getActivity/EasyHttp/issues/112
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                continue
            }
            try {
                // 获取字段的对象
                val value = field[`object`]

                // 前提是这个字段值不能为空（基本数据类型有默认的值，而对象默认的值为 null）
                // 又或者这个字段需要忽略，则进行忽略
                if (value == null || field.isAnnotationPresent(HttpIgnore::class.java)) {
                    continue
                }

                // 获取字段的名称
                var key: String
                val annotation = field.getAnnotation(HttpRename::class.java)
                if (annotation != null) {
                    key = annotation.value
                } else {
                    key = field.name
                    // 如果是内部类则会出现一个字段名为 this$0 的外部类对象，会导致无限递归，这里要忽略掉，如果使用静态内部类则不会出现这个问题
                    // 和规避 Kotlin 自动生成的伴生对象：https://github.com/getActivity/EasyHttp/issues/15
                    if (key.matches(Regex("this\\$\\d+")) || "Companion" == key) {
                        continue
                    }
                }
                data[key] = convertObject(value)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return data
    }

    /**
     * 对象转换
     */
    @JvmStatic
    fun convertObject(`object`: Any): Any {
        return when (`object`) {
            is List<*> -> {
                // 如果这是一个 List 参数
                listToJsonArray(`object`)
            }
            is Map<*, *> -> {
                // 如果这是一个 Map 参数
                mapToJsonObject(`object`)
            }
            else -> (`object` as? Enum<*>)?.// 如果这是一个枚举的参数
            toString()
                ?: if (isBeanType(`object`)) {
                    // 如果这是一个 Bean 参数
                    mapToJsonObject(beanToHashMap(`object`))
                } else {
                    // 如果这是一个普通的参数
                    `object`
                }
        }
    }

    /**
     * 获取对象上面的泛型
     */
    @JvmStatic
    fun getGenericType(`object`: Any?): Type {
        if (`object` == null) {
            return Void::class.java
        }
        // 获取接口上面的泛型
        val types = `object`.javaClass.genericInterfaces
        if (types.isNotEmpty()) {
            // 如果这个对象是直接实现了接口，并且携带了泛型
            return (types[0] as ParameterizedType).actualTypeArguments[0]
        }

        // 获取父类上面的泛型
        val genericSuperclass = `object`.javaClass.genericSuperclass as? ParameterizedType ?: return Void::class.java
        val actualTypeArguments = genericSuperclass.actualTypeArguments
        return if (actualTypeArguments.isEmpty()) {
            Void::class.java
        } else actualTypeArguments[0]

        // 如果这个对象是通过类继承，并且携带了泛型
    }

    /**
     * 获取进度百分比
     */
    @JvmStatic
    fun getProgressProgress(totalByte: Long, currentByte: Long): Int {
        return if (totalByte <= 0) {
            // 返回 -1 表示无法获取进度
            -1
        } else (currentByte.toDouble() / totalByte * 100).toInt()
        // 计算百分比，这里踩了两个坑
        // 当文件很大的时候：字节数 * 100 会超过 int 最大值，计算结果会变成负数
        // 还有需要注意的是，long 除以 long 等于 long，这里的字节数除以总字节数应该要 double 类型的
    }

    /**
     * 字符串编码
     */
    @JvmStatic
    fun encodeString(text: String?): String {
        return if (isEmpty(text)) {
            ""
        } else try {
            URLEncoder.encode(text!!, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            text!!
        }
    }

    /**
     * 格式化 Json 字符串
     */
    @JvmStatic
    fun formatJson(json: String?): String {
        if (json == null) {
            return ""
        }
        try {
            if (json.startsWith("{")) {
                return unescapeJson(JSONObject(json).toString(4))
            } else if (json.startsWith("[")) {
                return unescapeJson(JSONArray(json).toString(4))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return json
    }

    /**
     * 去除 Json 中非必要的字符转义
     */
    @JvmStatic
    fun unescapeJson(json: String): String {
        return if (isEmpty(json)) {
            ""
        } else json.replace("\\/", "/")
        // https://github.com/getActivity/EasyHttp/issues/67
    }

    /**
     * 获取对象的唯一标记
     */
    @JvmStatic
    fun getObjectTag(`object`: Any?): String? {
        return `object`?.toString()
    }

    /**
     * 创建文件夹
     */
    @JvmStatic
    fun createFolder(targetFolder: File) {
        if (targetFolder.exists()) {
            if (targetFolder.isDirectory) {
                return
            }
            targetFolder.delete()
        }
        targetFolder.mkdirs()
    }

    /**
     * 获取文件的 md5
     */
    @JvmStatic
    fun getFileMd5(inputStream: InputStream?): String? {
        if (inputStream == null) {
            return ""
        }
        var digestInputStream: DigestInputStream? = null
        try {
            var messageDigest = MessageDigest.getInstance("MD5")
            digestInputStream = DigestInputStream(inputStream, messageDigest)
            val buffer = ByteArray(1024 * 256)
            while (true) {
                if (digestInputStream.read(buffer) <= 0) {
                    break
                }
            }
            messageDigest = digestInputStream.messageDigest
            val md5 = messageDigest.digest()
            val sb = StringBuilder()
            for (b in md5) {
                sb.append(String.format("%02X", b))
            }
            return sb.toString().lowercase(Locale.getDefault())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeStream(inputStream)
            closeStream(digestInputStream)
        }
        return null
    }

    /**
     * 打开文件的输入流
     */
    @JvmStatic
    @Throws(IOException::class)
    fun openFileInputStream(file: File): InputStream {
        return Files.newInputStream(file.toPath())
    }

    /**
     * 打开文件的输出流
     */
    @JvmStatic
    @Throws(IOException::class)
    fun openFileOutputStream(file: File): OutputStream {
        return Files.newOutputStream(file.toPath())
    }

    /**
     * 判断字符串是否为空
     */
    @JvmStatic
    fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty() || str.isBlank()
    }// 获取本机IP地址


    /**
     * 判断网络是否可用
     */
    val isNetworkAvailable: Boolean
        get() = try {
            val addr = InetAddress.getByName("www.baidu.com")
            val state = addr.isReachable(3000)

            // 获取本机IP地址
            val localAddr = InetAddress.getLocalHost()
            val state1 = localAddr.hostName == "127.0.0.1"
            state || state1
        } catch (e: Exception) {
            false
        }
}