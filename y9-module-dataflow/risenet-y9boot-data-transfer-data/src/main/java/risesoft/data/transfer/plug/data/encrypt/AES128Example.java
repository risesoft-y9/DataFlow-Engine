package risesoft.data.transfer.plug.data.encrypt;

import javax.crypto.Cipher;  
import javax.crypto.KeyGenerator;  
import javax.crypto.SecretKey;  
import javax.crypto.spec.SecretKeySpec;  
import java.nio.charset.StandardCharsets;    
import java.util.Base64;  
  
public class AES128Example {  
  
    private static final String ALGORITHM = "AES";  
    private static final int KEY_SIZE = 128;  
  
    public static void main(String[] args) throws Exception {  
        String originalText = "Hello, World!";  
        String encryptedText = encrypt(originalText);  
        String decryptedText = decrypt(encryptedText);  
  
        System.out.println("Original Text: " + originalText);  
        System.out.println("Encrypted Text: " + encryptedText);  
        System.out.println("Decrypted Text: " + decryptedText);  
    }  
  
    public static String encrypt(String valueToEnc) throws Exception {  
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);  
        keyGen.init(KEY_SIZE);  
        SecretKey secretKey = keyGen.generateKey();  
        byte[] encodedValue = secretKey.getEncoded();  
        SecretKeySpec secretKeySpec = new SecretKeySpec(encodedValue, ALGORITHM);  
  
        Cipher cipher = Cipher.getInstance(ALGORITHM);  
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);  
  
        byte[] encryptedValue = cipher.doFinal(valueToEnc.getBytes(StandardCharsets.UTF_8));  
        return Base64.getEncoder().encodeToString(encryptedValue);  
    }  
  
    public static String decrypt(String encryptedValue) throws Exception {  
        byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);  
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);  
        keyGen.init(KEY_SIZE);  
        SecretKey secretKey = keyGen.generateKey();  
        byte[] encodedValue = secretKey.getEncoded();  
        SecretKeySpec secretKeySpec = new SecretKeySpec(encodedValue, ALGORITHM);  
  
        Cipher cipher = Cipher.getInstance(ALGORITHM);  
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);  
  
        byte[] originalValue = cipher.doFinal(decodedValue);  
        return new String(originalValue, StandardCharsets.UTF_8);  
    }  
}