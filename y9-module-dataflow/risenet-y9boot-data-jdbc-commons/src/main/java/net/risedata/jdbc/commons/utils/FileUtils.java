package net.risedata.jdbc.commons.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
	public static final String [] CODES = new  String[] {".java",".vue","html"};
	@SuppressWarnings("unused")
	private static int getCodeLine(File file,String[] codes) throws IOException {
		int i = 0;
		if (!file.isDirectory()) {
			boolean flag = true;
			for (int j = 0; j < codes.length; j++) {
				if (file.getName().endsWith(codes[j])) {
					flag = false;
				}
			}
			if (flag) {
				return i;
			}
			@SuppressWarnings("resource")
			BufferedReader bi = new BufferedReader( new FileReader(file));
			while (bi.readLine() != null) {
				i++;
			}
		}else {
			File[] child = file.listFiles();
			for (File file2 : child) {
				i+=getCodeLine(file2,codes);
			}
		}
		return i;
	}
	
	/**
	 * 下载zip压缩包文件会在fileName 后面添加.zip
	 * @param fileName
	 * @param os
	 * @param files
	 * @throws Exception 
	 */
	public static ZipOutputStream downZip(OutputStream os,byte[][] files,String[] fileNames) throws Exception {
	 	ZipOutputStream zos = new ZipOutputStream(os);	
   	 	ZipEntry entry  = null;
       	try {
       		for (int i = 0; i < fileNames.length; i++) {
   
   		  	   entry = new ZipEntry(fileNames[i]);
   		  	   zos.putNextEntry(entry);
   		  	   zos.write(files[i]);   
   		    }
   		} catch (Exception e) {
   			throw e;
   		}finally {
   			if(entry != null) {
   				try {
   					zos.closeEntry();
   				} catch (Exception e2) {
   					throw e2;
   				}
   			}		   			
   		}
       	return zos;
	}
	   
   
}
