package net.risedata.register.system;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 系统信息
 */
public class System{
    /**
     * 服务器名称
     */
    private String computerName;

    /**
     * 服务器Ip
     */
    private String computerIp;

    /**
     * 项目路径
     */
    private String userDir;

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 系统架构
     */
    private String osArch;

    public String getComputerName()
    {
        return computerName;
    }

    public void setComputerName(String computerName)
    {
        this.computerName = computerName;
    }

    public String getComputerIp()
    {
        return computerIp;
    }

    public void setComputerIp(String computerIp)
    {
        this.computerIp = computerIp;
    }

    public String getUserDir()
    {
        return userDir;
    }

    public void setUserDir(String userDir)
    {
        this.userDir = userDir;
    }

    public String getOsName()
    {
        return osName;
    }

    public void setOsName(String osName)
    {
        this.osName = osName;
    }

    public String getOsArch()
    {
        return osArch;
    }

    public void setOsArch(String osArch)
    {
        this.osArch = osArch;
    }

    public static List<String> executeAndClose(List<String> cmds) {
        java.lang.System.out.println(cmds);
        boolean isWindows = java.lang.System.getProperty("os.name").toLowerCase().startsWith("windows");
        List<String> result = new LinkedList<>();
        try {
            Process process = null;
            if (isWindows) {

                process = Runtime.getRuntime().exec("cmd");
            } else {
                process = Runtime.getRuntime().exec("sh");
            }
            SequenceInputStream sis = new SequenceInputStream(process.getInputStream(), process.getErrorStream());
            InputStreamReader isr = new InputStreamReader(sis, "gbk");
            BufferedReader br = new BufferedReader(isr);
            OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);
            for (int i = 0; i < cmds.size(); i++) {
                java.lang.System.out.println("执行命令:"+cmds.get(i));
                bw.write(cmds.get(i));
                bw.newLine();
            }
            bw.flush();
            bw.close();
            osw.close();
            String line = null;
            int i = 0;
            while (null != (line = br.readLine())) {
                result.add(line);
                i++;
                if (i == 40) {
                    break;
                }

            }
          /*  process.destroy();
            br.close();
            isr.close();
            process.destroy();*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
