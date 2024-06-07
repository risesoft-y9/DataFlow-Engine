package net.risedata.register.api.system;

import cn.hutool.core.io.FileUtil;
import net.risedata.register.model.FileModel;
import net.risedata.rpc.consumer.annotation.Listener;
import net.risedata.rpc.consumer.annotation.Listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description : 文件更新
 * @ClassName FileOperation
 * @Author lb
 * @Date 2023/2/6 16:22
 * @Version 1.0
 */
@Listeners
public class FileOperation {

    @Listener("getRootPath")
    public String getRootPath() {
        return FileOperation.class.getResource("/").getPath();
    }

    @Listener("downFile")
    public Byte[] downFile(String filePath) {

        return toObjects(FileUtil.readBytes(filePath));
    }

    Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];

        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; // Autoboxing

        return bytes;
    }
    /**
     * 获取底下文件根据rootUrl
     *
     * @param rootUrl
     * @return
     */
    @Listener("childrenListFile")
    public List<FileModel> childrenListFile(String rootUrl) {
        File f = new File(rootUrl);
        List<FileModel> res = new ArrayList<>();
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File file : files) {
                res.add(new FileModel(file.getName(), file.getAbsolutePath(), file.isDirectory(), file.lastModified(), file.length()));
            }
        } else {
            throw new RuntimeException("不是文件夹");
        }
        return res;
    }

    @Listener("deleteFile")
    public boolean deleteFile(String url) {
        return new File(url).delete();
    }


    @Listener("replaceFile")
    public boolean replaceFile(String url, byte[] data) throws Exception {
        FileUtil.writeBytes(data, url);
        return true;
    }

    /**
     * 根据文件名查找文件
     *
     * @param name
     * @return
     */
    @Listener("searchFile")
    public List<FileModel> searchFile(String name, String rootPath) {
        List<FileModel> res = new ArrayList<>();
        searchFile(new File(rootPath), name, res);
        return res;
    }

    private void searchFile(File file, String name, List<FileModel> res) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                searchFile(listFile, name, res);
            }
        } else {
            if (file.getName().equals(name)) {
                res.add(new FileModel(file.getName(), file.getAbsolutePath(), file.isDirectory(), file.lastModified(), file.length()));
            }
        }
    }
}
