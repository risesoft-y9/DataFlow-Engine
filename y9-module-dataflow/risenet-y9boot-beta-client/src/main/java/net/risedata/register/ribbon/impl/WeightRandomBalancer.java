package net.risedata.register.ribbon.impl;


import java.util.Random;

/**
 * @Description : 权重随机算法
 * @ClassName WeightBalancer
 * @Author lb
 * @Date 2021/11/30 16:13
 * @Version 1.0
 */
public class WeightRandomBalancer extends AbstractWeightBalancer {


    int getIndex(int maxIndex) {
        return new Random().nextInt(maxIndex);
    }
}
