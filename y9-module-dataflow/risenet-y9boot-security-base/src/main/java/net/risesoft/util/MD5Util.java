package net.risesoft.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * md5加密
 * @typeName MD5Util
 * @date 2024年1月11日
 * @author lb
 */
public class MD5Util {
    public final static  String md5key = "DATA_SERVICE";
    /**
     * MD5方法
     * @param text 明文
     * @param key 密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) {
        //加密后的字符串
        String encodeStr= DigestUtils.md5Hex(text + key);
        return encodeStr;
    }
    public static String md5(String text)  {
        //加密后的字符串

        return md5( text, md5key);
    }
    /**
     * MD5验证方法
     * @param text 明文
     * @param key 密钥
     * @param md5 密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        //根据传入的密钥进行验证
        String md5Text = md5(text, key);
        if(md5Text.equalsIgnoreCase(md5))
        {
            return true;
        }
        return false;
    }

    /**
     * 测试主函数
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        String str = new String("Beta");
        System.out.println("原始：" + str);
        System.out.println("MD5后：" + md5("Beta",md5key));
        System.out.println(verify("Beta",md5key,"d9ecd6df542ee202937fdd40101bf619"));
    }
}
