package net.risedata.jdbc.commons.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.*;

public class ImageZipUtil {

	/**
	 * 采用指定宽度、高度或压缩比例 的方式对图片进行压缩
	 *
	 * @param: [imgSrc, imgOut, width, height, rate] // rate:为空时按指定大小压缩，不为空时按原尺寸压缩
	 * @return: void
	 **/
	public static boolean reduceImg(File srcfile, String imgOut, int width, int height, Float rate) {
		try {
			// 如果rate不为空说明是按比例压缩
			if (rate != null && rate > 0) {
				// 获取文件高度和宽度
				int[] results = getImgWidth(srcfile);
				if (results == null || results[0] == 0 || results[1] == 0) {
					return false;
				} else {
					width = (int) (results[0] * rate);
					height = (int) (results[1] * rate);
				}
			}
			// 开始读取文件并进行压缩
			Image src = ImageIO.read(srcfile);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(src.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
			// FileOutputStream out = new FileOutputStream(imgOut);
			String formatName = imgOut.substring(imgOut.lastIndexOf(".") + 1);
			ImageIO.write(tag, formatName, new File(imgOut));
			// JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			// encoder.encode(tag);
			// out.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取图片的长宽
	 *
	 * @param: [file]
	 * @return: int[]
	 **/
	public static int[] getImgWidth(File file) {
		int[] result = { 0, 0 };
		try {
			FileInputStream is = new FileInputStream(file);
			BufferedImage src = ImageIO.read(is);
			// 得到源图宽
			result[0] = src.getWidth(null);
			// 得到源图高
			result[1] = src.getHeight(null);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}