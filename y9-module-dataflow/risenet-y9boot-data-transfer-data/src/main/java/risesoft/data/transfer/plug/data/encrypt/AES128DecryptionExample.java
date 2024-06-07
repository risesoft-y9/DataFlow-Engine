package risesoft.data.transfer.plug.data.encrypt;
import javax.crypto.Cipher;  
import javax.crypto.spec.SecretKeySpec;  
import java.nio.charset.StandardCharsets;  
import java.util.Base64;  
  
public class AES128DecryptionExample {  
  
    private static final String ALGORITHM = "AES";  
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // 可以根据需要修改模式（如CBC）和填充（如PKCS7Padding）  
  
    public static void main(String[] args) throws Exception {  
        // 假设这是你从某个地方得到的加密后的Base64编码字符串  
        String encryptedBase64 = "wTJHhwMrkQiryDTwRMWFtw==";  
          
        // 这是你用于解密的AES密钥，必须是16字节（128位）  
        byte[] secretKey = "FTQBMD_@20220804".getBytes(StandardCharsets.UTF_8);  
          
        // 解码Base64字符串以获取加密的字节  
        byte[] encrypted = Base64.getDecoder().decode(encryptedBase64);  
          
        // 创建一个SecretKeySpec，它使用给定的密钥字节和算法名称  
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, ALGORITHM);  
          
        // 初始化Cipher实例为解密模式  
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);  
        cipher.init(Cipher.DECRYPT_MODE, keySpec);  
          
        // 执行解密操作  
        byte[] decrypted = cipher.doFinal(encrypted);  
          
        // 将解密后的字节转换为字符串  
        String decryptedText = new String(decrypted, StandardCharsets.UTF_8);  
          
        // 输出解密后的文本  
        System.out.println("Decrypted Text: " + decryptedText);  
    }  
}