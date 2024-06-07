package risesoft.data.transfer.stream.es.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 通过将中文字符串的每个字符转成十六进制，
 * 然后用逗号连起来
 * 解决传参过程中出现的中文乱码问题的类
 * 缺点是转换后的字符串长度会比原来长很多
 * 最多是原来的4.8倍长。
 * 此类无视各种编码环境。
 * 可使用Base64加密，加密后长度只有原来的4/3
 * @author xieliuping
 *
 */
public class ChineseMessyCodeSolution {
	private static String charsetOfToBase64="UTF-8";

	public static void main(String[] args) throws UnsupportedEncodingException {
		String str1="/doc/{AC1D3315-0000-0000-68A2-68B500003CD7}1document.doc";
		String result=null;
//		result=strToHex(str1);
//		System.out.println(result);
//		System.out.println(result.matches("^[0-9a-zA-Z]+(,[0-9a-zA-Z]+)*$"));
//		System.out.println(result.length());
		
		result=strToBase64(str1);
		System.out.println(result);
		result=base64ToStr(result);
		System.out.println(result);
		
	}
	/**
	 * 将字符串转成HexString形的字符串（用“，”分隔）
	 * 以便于传递时不会产生乱码（主要针对中文）
	 * @param source
	 * @param reduction 用于优化产出的字符串的长度
	 * @return
	 */
	public static String strToHex(String source){
		if(source==null||"".equals(source))return source;
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<source.length();i++){
			sb.append(Integer.toHexString(source.codePointAt(i))+",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	/**
	 * 将HexString形的字符串转回原字符串
	 * 可以解决中文乱码的问题
	 * @param UCP
	 * @return
	 */
	public static String hexToStr(String UCP){
		if(UCP==null||"".equals(UCP))return UCP;
		String[] arr=UCP.split(",");
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<arr.length;i++){
			sb.append((char)(Integer.parseInt(arr[i], 16)));
		}
		return sb.toString();
	}
	public static String codePointArrayToStr(int[] arr){
		if(arr==null||arr.length==0)return null;
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<arr.length;i++){
			sb.append((char)arr[i]);
		}
		return sb.toString();
	}
	/*JS版本代码
	 * function strToHex(str){
		if(!str)return null;
		str=new String(str);
		var arr=[];
		for(var i=0;i<str.length;i++){
			arr[i]=str.charCodeAt(i).toString(16);
		}
		return arr.toString();
	}
	function hexToStr(str){
		if(str==undefined||str==null)return null;
		var arr=new String(str).split(',');
		var result='';
		for(var i=0;i<arr.length;i++){
			result+=String.fromCharCode(parseInt(arr[i],16));
		}
		return result;
	}*/
	
	
	//=======================================below there is Base64=========================================
	/**
	 * 将含有字符串用Base64加密
	 * （解决中文乱码问题推荐）
	 * @param source
	 * @return
	 */
	public static String strToBase64(String str){
		if(str==null||str.isEmpty())return str;
		try {
			str=new String(
					Base64.getEncoder().encode(str.getBytes(charsetOfToBase64)),
					charsetOfToBase64);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 将用Base64加密的字符串解密
	 * （解决中文乱码问题推荐）
	 * @param source
	 * @return
	 */
	public static String base64ToStr(String source){
		if(source==null||source.isEmpty())return source;
		try {
			source=new String(
					Base64.getDecoder().decode(source.getBytes(charsetOfToBase64)),
					charsetOfToBase64);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return source;
	}
	/*Base64 JS版本
	<script type="text/javascript" src="base64.js"></script>
	<script type="text/javascript">
	  var b = new Base64();
	  var str = b.encode("admin:admin");
	  alert("base64 encode:" + str);
	  //解密
	  str = b.decode(str);
	  alert("base64 decode:" + str);
	</script>
	 */
	
	/*base64.js文件源码
	 * function Base64() {
	 // private property
	 _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	 // public method for encoding
	 this.encode = function (input) {
	  var output = "";
	  var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
	  var i = 0;
	  input = _utf8_encode(input);
	  while (i < input.length) {
	   chr1 = input.charCodeAt(i++);
	   chr2 = input.charCodeAt(i++);
	   chr3 = input.charCodeAt(i++);
	   enc1 = chr1 >> 2;
	   enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
	   enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
	   enc4 = chr3 & 63;
	   if (isNaN(chr2)) {
	    enc3 = enc4 = 64;
	   } else if (isNaN(chr3)) {
	    enc4 = 64;
	   }
	   output = output +
	   _keyStr.charAt(enc1) + _keyStr.charAt(enc2) +
	   _keyStr.charAt(enc3) + _keyStr.charAt(enc4);
	  }
	  return output;
	 }
	 // public method for decoding
	 this.decode = function (input) {
	  var output = "";
	  var chr1, chr2, chr3;
	  var enc1, enc2, enc3, enc4;
	  var i = 0;
	  input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
	  while (i < input.length) {
	   enc1 = _keyStr.indexOf(input.charAt(i++));
	   enc2 = _keyStr.indexOf(input.charAt(i++));
	   enc3 = _keyStr.indexOf(input.charAt(i++));
	   enc4 = _keyStr.indexOf(input.charAt(i++));
	   chr1 = (enc1 << 2) | (enc2 >> 4);
	   chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
	   chr3 = ((enc3 & 3) << 6) | enc4;
	   output = output + String.fromCharCode(chr1);
	   if (enc3 != 64) {
	    output = output + String.fromCharCode(chr2);
	   }
	   if (enc4 != 64) {
	    output = output + String.fromCharCode(chr3);
	   }
	  }
	  output = _utf8_decode(output);
	  return output;
	 }
	 // private method for UTF-8 encoding
	 _utf8_encode = function (string) {
	  string = string.replace(/\r\n/g,"\n");
	  var utftext = "";
	  for (var n = 0; n < string.length; n++) {
	   var c = string.charCodeAt(n);
	   if (c < 128) {
	    utftext += String.fromCharCode(c);
	   } else if((c > 127) && (c < 2048)) {
	    utftext += String.fromCharCode((c >> 6) | 192);
	    utftext += String.fromCharCode((c & 63) | 128);
	   } else {
	    utftext += String.fromCharCode((c >> 12) | 224);
	    utftext += String.fromCharCode(((c >> 6) & 63) | 128);
	    utftext += String.fromCharCode((c & 63) | 128);
	   }
	  }
	  return utftext;
	 }
	 // private method for UTF-8 decoding
	 _utf8_decode = function (utftext) {
	  var string = "";
	  var i = 0;
	  var c = c1 = c2 = 0;
	  while ( i < utftext.length ) {
	   c = utftext.charCodeAt(i);
	   if (c < 128) {
	    string += String.fromCharCode(c);
	    i++;
	   } else if((c > 191) && (c < 224)) {
	    c2 = utftext.charCodeAt(i+1);
	    string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
	    i += 2;
	   } else {
	    c2 = utftext.charCodeAt(i+1);
	    c3 = utftext.charCodeAt(i+2);
	    string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
	    i += 3;
	   }
	  }
	  return string;
	 }
	}*/
	
	//===================================以下为unicode和中文的互转方法========================================
	/**
	 * unicode形式的中文转中文
	 * <p>如：\u4e2d\u6587==中文</p>
	 * @param sourceFilePath
	 * @param destFilePath
	 * @throws IOException
	 */
	public static void unicodeToChinese(String sourceFilePath,String destFilePath) throws IOException{
		File file=new File(sourceFilePath);
		File file2=new File(destFilePath);
		FileReader fr=new FileReader(file);
		BufferedReader br=new BufferedReader(fr);
		FileWriter fw=new FileWriter(file2);
		BufferedWriter bw=new BufferedWriter(fw);
		String line=null;
		Pattern p=Pattern.compile("\\\\u[0-9aA-fF]{4}");
		try {
			while((line=br.readLine())!=null){
				line=unicodeToChinese(line, p);
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				if(bw!=null)bw.close();
				if(fw!=null)fw.close();
				if(br!=null)br.close();
				if(fr!=null)fr.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}
	/**
	 * unicode形式的中文转中文
	 * <p>如：\u4e2d\u6587==中文</p>
	 * @param str
	 * @return
	 */
	public static String unicodeToChinese(String unicodeStr){
		Pattern p=Pattern.compile("\\\\u[0-9aA-fF]{4}");
		return unicodeToChinese(unicodeStr, p);
	}
	private static String unicodeToChinese(String unicodeStr,Pattern p){
		
		StringBuffer sb=new StringBuffer();
		Matcher m=p.matcher(unicodeStr);
		while(m.find()){
			char letter = (char) Integer.parseInt(m.group().substring(2), 16); // 16进制parse整形字符串。     
			m.appendReplacement(sb, new Character(letter).toString());
		}
		m.appendTail(sb);
		return sb.toString();
	}
	/**
	 * 中文转unicode
	 * <p>中文==\u4e2d\u6587</p>
	 * @param chineseStr
	 * @return
	 */
    public static String chineseToUnicode(String chineseStr) {
    	if(chineseStr==null||"".equals(chineseStr))return chineseStr;
        char[] utfBytes = chineseStr.toCharArray();
        StringBuilder unicodeBytes = new StringBuilder();
        for (int i = 0; i < utfBytes.length; i++) {
        	unicodeBytes.append( "\\u");
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
            	unicodeBytes.append("00");
            }
            unicodeBytes.append(hexB);
        }
        return unicodeBytes.toString();
    }
    public final static Pattern chineseWord=Pattern.compile("[\u4e00-\u9fa5]");
    /**
     * 计算汉字数量
     * @param str
     * @return
     */
    public static int countChineseWord(String str){
    	int count=0;
    	if(StringUtils.isNotBlank(str)){
    		Matcher m=chineseWord.matcher(str);
    		while(m.find()){
    			count++;
    		}
    	}
    	return count;
    }
	
}
