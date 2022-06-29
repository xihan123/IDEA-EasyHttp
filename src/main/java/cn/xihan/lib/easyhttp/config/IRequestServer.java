package cn.xihan.lib.easyhttp.config;



import cn.xihan.lib.easyhttp.model.BodyType;


/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/05/19
 *    desc   : 请求服务配置
 */
public interface IRequestServer extends
        IRequestHost, IRequestClient,
        IRequestType {


    @Override
    default BodyType getBodyType() {
        // 默认以表单的方式提交
        return BodyType.FORM;
    }
}