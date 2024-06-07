package net.risesoft.api.utils;

/**
 * @Description :
 * @ClassName sort
 * @Author lb
 * @Date 2023/2/2 15:45
 * @Version 1.0
 */
public class Sort implements Comparable<Sort> {

    public Integer count;
    public String name;

    public Sort(Integer count, String name) {
        this.count = count;
        this.name = name;
    }

    @Override
    public int compareTo(Sort o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "sort{" +
                "count=" + count +
                ", name='" + name + '\'' +
                '}';
    }


}
