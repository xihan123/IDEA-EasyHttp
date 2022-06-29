package cn.xihan.lib.easyhttp;


import cn.xihan.lib.easyhttp.annotation.HttpIgnore;
import cn.xihan.lib.easyhttp.annotation.HttpRename;
import cn.xihan.lib.easyhttp.model.ThreadSchedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.*;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * author : Android 轮子哥
 * github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 * time   : 2019/11/17
 * desc   : 请求工具类
 */
public final class EasyUtils {

    // 加载中字符串
    public static final String LOADING = "加载中...";

    // 请求出错字符串
    public static final String ERROR = "请求出错";

    // 登录失效字符串
    public static final String LOGIN_INVALID = "登录失效";

    // 服务器连接超时字符串
    public static final String TIME_OUT = "连接超时";

    // 服务器响应异常字符串
    public static final String RESPONSE_ERROR = "响应异常";

    // 服务器连接异常字符串
    public static final String CONNECT_ERROR = "连接异常";

    // 请求被中断字符串
    public static final String REQUEST_CANCEL = "请求被中断";

    // 服务器数据返回异常字符串
    public static final String DATA_ERROR = "数据异常";

    private static final Map<Class<?>, List<Field>> CLASS_LIST_LRU_CACHE = new HashMap<>();


    /**
     * 在子线程中执行
     */
    public static void runOnIOThread(Runnable runnable) {
        new Thread(runnable).start();
    }


    /**
     * 运行在指定线程
     */
    public static void runOnAssignThread(ThreadSchedulers schedulers, Runnable runnable) {
        switch (schedulers) {
            case IOThread:
                runOnIOThread(runnable);
                break;
            case MainThread:
            default:
                runnable.run();
                break;
        }
    }

