package cn.xihan.lib.easyhttp.model

import cn.xihan.lib.easyhttp.model.HttpListData.ListBean

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/10/07
 * desc   : 统一接口列表数据结构
 */
class HttpListData<T> : HttpData<ListBean<T>?>() {
    class ListBean<T> {
        /** 当前页码  */
        val pageIndex = 0

        /** 页大小  */
        val pageSize = 0

        /** 总数量  */
        val totalNumber = 0

        /** 数据  */
        val items: List<T>? = null

        /**
         * 判断是否是最后一页
         */
        val isLastPage: Boolean
            get() = Math.ceil((totalNumber.toFloat() / pageSize).toDouble()) <= pageIndex
    }
}