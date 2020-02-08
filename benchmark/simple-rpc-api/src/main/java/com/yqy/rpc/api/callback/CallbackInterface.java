package com.yqy.rpc.api.callback;


/**
 * @ClassName: CallbackInterface
 * @Description: 回调接口
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/

public interface CallbackInterface {
    /**
     * 回调方法
     * @param result rpc返回请求的结果
     */
    void getInfoFromClient(String result);
}
