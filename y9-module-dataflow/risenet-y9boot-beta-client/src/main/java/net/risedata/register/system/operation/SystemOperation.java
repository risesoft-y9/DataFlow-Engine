package net.risedata.register.system.operation;

/**
 * @Description : 提供系统操作的接口
 * @ClassName SystemOperation
 * @Author lb
 * @Date 2021/12/22 15:17
 * @Version 1.0
 */
public interface SystemOperation {

    /**
     * 后台启动指令
     * @param sourceOperation 核心指令
     */
    String nohupRun(String sourceOperation);

    /**
     * 执行指令集
     * @param sourceOperation
     */
    void execute(boolean isNewThread,String... sourceOperation);

    /**
     * 拿到当前系统分隔符
     * @return
     */
    String  getSeparate();
}
