package net.risesoft.util.home;

import net.risesoft.pojo.home.HomeData.QueryTimeRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.atomic.AtomicReference;

public class QueryTimeRangeCacheUtil {

    private static final List<QueryTimeRange> cachedQueryTimeRangeList = new ArrayList<>();
    private static final AtomicReference<LocalDate> cacheDateRef = new AtomicReference<>(LocalDate.MIN);

    public static final String LAST_WEEK = "最近一周";
    public static final String LAST_TWO_WEEKS = "最近二周";
    public static final String LAST_THREE_WEEKS = "最近三周";
    public static final String LAST_MONTH = "最近一个月";


    private QueryTimeRangeCacheUtil() {
    }

    public static List<QueryTimeRange> getQueryTimeRangeList() {
        LocalDate today = LocalDate.now();

        // 检查是否需要更新缓存
        if (!today.equals(cacheDateRef.get())) {
            synchronized (QueryTimeRangeCacheUtil.class) {
                if (!today.equals(cacheDateRef.get())) {
                    cachedQueryTimeRangeList.clear();
                    createQueryTimeRange(today);
                    cacheDateRef.set(today);
                }
            }
        }
        return cachedQueryTimeRangeList;
    }

    public static QueryTimeRange getQueryTimeRangeByName(String name) {
        return getQueryTimeRangeList().stream()
                .filter(range -> range.getName().equals(name))
                .findFirst().orElse(null);
    }


    /**
     * 以当前天的结束时间 为标准 向前推 的 时间范围
     *
     * @param today
     */
    private static void createQueryTimeRange(LocalDate today) {
        //System.out.println("**************************start*********************************");
        // 获取今日的结束时间
        LocalTime endTime = LocalTime.of(23, 59, 59);
        LocalDateTime todayEnd = LocalDateTime.of(today, endTime);

        // 一周
        addQueryTimeRange(LAST_WEEK, getStartTime(todayEnd, 1), todayEnd);

        // 二周
        addQueryTimeRange(LAST_TWO_WEEKS, getStartTime(todayEnd, 2), todayEnd);

        //三周
        addQueryTimeRange(LAST_THREE_WEEKS, getStartTime(todayEnd, 3), todayEnd);

        // 一个月
        LocalDateTime oneMonthStart = todayEnd.minusMonths(1).plusSeconds(1);
        addQueryTimeRange(LAST_MONTH, oneMonthStart, todayEnd);
    }

    private static LocalDateTime getStartTime(LocalDateTime endTime, int weeksAgo) {
        return endTime.minusWeeks(weeksAgo).plusSeconds(1);
    }

    private static void addQueryTimeRange(String name, LocalDateTime start, LocalDateTime end) {
        cachedQueryTimeRangeList.add(new QueryTimeRange(name, toEpochMilli(start), toEpochMilli(end)));
    }

    /*
     将 LocalDateTime 转换为毫秒数
     */
    private static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }


}
