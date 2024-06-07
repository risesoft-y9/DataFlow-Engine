package risesoft.data.transfer.stream.es.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 模似http请求的工具
 * @author xieliuping
 *
 */
public class HttpClientUtil {
	static String newLine="\r\n"; 
	
	
	/**
	 * 将json字符串post出去
	 * @param url
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String url,String jsonStr) throws Exception{
		return sendRequestByRaw("POST", url, jsonStr, "UTF-8", null, 30000, false);
	}
	public static String httpGET(String url) throws Exception{
		return sendRequestByRaw("GET", url, null, "UTF-8", null, 30000, false);
	}
	

	private static HttpURLConnection getConnection(String url,String methodName) throws IOException{
		if(url.startsWith("https://")){
			return getHttpsURLConnection(url, methodName);
		}else{
			URL url0 = new URL(url);  
			return (HttpURLConnection) url0.openConnection();  
		}
	}
	/**
	 * 用原生java发送http请求
	 * Set the method for the URL request, one of: 
	 * <UL>
     *  <LI>GET
     *  <LI>POST
     *  <LI>HEAD
     *  <LI>OPTIONS
     *  <LI>PUT
     *  <LI>DELETE
     *  <LI>TRACE
     * </UL> are legal, subject to protocol restrictions.  The default
	 * @param methodName 只是POST或PUT，默认为POST
	 * @param url
	 * @param paramStr 可以是key=value&key2=value2...也可以是{'key':value,'key2':value2}
	 * @param charset 默认UTF-8
	 * @param contentType application/json、multipart/form-data
	 * @param timeout ConnectTimeout/ReadTimeout毫秒，0表示无限
	 * @param header 
	 * @return
	 * @throws Exception
	 */
	public static String sendRequestByRaw(String methodName,String url,String paramStr,
			String charset,String contentType,int timeout,boolean keepAlive,Map<String, String> header) throws Exception {  
 		HttpURLConnection conn=null;
		try {  
			// 换行符  
			charset=StringUtils.isBlank(charset)?"UTF-8":charset;  
			contentType=StringUtils.isBlank(contentType)?"application/json":contentType;  
			// 服务器的域名  
			conn=getConnection(url,methodName);
			// 设置为POST
			conn.setRequestMethod(methodName.toUpperCase()); // 设置请求方式
			// 发送POST请求必须设置如下两行  
			conn.setDoOutput(true);  
			conn.setDoInput(true);  
			conn.setUseCaches(false);  
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			// 设置请求头参数  
			if(keepAlive){
				conn.setRequestProperty("connection", "Keep-Alive");  
			}
			conn.setRequestProperty("Charset", charset);  
			conn.setRequestProperty("Content-Type", contentType); 
			if(header!=null&&!header.isEmpty()){
				for(Iterator<Entry<String, String>> it=header.entrySet().iterator();it.hasNext();){
					Entry<String, String> entry=it.next();
					conn.setRequestProperty(entry.getKey(), entry.getValue()); 
				}
				
			}
			
			if(StringUtils.isNotBlank(paramStr)){
				OutputStream out =conn.getOutputStream();  
				out.write(paramStr.getBytes(charset));
				out.close();
			}
			// 定义BufferedReader输入流来读取URL的响应  
			return inputStream2StringBuilder(conn.getInputStream(), charset).toString();
		} catch (Exception e) {  
			e.printStackTrace();
			String error=inputStream2StringBuilder(conn.getErrorStream(), charset).toString();
			System.out.println("服务器返回报错信息："+error);
			return "";
		}finally{
			if(!keepAlive){
				//缓存连接，可以利用
				if(conn!=null){
					conn.disconnect();
				}
			}
		}
	}
	/**
	 * 用原生java发送http请求
	 * Set the method for the URL request, one of: 
	 * <UL>
     *  <LI>GET
     *  <LI>POST
     *  <LI>HEAD
     *  <LI>OPTIONS
     *  <LI>PUT
     *  <LI>DELETE
     *  <LI>TRACE
     * </UL> are legal, subject to protocol restrictions.  The default
	 * @param methodName 只是POST或PUT，默认为POST
	 * @param url
	 * @param paramStr 可以是key=value&key2=value2...也可以是{'key':value,'key2':value2}
	 * @param charset 默认UTF-8
	 * @param contentType application/json、multipart/form-data
	 * @param timeout ConnectTimeout/ReadTimeout毫秒，0表示无限
	 * @return
	 * @throws Exception
	 */
	public static String sendRequestByRaw(String methodName,String url,String paramStr,
			String charset,String contentType,int timeout,boolean keepAlive) throws Exception {  
		return sendRequestByRaw(methodName, url, paramStr, charset, contentType, timeout, keepAlive, null);
	}
	

	
	public static StringBuilder inputStream2StringBuilder(InputStream in,String charset) throws IOException{
		// 定义BufferedReader输入流来读取URL的响应  
		StringBuilder sb=new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(  
				in,charset));  
		String line = null;  
		line = reader.readLine();
		if(line!=null){
			sb.append(line);
			sb.append(newLine);
			while ((line = reader.readLine()) != null) {  
				sb.append(newLine);
				sb.append(line);
			}  
		}
		in.close();
		return sb;
	}
	
	
	
	public String httpPOST_upload(String url,byte[] bytes) throws IOException{
		 URL url2 = new URL(url);   
		  
		 URLConnection urlConnection = url2.openConnection();  
		          // 此处的urlConnection对象实际上是根据URL的   
		          // 请求协议(此处是http)生成的URLConnection类   
		          // 的子类HttpURLConnection,故此处最好将其转化   
		          // 为HttpURLConnection类型的对象,以便用到   
		          // HttpURLConnection更多的API.如下:   
		   
		 HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
		 // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在   
		 // http正文内，因此需要设为true, 默认情况下是false;   
		 httpUrlConnection.setDoOutput(true);   
		  
		 // 设置是否从httpUrlConnection读入，默认情况下是true;   
		 httpUrlConnection.setDoInput(true);   
		  
		 // Post 请求不能使用缓存   
		 httpUrlConnection.setUseCaches(false);   
		  
		 // 设定请求的方法为"POST"，默认是GET   
		 httpUrlConnection.setRequestMethod("POST");   
		  
		 // 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，   
	    httpUrlConnection.connect();
	    // 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，   
	    // 所以在开发中不调用上述的connect()也可以)。   
	    OutputStream outStrm = httpUrlConnection.getOutputStream(); 
	    ByteArrayInputStream bais=new ByteArrayInputStream(bytes);
	    byte[] buf=new byte[8192];
	    while(-1!=bais.read(buf)){
	   	 outStrm.write(buf);
	   	 outStrm.flush();
	    }
	    outStrm.close();
	    // 调用HttpURLConnection连接对象的getInputStream()函数,   
	    // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。   
	    InputStream inStrm = httpUrlConnection.getInputStream(); // <===注意，实际发送请求的代码段就在这里   
	    // 上边的httpConn.getInputStream()方法已调用,本次HTTP请求已结束,下边向对象输出流的输出已无意义，   
	    // 既使对象输出流没有调用close()方法，下边的操作也不会向对象输出流写入任何数据.   
	    // 因此，要重新发送数据时需要重新创建连接、重新设参数、重新创建流对象、重新写数据、   
	    // 重新发送数据(至于是否不用重新这些操作需要再研究)  
	    System.out.println("httpGET method url:"+url);
		System.out.println("httpGET method httpcode="+httpUrlConnection.getResponseCode());
		StringBuilder sb=new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(inStrm,"utf-8"));
		String line=null;
		while ((line = in.readLine())!= null){
			sb.append(line);
		}
		inStrm.close();
		System.out.println(sb.toString());
	    return sb.toString();
	}
	public void httpPOST_serializableObject(String url,Object outObj) throws IOException{
		URL url2 = new URL(url);   
		
		URLConnection urlConnection = url2.openConnection();  
		// 此处的urlConnection对象实际上是根据URL的   
		// 请求协议(此处是http)生成的URLConnection类   
		// 的子类HttpURLConnection,故此处最好将其转化   
		// 为HttpURLConnection类型的对象,以便用到   
		// HttpURLConnection更多的API.如下:   
		
		HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
		// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在   
		// http正文内，因此需要设为true, 默认情况下是false;   
		httpUrlConnection.setDoOutput(true);   
		
		// 设置是否从httpUrlConnection读入，默认情况下是true;   
		httpUrlConnection.setDoInput(true);   
		
		// Post 请求不能使用缓存   
		httpUrlConnection.setUseCaches(false);   
		
		// 设定传送的内容类型是可序列化的java对象   
		// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)   
		httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");   
		
		// 设定请求的方法为"POST"，默认是GET   
		httpUrlConnection.setRequestMethod("POST");   
		
		// 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，   
		httpUrlConnection.connect();
		// 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，   
		// 所以在开发中不调用上述的connect()也可以)。   
		OutputStream outStrm = httpUrlConnection.getOutputStream(); 
		
		// 现在通过输出流对象构建对象输出流对象，以实现输出可序列化的对象。   
		ObjectOutputStream objOutputStrm = new ObjectOutputStream(outStrm);   
		// 向对象输出流写出数据，这些数据将存到内存缓冲区中   
		objOutputStrm.writeObject(outObj);   
		// 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）   
		objOutputStrm.flush();   
		// 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,   
		// 在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器   
		objOutputStrm.close();   
		// 调用HttpURLConnection连接对象的getInputStream()函数,   
		// 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。   
		InputStream inStrm = httpUrlConnection.getInputStream(); // <===注意，实际发送请求的代码段就在这里   
		// 上边的httpConn.getInputStream()方法已调用,本次HTTP请求已结束,下边向对象输出流的输出已无意义，   
		// 既使对象输出流没有调用close()方法，下边的操作也不会向对象输出流写入任何数据.   
		// 因此，要重新发送数据时需要重新创建连接、重新设参数、重新创建流对象、重新写数据、   
		// 重新发送数据(至于是否不用重新这些操作需要再研究)   
		System.out.println("httpPOST_serializableObject method httpcode="+httpUrlConnection.getResponseCode());
		inStrm.close();
	}
	/**
	 * 通过get方式请求，
	 * 获得文件流并写到指定的路径
	 * @param url
	 * @param destPath
	 * @return
	 * @throws IOException
	 */
	public static long httpGET_download(String url,String destPath) throws IOException{
		URL url2 = new URL(url);   
		
		URLConnection urlConnection = url2.openConnection();  
		// 此处的urlConnection对象实际上是根据URL的   
		// 请求协议(此处是http)生成的URLConnection类   
		// 的子类HttpURLConnection,故此处最好将其转化   
		// 为HttpURLConnection类型的对象,以便用到   
		// HttpURLConnection更多的API.如下:   
		
		HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
		// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在   
		// http正文内，因此需要设为true, 默认情况下是false;   
		httpUrlConnection.setDoOutput(false);   
		
		// 设置是否从httpUrlConnection读入，默认情况下是true;   
		httpUrlConnection.setDoInput(true);   
		
		// Post 请求不能使用缓存   
		httpUrlConnection.setUseCaches(false);   
		
		// 设定请求的方法为"POST"，默认是GET   
		httpUrlConnection.setRequestMethod("GET");   
		
		// 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，   
		httpUrlConnection.connect();
		
		InputStream inStrm = httpUrlConnection.getInputStream(); // <===注意，实际发送请求的代码段就在这里   
		// 上边的httpConn.getInputStream()方法已调用,本次HTTP请求已结束,下边向对象输出流的输出已无意义，   
		// 既使对象输出流没有调用close()方法，下边的操作也不会向对象输出流写入任何数据.   
		// 因此，要重新发送数据时需要重新创建连接、重新设参数、重新创建流对象、重新写数据、   
		// 重新发送数据(至于是否不用重新这些操作需要再研究)  
		System.out.println("httpGET_download method url="+url);
		System.out.println("httpGET_download method httpcode="+httpUrlConnection.getResponseCode());
		File file=new File(destPath);
		if(!file.getParentFile().exists())file.getParentFile().mkdirs();
		FileOutputStream fos=new FileOutputStream(file);
	    byte[] buffer = new byte[1024];
	    int len = 0;
	    while(-1 != (len = inStrm.read(buffer))){
	        fos.write(buffer,0,len);
	        fos.flush();
	    }
	    fos.close();
	    inStrm.close();
	    httpUrlConnection.disconnect();
	    return file.length();
	}
	//==========================================================
	private static final class DefaultTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static HttpsURLConnection getHttpsURLConnection(String uri,
			String method) throws IOException {
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0],
					new TrustManager[] { new DefaultTrustManager() },
					new SecureRandom());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		SSLSocketFactory ssf = ctx.getSocketFactory();

		URL url = new URL(uri);
		HttpsURLConnection httpsConn = (HttpsURLConnection) url
				.openConnection();
		httpsConn.setSSLSocketFactory(ssf);
		httpsConn.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		httpsConn.setRequestMethod(method);
		httpsConn.setDoInput(true);
		httpsConn.setDoOutput(true);
		return httpsConn;
	}

	
	/**
	 * 下载中转需要用到
	 * 获取链接地址文件的byte数据  
	 * @param fileUrl
	 * @return
	 * @throws Exception
	 */
    public static byte[] getUrlFileData(String fileUrl) throws Exception{  
    	URL url = new URL(fileUrl);  
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();  
        httpConn.setConnectTimeout(30000);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.connect();  
        InputStream cin = httpConn.getInputStream();  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while ((len = cin.read(buffer)) != -1) {  
           outStream.write(buffer, 0, len);  
        }  
        cin.close();  
        byte[] fileData = outStream.toByteArray();  
        outStream.close();  
        return fileData;  
   }
}