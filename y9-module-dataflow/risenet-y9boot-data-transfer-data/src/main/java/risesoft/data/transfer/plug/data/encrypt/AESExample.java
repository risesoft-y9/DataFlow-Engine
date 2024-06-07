package risesoft.data.transfer.plug.data.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 对称加密AES
 * 
 * @typeName AESExample
 * @date 2024年3月12日
 * @author lb
 */
public class AESExample {
	private static final String ALGORITHM = "AES";
	private static final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't',
			'K', 'e', 'y' };

	/**
	 * 加密
	 * 
	 * @param valueToEnc
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String valueToEnc) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyValue, ALGORITHM));
		byte[] encrypted = cipher.doFinal(valueToEnc.getBytes());
		return Base64.getEncoder().encodeToString(encrypted);
	}

	/**
	 * 加密
	 * 
	 * @param valueToEnc
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String valueToEnc, byte[] key) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
		byte[] encrypted = cipher.doFinal(valueToEnc.getBytes());
		return Base64.getEncoder().encodeToString(encrypted);
	}

	/**
	 * 解密
	 * 
	 * @param encryptedValue
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encryptedValue) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyValue, ALGORITHM));
		byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
		return new String(original);
	}

	public static void main(String[] args) throws Exception {
		String originalString = "Hello World!";
		String encryptedString = encrypt(originalString);
		String decryptedString = decrypt(encryptedString);

		System.out.println("Original String: " + originalString);
		System.out.println("Encrypted String: " + encryptedString);
		System.out.println("Decrypted String: " + decryptedString);
	}
}