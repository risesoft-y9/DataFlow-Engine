package net.risedata.rpc.consumer.factory;

import net.risedata.rpc.consumer.result.value.ReturnValueHandle;
import net.risedata.rpc.consumer.result.value.impl.*;
import net.risedata.rpc.factory.model.ReturnType;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 返回值处理工厂
 * @Author lb176
 * @Date 2021/8/4==15:12
 */
public class ReturnValueHandleFactory {

    private static final List<ReturnValueHandle> RETURN_VALUE_HANDLES = new ArrayList<>();

    static {
        RETURN_VALUE_HANDLES.add(new ListHandle());
        RETURN_VALUE_HANDLES.add(new SyncResultHandle());
        RETURN_VALUE_HANDLES.add(new SyncGenericityResultHandle());
        RETURN_VALUE_HANDLES.add(new SyncListGenericityResultHandle());
        RETURN_VALUE_HANDLES.add(new DefaulGenericityHandle());
        RETURN_VALUE_HANDLES.add(new DefaulClasstHandle());
    }


    public static ReturnValueHandle getInstance(ReturnType returnType) {
        for (ReturnValueHandle re :
                RETURN_VALUE_HANDLES) {
            if (re.isHandle(returnType)) {

                return re;
            }
        }
        return null;
    }
}