    /**
     * 延迟一段时间执行
     */
    public static void postDelayed(Runnable runnable, long delayMillis) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMillis);
                runnable.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    /**
     * 关闭流
     */
    public static void closeStream(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断对象是否为 Bean 类
     */
    public static boolean isBeanType(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Enum) {
            return false;
        }
        // Number：Long、Integer、Short、Double、Float、Byte
        // CharSequence：String、StringBuilder、StringBuilder
        return !(object instanceof Number || object instanceof CharSequence || object instanceof Boolean ||
                object instanceof File || object instanceof InputStream || object instanceof RequestBody ||
                object instanceof Character || object instanceof JSONObject || object instanceof JSONArray);
    }

    /**
     * 判断是否包含存在流参数
     */
    public static boolean isMultipartParameter(List<Field> fields) {
        for (Field field : fields) {
            // 允许访问私有字段
            field.setAccessible(true);

            if (EasyUtils.isConstantField(field)) {
                continue;
            }

            // 获取对象的类型
            Class<?> clazz = field.getType();

            // 获取对象上面实现的接口
            Class<?>[] interfaces = clazz.getInterfaces();
            for (int i = 0; i <= interfaces.length; i++) {
                Class<?> temp;
                if (i == interfaces.length) {
                    temp = clazz;
                } else {
                    temp = interfaces[i];
                }

                if (List.class.equals(temp)) {
                    // 如果实现了 List 接口，则取第一个位置的泛型
                    if (isMultipartClass(getFieldGenericType(field, 0))) {
                        return true;
                    }
                } else if (Map.class.equals(temp)) {
                    // 如果实现了 Map 接口，则取第二个位置的泛型
                    if (isMultipartClass(getFieldGenericType(field, 1))) {
                        return true;
                    }
                }
            }

            do {
                if (isMultipartClass(clazz)) {
                    return true;
                }
                // 获取对象的父类类型
                clazz = clazz.getSuperclass();
            } while (clazz != null && !Object.class.equals(clazz));
        }
        return false;
    }

    /**
     * 获取字段中携带的泛型类型
     *
     * @param field    字段对象
     * @param position 泛型的位置
     */
    public static Type getFieldGenericType(Field field, int position) {
        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            return null;
        }

        // 获取泛型数组
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        // 如果泛型的位置超过数组的长度，证明这个位置的泛型不存在
        if (position >= actualTypeArguments.length) {
            return null;
        }

        // 获取指定位置上的泛型
        Type actualType = actualTypeArguments[position];
        // 如果这是一个通配符类型
        if (actualType instanceof WildcardType) {
            // 获取上界通配符
            Type[] upperBounds = ((WildcardType) actualType).getUpperBounds();
            if (upperBounds.length == 0) {
                return null;
            }
            return upperBounds[0];
        }
        return actualType;
    }

    /**
     * 判断 Type 是否为流类型
     */
    public static boolean isMultipartClass(Type type) {
        if (type == null) {
            return false;
        }
        return File.class.equals(type) || InputStream.class.equals(type)
                || RequestBody.class.equals(type) || MultipartBody.Part.class.equals(type);
    }

    /**
     * 将 List 集合转 JsonArray 对象
     */
    public static JSONArray listToJsonArray(List<?> list) {
        JSONArray jsonArray = new JSONArray();
        if (list == null || list.isEmpty()) {
            return jsonArray;
        }

        for (Object value : list) {
            if (value == null) {
                continue;
            }
            jsonArray.put(convertObject(value));
        }
        return jsonArray;
    }

    /**
     * 将 Map 集合转成 JsonObject 对象
     */
    public static JSONObject mapToJsonObject(Map<?, ?> map) {
        JSONObject jsonObject = new JSONObject();
        if (map == null || map.isEmpty()) {
            return jsonObject;
        }

        Set<?> keySet = map.keySet();
        for (Object key : keySet) {
            Object value = map.get(key);
            if (value == null) {
                continue;
            }
            try {
                jsonObject.put(String.valueOf(key), convertObject(value));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * 将 Bean 类转成 HashMap 对象
     */
    public static HashMap<String, Object> beanToHashMap(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Enum) {
            return null;
        }

        Field[] fields = object.getClass().getDeclaredFields();
        HashMap<String, Object> data = new HashMap<>(fields.length);
        for (Field field : fields) {
            // 允许访问私有字段
            field.setAccessible(true);

            if (EasyUtils.isConstantField(field)) {
                continue;
            }

            try {
                // 获取字段的对象
                Object value = field.get(object);

                // 前提是这个字段值不能为空（基本数据类型有默认的值，而对象默认的值为 null）
                // 又或者这个字段需要忽略，则进行忽略
                if (value == null || field.isAnnotationPresent(HttpIgnore.class)) {
                    continue;
                }

                // 获取字段的名称
                String key;
                HttpRename annotation = field.getAnnotation(HttpRename.class);
                if (annotation != null) {
                    key = annotation.value();
                } else {
                    key = field.getName();
                    // 如果是内部类则会出现一个字段名为 this$0 的外部类对象，会导致无限递归，这里要忽略掉，如果使用静态内部类则不会出现这个问题
                    // 和规避 Kotlin 自动生成的伴生对象：https://github.com/getActivity/EasyHttp/issues/15
                    if (key.matches("this\\$\\d+") || "Companion".equals(key)) {
                        continue;
                    }
                }

                data.put(key, convertObject(value));

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    /**
     * 对象转换
     */
    public static Object convertObject(Object object) {
        if (object instanceof List) {
            // 如果这是一个 List 参数
            return listToJsonArray(((List<?>) object));
        } else if (object instanceof Map) {
            // 如果这是一个 Map 参数
            return mapToJsonObject(((Map<?, ?>) object));
        } else if (object instanceof Enum) {
            // 如果这是一个枚举的参数
            return String.valueOf(object);
        } else if (isBeanType(object)) {
            // 如果这是一个 Bean 参数
            return mapToJsonObject(beanToHashMap(object));
        } else {
            // 如果这是一个普通的参数
            return object;
        }
    }

    /**
     * 获取对象上面的泛型
     */
    public static Type getGenericType(Object object) {
        if (object == null) {
            return Void.class;
        }
        // 获取接口上面的泛型
        Type[] types = object.getClass().getGenericInterfaces();
        if (types.length > 0) {
            // 如果这个对象是直接实现了接口，并且携带了泛型
            return ((ParameterizedType) types[0]).getActualTypeArguments()[0];
        }

        // 获取父类上面的泛型
        Type genericSuperclass = object.getClass().getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return Void.class;
        }

        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        if (actualTypeArguments.length == 0) {
            return Void.class;
        }

        // 如果这个对象是通过类继承，并且携带了泛型
        return actualTypeArguments[0];
    }

    /**
     * 获取进度百分比
     */
    public static int getProgressProgress(long totalByte, long currentByte) {
        if (totalByte <= 0) {
            // 返回 -1 表示无法获取进度
            return -1;
        }
        // 计算百分比，这里踩了两个坑
        // 当文件很大的时候：字节数 * 100 会超过 int 最大值，计算结果会变成负数
        // 还有需要注意的是，long 除以 long 等于 long，这里的字节数除以总字节数应该要 double 类型的
        return (int) (((double) currentByte / totalByte) * 100);
    }

    /**
     * 字符串编码
     */
    public static String encodeString(String text) {
        if (isEmpty(text)) {
            return "";
        }
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text;
        }
    }

    /**
     * 格式化 Json 字符串
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    public static String formatJson(String json) {
        if (json == null) {
            return "";
        }

        try {
            if (json.startsWith("{")) {
                return unescapeJson(new JSONObject(json).toString(4));
            } else if (json.startsWith("[")) {
                return unescapeJson(new JSONArray(json).toString(4));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 去除 Json 中非必要的字符转义
     */
    public static String unescapeJson(String json) {
        if (isEmpty(json)) {
            return "";
        }
        // https://github.com/getActivity/EasyHttp/issues/67
        return json.replace("\\/", "/");
    }

    /**
     * 获取对象的唯一标记
     */
    public static String getObjectTag(Object object) {
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }

    /**
     * 创建文件夹
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createFolder(File targetFolder) {
        if (targetFolder.exists()) {
            if (targetFolder.isDirectory()) {
                return;
            }
            targetFolder.delete();
        }
        targetFolder.mkdirs();
    }

    /**
     * 获取文件的 md5
     */
    public static String getFileMd5(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }
        DigestInputStream digestInputStream = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            digestInputStream = new DigestInputStream(inputStream, messageDigest);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(digestInputStream.read(buffer) > 0)) {
                    break;
                }
            }
            messageDigest = digestInputStream.getMessageDigest();
            byte[] md5 = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : md5) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            EasyUtils.closeStream(inputStream);
            EasyUtils.closeStream(digestInputStream);
        }
        return null;
    }

    /**
     * 打开文件的输入流
     */
    public static InputStream openFileInputStream(File file) throws IOException {
    if (file == null) {
                throw new FileNotFoundException("File is null");
            }
        return Files.newInputStream(file.toPath());
    }

    /**
     * 打开文件的输出流
     */
    public static OutputStream openFileOutputStream(File file) throws IOException {
        if (file == null) {
            throw new FileNotFoundException("File is null");
        }
        return Files.newOutputStream(file.toPath());
    }

    /**
     * 判断一个字段是否是常量字段
     */
    public static boolean isConstantField(Field field) {
        int modifiers = field.getModifiers();
        // 如果这是一个常量字段，则直接忽略掉，例如 Parcelable 接口中的 CREATOR 字段
        // https://github.com/getActivity/EasyHttp/issues/112
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    /**
     * 获取类的所有字段
     */
    public static List<Field> getAllFields(Class<?> originalClass) {
        List<Field> fields = CLASS_LIST_LRU_CACHE.get(originalClass);
        if (fields != null) {
            return fields;
        }

        fields = new ArrayList<>();
        Class<?> clazz = originalClass;
        do {
            Field[] declaredFields = clazz.getDeclaredFields();
            fields.addAll(0, Arrays.asList(declaredFields));
            // 遍历获取父类的字段
            clazz = clazz.getSuperclass();
        } while (clazz != null && !Object.class.equals(clazz));

        CLASS_LIST_LRU_CACHE.put(originalClass, fields);

        return fields;
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable() {
        try {
            InetAddress addr = InetAddress.getByName("www.baidu.com");
            boolean state = addr.isReachable(3000);

            // 获取本机IP地址
            InetAddress localAddr = InetAddress.getLocalHost();
            boolean state1 = localAddr.getHostName().equals("127.0.0.1");
            return state || state1;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 字符串判空
     */
    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    /**
     * 打印 tag 日志
     */
    public static void log(String tag, String message) {
        System.out.println(tag + "：" + message);
    }

}