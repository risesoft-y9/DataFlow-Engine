package net.risedata.register.system.operation.linux;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;

import net.risedata.register.model.FileModel;
import net.risedata.register.system.System;
import net.risedata.register.system.operation.SystemOperation;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @Description : linux 系统执行指令
 * @ClassName LinuxSystemOperation
 * @Author lb
 * @Date 2021/12/22 15:30
 * @Version 1.0
 */
public class LinuxSystemOperation implements SystemOperation {


    private static final String startFile ="/tmp/beta_temp/start.sh";
    @Override
    public String nohupRun(String sourceOperation) {
        return "nohup " + sourceOperation + " &";
    }
    @Override
    public void execute(boolean isNewThread, String... sourceOperation) {
        if (isNewThread) {

            Thread thread = new Thread(() -> {
                System.executeAndClose(Arrays.asList("chmod u+x "+startFile,"nohup "+startFile));
            });
            thread.setDaemon(false);
            thread.start();

            //生成shell 脚本
            StringBuilder codeStr = new StringBuilder();
            for (String s : sourceOperation) {
                codeStr.append(s + "\n");
            }
            FileUtil.writeString("echo start\n" +
                    "  \n" +
                    "sleep 3s\n" +
                    codeStr +
                    "echo end\n" +
                    "rm -rf "+startFile, new File(startFile), Charset.forName("UTF-8"));


        } else {

            System.executeAndClose(Arrays.asList(sourceOperation));
        }
    }

    @Override
    public String getSeparate() {
        return "/";
    }


}
