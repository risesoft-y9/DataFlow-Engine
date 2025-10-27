package net.risesoft.converter.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.base64.Y9Base64Util;

public class Y9Encrytor {

    //SecretKey 负责保存对称密钥
    private SecretKey deskey;
    
    // GCM模式推荐的IV长度（12字节=96位）
    private static final int GCM_IV_LENGTH = 12;
    // 标签长度（16字节=128位）
    private static final int GCM_TAG_LENGTH = 16;
    
    public Y9Encrytor() {
        try {
        	String key = Y9Context.getProperty("y9.common.secret-key");
        	// 生成安全的AES密钥（128位）
        	KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        	// 根据KEY规则初始化密钥生成器生成一个128位的随机源
    		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
    		secureRandom.setSeed(key.getBytes());
    		keyGenerator.init(128, secureRandom);
            deskey = keyGenerator.generateKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * 对字符串加密
     */
    public String Encrytor(String content) throws Exception {
        // 生成随机IV（12字节）
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // 初始化加密器
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, parameterSpec);
        
        // 加密明文，结果包含密文+认证标签（GCM模式自动生成标签）
        byte[] ciphertext = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

        // 组合IV、密文+标签（格式：IV(12字节) + 密文+标签(n字节)）
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
        byteBuffer.put(iv);
        byteBuffer.put(ciphertext);
        byte[] encryptedData = byteBuffer.array();

        // 转为Base64方便传输/存储
        return Y9Base64Util.encode(encryptedData);
    }

    /**
     * 对字符串解密
     */
    public String Decryptor(String content) throws Exception {
    	// 解码Base64
        byte[] decodedData = Y9Base64Util.decodeAsBytes(content);

        // 分离IV和密文+标签
        ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv); // 前12字节为IV

        byte[] ciphertextWithTag = new byte[byteBuffer.remaining()];
        byteBuffer.get(ciphertextWithTag); // 剩余部分为密文+标签

        // 初始化解密器
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, deskey, parameterSpec);

        // 解密
        byte[] plaintext = cipher.doFinal(ciphertextWithTag);
        return new String(plaintext, StandardCharsets.UTF_8);
    }
    
//    public static void main(String[] args) throws Exception {
//    	// 历史加密数据的解密方法
//    	String msg = "";//密文
//    	// 密钥生成器  
//        KeyGenerator keyGen = KeyGenerator.getInstance("AES");  
//        // 根据KEY规则初始化密钥生成器生成一个128位的随机源
//		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
//		secureRandom.setSeed("risesoft".getBytes());
//		keyGen.init(128, secureRandom);
//        SecretKey secretKey = keyGen.generateKey();
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] decrypted = cipher.doFinal(Y9Base64Util.decodeAsBytes(msg));
//        System.out.println(new String(decrypted, StandardCharsets.UTF_8));
//    }
    
}
