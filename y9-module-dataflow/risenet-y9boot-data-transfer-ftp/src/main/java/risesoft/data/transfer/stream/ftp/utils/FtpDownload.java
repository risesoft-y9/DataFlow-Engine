package risesoft.data.transfer.stream.ftp.utils;
import org.apache.commons.net.ftp.FTPClient;  
import org.apache.commons.net.ftp.FTPFile;  
import org.apache.commons.net.ftp.FTPReply;  
  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;  
  
public class FtpDownload {  
  
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
  
            boolean success = ftpClient.login(user, pass);  
  
            if (!success) {  
                System.err.println("Could not login to FTP server.");  
                ftpClient.logout();  
                ftpClient.disconnect();  
                return;  
            }  
  
            // Enter local file path where you want to save the downloaded file  
            String remoteFilePath = "/path/to/remote/file.txt";  
            String localFilePath = "/path/to/local/file.txt";  
  
            try (OutputStream outputStream = new FileOutputStream(localFilePath)) {  
                boolean success1 = ftpClient.retrieveFile(remoteFilePath, outputStream);  
  
                if (success1) {  
                    System.out.println("File has been downloaded successfully.");  
                }  
            }  
  
            ftpClient.logout();  
            ftpClient.disconnect();  
  
        } catch (IOException ex) {  
            System.err.println("Error: " + ex.getMessage());  
            ex.printStackTrace();  
        }  
    }  
}