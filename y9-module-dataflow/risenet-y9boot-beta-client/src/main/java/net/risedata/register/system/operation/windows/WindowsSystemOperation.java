package net.risedata.register.system.operation.windows;

import java.util.Arrays;

import net.risedata.register.system.System;
import net.risedata.register.system.operation.SystemOperation;
import net.risedata.register.system.operation.linux.LinuxSystemOperation;

/**
 * @Description : windows系统操作系统
 * @ClassName WindowsSystemOperation
 * @Author lb
 * @Date 2021/12/22 15:31
 * @Version 1.0
 */
public class WindowsSystemOperation implements SystemOperation {


    @Override
    public String nohupRun(String sourceOperation) {
      return "start /b "+sourceOperation;
    }

    @Override
    public void execute(boolean isNewThread,String... sourceOperation) {

        System.executeAndClose(Arrays.asList(sourceOperation));
    }

    @Override
    public String getSeparate() {
        return "\\";
    }
}
