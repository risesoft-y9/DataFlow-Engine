package net.risedata.rpc.provide.listener;

/**
* @description: 返回值异步操作接口不同的是这个需要解析返回值
* @Author lb176
* @Date 2021/7/30==9:41
*/
public interface SyncParseResult extends SyncResult{
    /**
     * 当返回数据的时候调用
     * @param res 字符串
     */
   void success(String res);

    /**
     * 调用失败后执行的方法
     * @param e 异常
     */
   void error(Throwable e);


}
