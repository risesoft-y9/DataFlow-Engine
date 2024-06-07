package net.risesoft.api.utils;

import org.springframework.scheduling.support.CronSequenceGenerator;

import net.risedata.jdbc.commons.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description :
 * @ClassName TaskUtils
 * @Author lb
 * @Date 2023/1/31 9:32
 * @Version 1.0
 */
public class TaskUtils {
    private static final long ONETIME = 1000 * 60 * 60 * 23 + 1000 * 60 * 59 + 59 * 1000+999;


    public static List<String> getDateForOne(NextTime next, String format) {
        Date start = null;
        try {
            start = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date = new Date(start.getTime());
        List<String> res = new ArrayList<>();
        SimpleDateFormat dataFormat = new SimpleDateFormat(format == null ? "dd HH:mm" : format);
        while (((date.getTime() - start.getTime()) - ONETIME) < 0) {
            date = next.next(date);
            if (((date.getTime() - start.getTime()) - ONETIME) < 0) {
                res.add(dataFormat.format(date));
            }
        }
        return res;
    }


    public static List<String> getDayTaskOfSpped(int speed, String format) throws ParseException {
        final int speedTime = speed * 1000;
        return TaskUtils.getDateForOne((date) -> new Date(date.getTime() + speedTime), format);
    }

    public static List<String> getDayTaskOfCron(String cron, String format) throws ParseException {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cron);
        return TaskUtils.getDateForOne((date) -> cronSequenceGenerator.next(date), format);
    }
}
