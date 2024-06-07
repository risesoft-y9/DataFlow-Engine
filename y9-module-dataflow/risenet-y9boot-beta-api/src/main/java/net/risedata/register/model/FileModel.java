package net.risedata.register.model;

/**
 * @Description : 文件操作model
 * @ClassName FileModel
 * @Author lb
 * @Date 2023/2/6 16:25
 * @Version 1.0
 */
public class FileModel {
    //name
    private String name;

    // path
    private String path;
    //是否为文件夹
    private boolean isDirectory;
    /**
     * 最后修改时间
     */
    private long lastModified;

    private long size;
    public FileModel(String name, String path, boolean isDirectory, long lastModified,long size) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.isDirectory = isDirectory;
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", isDirectory='" + isDirectory + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }
}
