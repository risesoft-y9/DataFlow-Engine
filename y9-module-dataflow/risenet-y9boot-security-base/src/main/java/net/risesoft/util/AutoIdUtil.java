package net.risesoft.util;

/**
 * 随机id
 *
 * @author libo 2020年10月14日
 */
public class AutoIdUtil {

    /**
     * 返回一个随机id
     *
     * @param length 生成的长度 11+length
     * @return
     */
    public static String getRandomId(int length) {
        long time = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append(time);

        int random;
        int maxLength = sb.length() + length;
        while (sb.length() < maxLength) {
            random = (int) (Math.random() * 100);
            sb.append(random);
        }

        return sb.substring(0, maxLength);
    }

    public static Long getRandomLongId(int length) {
        return Long.parseLong(getRandomId(length));
    }

    public static Long getRandomLongId() {
        return Long.parseLong(getRandomId(0));
    }

    public static Long getRandomLongId20() {
        return Long.parseLong(getRandomId(7));
    }

    public static Long getRandomLongId36() {
        return Long.parseLong(getRandomId(23));
    }

    public static Long getRandomLongId26() {
        return Long.parseLong(getRandomId(13));
    }

    public static String getRandomId26() {
        return getRandomId(13);
    }

    public static String getRandomId30() {
        return getRandomId(17);
    }

    public static String getRandomId36() {
        return getRandomId(23);
    }
}
