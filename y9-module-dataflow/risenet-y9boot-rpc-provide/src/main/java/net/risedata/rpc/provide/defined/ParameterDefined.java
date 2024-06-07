package net.risedata.rpc.provide.defined;


import net.risedata.rpc.utils.LParameter;

/**
 * @description: 参数处理
 * @Author lb176
 * @Date 2021/4/29==16:30
 */
public interface ParameterDefined {
    /**
     * 没有通过检测返回的信息
     *
     * @param index            参数的下标
     * @param value            参数
     * @param parameterDefined 参数的描述
     * @return 返回信息
     */
    String noPassMsg(int index, Object value, ParameterDefined parameterDefined);

    /**
     * 拿到参数定义对象
     * @return
     */
    LParameter getParameter();

    /**
     * 检验该参数是否通过 参数类型为 getType 的类型
     * 注意此方法比getDefaultValue 先执行
     *
     * @param value 参数
     * @return 返回 true 则代表通过 false
     */
    boolean pass(Object value);

    /**
     * 返回此参数的类型
     *
     * @return
     */
    Class<?> getType();

    /**
     * 返回默认值
     *
     * @return 返回默认值 如果type 是对象则返回json String
     */
    <T> T getDefaultValue(Class<T> returnType);


    String getName();
}
