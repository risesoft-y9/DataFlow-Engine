package risesoft.data.transfer.plug.data.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import java.util.Base64;

/**
 * 非对称加密ASA 算法
 * 
 * @typeName RSAExample
 * @date 2024年3月12日
 * @author lb
 */
public class RSAExample {
	private static final String ALGORITHM = "RSA";

	public static KeyPair generateKeyPair() throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		keyGen.initialize(2048);
		return keyGen.generateKeyPair();
	}

	public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
		Cipher encryptCipher = Cipher.getInstance(ALGORITHM);
		encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
		byte[] bytes = Base64.getDecoder().decode(cipherText);
		Cipher decryptCipher = Cipher.getInstance(ALGORITHM);
		decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedText = decryptCipher.doFinal(bytes);
		return new String(decryptedText);
	}

	public static void main(String[] args) throws Exception {
		KeyPair keyPair = generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		String originalString = "Hello RSA!";
		String encryptedString = encrypt(originalString, publicKey);
		String decryptedString = decrypt(encryptedString, privateKey);

		System.out.println("Original String: " + originalString);
		System.out.println("Encrypted String: " + encryptedString);
		System.out.println("Decrypted String: " + decryptedString);
	}
}