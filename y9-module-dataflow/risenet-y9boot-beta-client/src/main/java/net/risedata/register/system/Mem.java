package net.risedata.register.system;

import cn.hutool.core.util.NumberUtil;

/**
 * 內存信息
 */
public class Mem {
    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;

    public double getTotal()
    {

        return NumberUtil.div(total, (1024 * 1024 * 1024), 2);
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public double getUsed()
    {
        return NumberUtil.div(used, (1024 * 1024 * 1024), 2);
    }
    public double getUsedScale()
    {
        return (used/total)*100;
    }
    public void setUsed(long used)
    {
        this.used = used;
    }

    public double getFree()
    {
        return NumberUtil.div(free, (1024 * 1024 * 1024), 2);
    }

    public void setFree(long free)
    {
        this.free = free;
    }

    public double getUsage()
    {
        return NumberUtil.mul(NumberUtil.div(used, total, 4), 100);
    }

    @Override
    public String toString() {
        return "Mem{" +
                "total=" + total +
                ", used=" + used +
                ", free=" + free +
                '}';
    }
}
