package net.risesoft.converter.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import net.risesoft.y9.util.base64.Y9Base64Util;

public class Y9Encrytor {

	//KeyGenerator 提供对称密钥生成器的功能，支持各种算法
    private KeyGenerator keygen;
    
    //SecretKey 负责保存对称密钥
    private SecretKey deskey;
    
    //Cipher负责完成加密或解密工作
    private Cipher cipher;
    
    private String KEY = "risesoft";
    
    private String args = "AES";
    
    public Y9Encrytor() {
        try {
			// 构造密钥生成器，指定为AES算法,不区分大小写
			keygen = KeyGenerator.getInstance("AES");
			// 根据KEY规则初始化密钥生成器生成一个128位的随机源
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(KEY.getBytes());
			keygen.init(128, secureRandom);
			// 生成密钥
			deskey = keygen.generateKey();
			// 生成Cipher对象,指定其支持的AES算法
			cipher = Cipher.getInstance(args);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
    }

    /**
     * 对字符串加密
     */
    public String Encrytor(String content) throws Exception {
        // 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
    	cipher.init(Cipher.ENCRYPT_MODE, deskey);
        // 加密
        byte[] cipherByte = cipher.doFinal(content.getBytes("utf-8"));
        return new String(Y9Base64Util.encode(cipherByte));
    }

    /**
     * 对字符串解密
     */
    public String Decryptor(String content) throws Exception {
        // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
    	cipher.init(Cipher.DECRYPT_MODE, deskey);
    	// 解密
        byte[] cipherByte = cipher.doFinal(Y9Base64Util.decodeAsBytes(content));
        return new String(cipherByte, "utf-8");
    }
    
}
