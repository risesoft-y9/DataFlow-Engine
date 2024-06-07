package net.risedata.jdbc.commons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Dates {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static int doDate(Date d,Date doDate) {
        int count = 1;
    	Calendar c = Calendar.getInstance();
    	c.setTime(doDate);
    	int doN = c.get(Calendar.YEAR);
    	int doM = c.get(Calendar.MONTH);
    	int doD = c.get(Calendar.DATE); 
    	Calendar c2 = Calendar.getInstance();
    	c2.setTime(d);
    	int n = c2.get(Calendar.YEAR);
    	int m = c2.get(Calendar.MONTH);
    	int day = c2.get(Calendar.DATE);	
      
    	
    	long time1 = c.getTimeInMillis();
    	long time2 = c2.getTimeInMillis();
    	long abs = Math.abs(time2-time1);
    	count = (int)(abs/(1000*60*60*24));
    	System.out.println("年"+n+"月"+(m+1)+"日"+day+"距离年"+doN+"月"+(doM+1)+"日"+doD);
    	System.out.println("共 "+count+"天");
		return count;
    }
}
