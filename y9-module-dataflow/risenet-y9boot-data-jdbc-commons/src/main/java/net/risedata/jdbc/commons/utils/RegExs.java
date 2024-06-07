package net.risedata.jdbc.commons.utils;
/**
 * 正则匹配集合
 * @author libo
 *2020年12月7日
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegExs {
	public static final Pattern INT_PATTERN = Pattern.compile("^[1-9]\\d*|0$");
    public static final Pattern A_Z_PATTERN = Pattern.compile("^[a-zA-Z]+$");
    public static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^[a-z_A-Z0-9]+$");
    
    public static final Pattern PHONE=Pattern.compile("0?(13|14|15|17|18|19|16)[0-9]{9}");

	/*
	 * public static final Pattern
	 * PHONE=Pattern.compile("0?(13|14|15|17|18|19|16)[0-9]{9}");
	 */
    public static boolean has(Pattern p,String complate) {
    	return p.matcher(complate).find();
    }

    
    
}
