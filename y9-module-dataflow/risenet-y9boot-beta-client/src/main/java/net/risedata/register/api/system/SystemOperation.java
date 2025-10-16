package net.risedata.register.api.system;

import cn.hutool.core.date.DateUtil;
import net.risedata.register.api.filter.listener.CountListener;
import net.risedata.register.config.ListenerConfigs;
import net.risedata.register.config.SystemConfig;
import net.risedata.register.config.SystemConfigSelector;
import net.risedata.register.container.ContainerManager;
import net.risedata.register.system.Server;
import net.risedata.rpc.consumer.annotation.Listener;
import net.risedata.rpc.consumer.annotation.Listeners;
import net.risedata.rpc.consumer.config.ConsumerApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.*;

import java.io.*;
import java.util.*;

/**
 * @Description :
 * @ClassName SystemOperation
 * @Author lb
 * @Date 2022/7/1 10:58
 * @Version 1.0
 */
@Listeners
public class SystemOperation {

    @Autowired
    CountListener countListener;

    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;
    
    @Autowired
    ApplicationArguments arg;
    
    private Class<?> rootClass;
    
    @Autowired
    ApplicationContext ac;
    
    /**
     * 标记了状态
     */
    private int status;

    private Server server;

    public static final int START = 0;
    public static final int SHUT_TER = 1;
    public static final int STARTING = 3;
    public static final int CLOSE = 2;

    @Autowired(required = false)
    ServletWebServerFactory servletWebServerFactory;

    @Autowired(required = false)
    ContainerManager containerManager;

