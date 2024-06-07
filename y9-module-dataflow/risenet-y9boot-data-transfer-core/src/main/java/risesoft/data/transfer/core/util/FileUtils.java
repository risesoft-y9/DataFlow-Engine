package risesoft.data.transfer.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description :
 * @ClassName FileUtils
 * @Author lb
 * @Date 2023/6/29 9:36
 * @Version 1.0
 */
public class FileUtils {

    public static List<File> getChildrens(File rootFile) {
        List<File> res = new ArrayList<>();
        getChildren(rootFile, res);
        return res;
    }

    private static void getChildren(File rootFile, List<File> files) {
        for (File file : rootFile.listFiles()) {
            if (!file.isDirectory()) {
                files.add(file);
            } else {
                getChildren(file, files);
            }
        }
    }






}
