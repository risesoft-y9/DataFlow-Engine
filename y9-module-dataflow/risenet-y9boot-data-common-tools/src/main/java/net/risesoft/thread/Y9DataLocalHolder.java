package net.risesoft.thread;

import com.alibaba.ttl.TransmittableThreadLocal;

public abstract class Y9DataLocalHolder {
	
	private static final TransmittableThreadLocal<String> MASKCLASS_HOLDER = new TransmittableThreadLocal<String>();
    private static final TransmittableThreadLocal<String> ENCRYPCLASS_HOLDER = new TransmittableThreadLocal<String>();
    private static final TransmittableThreadLocal<String> DATECLASS_HOLDER = new TransmittableThreadLocal<String>();
    private static final TransmittableThreadLocal<String> CONVERTCLASS_HOLDER = new TransmittableThreadLocal<String>();

    public static void clear() {
    	MASKCLASS_HOLDER.remove();
    	ENCRYPCLASS_HOLDER.remove();
    	DATECLASS_HOLDER.remove();
    	CONVERTCLASS_HOLDER.remove();
    }

    public static String getConvertClass() {
        return CONVERTCLASS_HOLDER.get();
    }

    public static void setConvertClass(final String convertClass) {
    	CONVERTCLASS_HOLDER.set(convertClass);
    }

    public static String getMaskClass() {
        return MASKCLASS_HOLDER.get();
    }

    public static void setMaskClass(final String maskClass) {
    	MASKCLASS_HOLDER.set(maskClass);
    }

    public static String getEncrypClass() {
        return ENCRYPCLASS_HOLDER.get();
    }

    public static void setEncrypClass(final String encrypClass) {
    	ENCRYPCLASS_HOLDER.set(encrypClass);
    }

    public static String getDateClass() {
        return DATECLASS_HOLDER.get();
    }

    public static void setDateClass(final String dataClass) {
    	DATECLASS_HOLDER.set(dataClass);
    }

}