    // 使用重启容器操作 当没有实现哪个ServletWebServerFactory 这个bean 的时候触发
    @Listener(ListenerConfigs.RE_START)
    public int reStart() {
        System.out.println("重启"+status);
        System.out.println("重启程序");
        if (status != START) {
            return status;
        }
        status = SHUT_TER;
        countListener.add(() -> {
            Map<String, Object> boot = ac.getBeansWithAnnotation(SpringBootApplication.class);
            Collection<Object> values = boot.values();
            for (Object o : values) {
                Class<?> tempClass = o.getClass();
                String name = tempClass.getName();
                while (name.indexOf("$") != -1) {
                    tempClass = tempClass.getSuperclass();
                    name = tempClass.getName();
                }
                if (ConsumerApplication.LOGGER.isInfoEnabled()) {
                    ConsumerApplication.LOGGER.info("boot class " + tempClass);
                }
                this.rootClass = tempClass;
            }
            SystemConfigSelector.rootClass = this.rootClass;
            ConsumerApplication.LOGGER.info("restart ----");

            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                if (servletWebServerFactory == null) {
                    System.out.println("容器对象为空 走查找-- ");
                    if (containerManager!=null){
                        System.out.println("自定义--"+containerManager.getClass());
                        containerManager.reStart();
                    }else{
                        SystemConfig system = new SystemConfig();
                        system.containerManager(system.createSystemOperation()).reStart();
                    }
                    return;
                }
                ConsumerApplication.LOGGER.info("close ");
                try {
                    configurableApplicationContext.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                status = CLOSE;
                ConsumerApplication.LOGGER.info("closed ");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }

                status = STARTING;
                ConsumerApplication.LOGGER.info("start for boot " + rootClass);
                //   SpringApplication.
                configurableApplicationContext = SpringApplication.run(rootClass, arg.getSourceArgs());
                status = START;
            });

            thread.setDaemon(false);
            thread.start();
        });
        return status;
    }

    /**
     * 获取当前http 请求数量
     *
     * @return
     */
    @Listener(ListenerConfigs.GET_HTTP_COUNT)
    public int getCount() {
        return countListener.getCount();
    }

    /**
     * 获取系统状态
     *
     * @return status = 200 成功/500失败    ,menu 内存,cpu:
     */
    @Listener(ListenerConfigs.GET_SYSTEM_STATUS)
    public synchronized Map<String, Object> getSystemStatus() {
        Map<String, Object> map = new HashMap<>();
        try {
            if (server == null) {
                server = new Server();
                server.serverInfo();
            }
            server.refreshCpu();
            server.refreshMem();
            server.refreshDisks();
            map.put("cpu", server.getCpu());
            map.put("mem", server.getMem());
            map.put("disks", server.getDisks());
            map.put("count", countListener.getCount());
            map.put("status", 200);
        } catch (Exception e) {
            map.put("status", 500);
            map.put("data", e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 读取文件
     * maxDate or maxName 是必备的
     *
     * @param file       文件匹配 文件匹配格式为maxDate:/logs/{yyyy-MM-dd}/ 可以使用时间格式化 maxName:/logs/{yyyy-MM-dd}/
     * @param startIndex 开始索引
     * @param readSize   读取大小
     * @return
     */
    @Listener(ListenerConfigs.GET_READ_FILE)
    public Map<String, Object> readFile(String file, Long startIndex, Integer readSize) {
        Map<String, Object> map = new HashMap<>();
        try {
            int index = file.indexOf("{");
            int endIndex = file.indexOf("}");
            while (index != -1 && endIndex != -1 && endIndex > index) {
                String temp = file.substring(index + 1, endIndex);
                String value = DateUtil.format(new Date(), temp);
                file = file.replace("{" + temp + "}", value);
                index = file.indexOf("{");
                endIndex = file.indexOf("}");
            }
            if (file.startsWith("maxDate:")) {
                file = file.substring(8);
                file = getMaxDateFile(file);
            } else if (file.startsWith("maxName:")) {
                file = file.substring(8);
                file = getMaxDateFile(file);
            }

            map.put("status", 200);
            map.put("data", readFileStr(file, startIndex, readSize));
        } catch (Exception e) {
            map.put("status", 500);
            map.put("data", e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取最新更新时间的文件
     *
     * @param file 上册文件夹或者文件如果是文件则直接返回
     * @return
     */
    private static String getMaxDateFile(String file) {
        File f = new File(file);
        if (f.isDirectory()) {
            List<File> files = new ArrayList<>();
            getFiles(f, files);
            f = files.get(0);
            for (File file1 : files) {
                if (file1.lastModified() > f.lastModified()) {
                    f = file1;
                }
            }
            return f.getAbsolutePath();
        }
        return f.getAbsolutePath();
    }

    /**
     * 拿到一个目录下所有的文件
     *
     * @param f
     * @param files
     */
    private static void getFiles(File f, List<File> files) {
        if (f.isDirectory()) {
            File[] files2 = f.listFiles();
            for (File file : files2) {
                getFiles(file, files);
            }
        } else {
            files.add(f);
        }
    }

    /**
     * 读取文件
     *
     * @param file       文件
     * @param startIndex 起始位置-1则代表更具readSize 来
     * @param readSize   读取多少
     * @return
     * @throws Exception
     */
    private static Map<String, Object> readFileStr(String file, long startIndex, int readSize) {
        Map<String, Object> res = new HashMap<>();
        RandomAccessFile randomAccessFile = null;
        try {
			randomAccessFile = new RandomAccessFile(file, "r");
			long maxSize = randomAccessFile.length();
			if (maxSize <= startIndex) {
			    res.put("data", "");
			    res.put("nextIndex", maxSize);
			    return res;
			}
			if (readSize == -1) {
			    readSize = (int) maxSize;
			}
			if (startIndex == -1) {
			    startIndex = randomAccessFile.getChannel().size() - readSize;
			    if (startIndex < 0) {
			        startIndex = 0;
			    }
			}

			if (startIndex + readSize > maxSize) {
			    res.put("nextIndex", maxSize);
			    readSize = (int) (maxSize - startIndex);
			} else {
			    res.put("nextIndex", startIndex + readSize);
			}

			randomAccessFile.seek(startIndex);
			byte[] bytes = new byte[readSize];
			res.put("data", new String(bytes, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return res;
    }

}