package risesoft.data.transfer.stream.ftp.utils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;  
import org.apache.commons.net.ftp.FTPFile;  
import org.apache.commons.net.ftp.FTPReply;  
  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.IOException;  
  
public class FtpUploadExample {  
  
    public static void main(String[] args) {  
        String server = "ftp.example.com";  
        int port = 21;  
        String user = "username";  
        String pass = "password";  
  
        FTPClient ftpClient = new FTPClient();  
        try {  
            ftpClient.connect(server, port);  
            int reply = ftpClient.getReplyCode();  
  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                ftpClient.disconnect();  
                System.err.println("FTP server refused connection.");  
                return;  
            }  
  
            ftpClient.login(user, pass);  
            ftpClient.enterLocalPassiveMode();  
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);  
  
            // 指定要上传的文件  
            File localFile = new File("path/to/local/file.txt");  
            FileInputStream inputStream = new FileInputStream(localFile);  
            
            // 上传到FTP服务器  
//            ftpClient.storeFileStream("")
            boolean success = ftpClient.storeFile("path/on/server/file.txt", inputStream);  
            inputStream.close();  
  
            if (success) {  
                System.out.println("File has been uploaded successfully.");  
            } else {  
                System.out.println("Could not upload file.");  
            }  
  
            // 登出并断开连接  
            ftpClient.logout();  
            ftpClient.disconnect();  
  
        } catch (IOException ex) {  
            ex.printStackTrace();  
            try {  
                if (ftpClient.isConnected()) {  
                    ftpClient.logout();  
                    ftpClient.disconnect();  
                }  
            } catch (IOException ex1) {  
                ex1.printStackTrace();  
            }  
        }  
    }  
}