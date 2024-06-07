package net.risedata.jdbc.commons.utils;
import java.io.File;

public class util {
		public static void main(String[] args) {
			String url="E:\\PS\\肖战王一博去水印\\去水印之后";
			int startNum=1;
			util.sort(startNum, url);
	  }
	/**
	 * 用于将文件名按照指定数字往后排
	 * @param startNum
	 * @param url
	 */
	public static void sort(int startNum,String url) {
		 File file = new File(url);
		 File[] list = file.listFiles();
		 String newName=null;
		// 如果目录下文件存在
	        if (file.exists() && file.isDirectory())
	        {
	            for (int i = 0; i < list.length; i++)
	            {
	                //取文件名子存入name中
	                String name = list[i].getName();
	                int lo=name.indexOf(".");
	               String lastName=name.substring(lo,name.length());
	               String forNeName=String.valueOf(startNum);
	               startNum++;
	                //重命名
	               newName=forNeName+lastName;
	                File dest = new File(url + "/" + newName);
	                list[i].renameTo(dest);
	                System.out.println(dest.getName());
	            }
	        }
	}
	
}
