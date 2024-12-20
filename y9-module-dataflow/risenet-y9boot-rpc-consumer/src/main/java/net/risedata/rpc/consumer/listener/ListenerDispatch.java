package net.risedata.rpc.consumer.listener;

import net.risedata.rpc.consumer.core.impl.ChannelConnection;
import net.risedata.rpc.consumer.exceptions.ListenerException;
import net.risedata.rpc.consumer.model.ListenerRequest;
import net.risedata.rpc.model.ListenerResponse;
import net.risedata.rpc.utils.LParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description : 监听器的执行
 * @ClassName ListenerDispatch
 * @Author lb
 * @Date 2021/11/24 11:27
 * @Version 1.0
 */
public class ListenerDispatch {

    public static Object doDispatch(ListenerRequest request) {

        List<ListenerModel> listenerModels = ListenerApplication.LISTENER_MODELS.get(request.getMapping());

        if (listenerModels != null) {
            List<Object> res = new ArrayList<>();
            for (ListenerModel listenerModel : listenerModels) {
                if (listenerModel.getArgs() != null && listenerModel.getArgs().length > 0) {
                    LParameter[] parameter = listenerModel.getArgs();
                    Object[] args = new Object[parameter.length];
                    for (int i = 0; i < parameter.length; i++) {
                        args[i] = request.getArgs().getObject(parameter[i].getParameterName(), parameter[i].getParameterType());
                    }
                    try {
                        res.add(listenerModel.getMethod().invoke(listenerModel.getInstance(), args));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new ListenerException("invoke listener error " + e.getCause().getMessage());
                    }
                } else {
                    try {
                        res.add(listenerModel.getMethod().invoke(listenerModel.getInstance()));
                    } catch (Exception e) {
                        e.getCause().printStackTrace();

                        e.printStackTrace();
                        throw new ListenerException("invoke listener error " + e.getCause().getMessage());
                    }
                }
            }
            return res;
        }
        throw new ListenerException("未找到" + request.getMapping());
    }

    public static void backError(ListenerRequest request , ListenerResponse response, ChannelConnection channelConnection){
        for (ListenerBack back : ListenerApplication.LISTENER_BACKS) {
            back.onBackError(request,response,channelConnection);
        }
    }
}
