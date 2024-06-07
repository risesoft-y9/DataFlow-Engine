package net.risedata.register.ribbon.impl;

/**
 * @Description : 权重算法
 * @ClassName WeightBalancer
 * @Author lb
 * @Date 2021/11/30 16:13
 * @Version 1.0
 */
public class WeightPollBalancer extends AbstractWeightBalancer {

    /**
     * 当前拿到的index
     */
    private int index = 0;


     synchronized int getIndex(int maxIndex) {

        if (index + 1 > maxIndex) {
            index = 0;
        }
        return index++;
    }




}
