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

	private static final Random random = new Random();

    int getIndex(int maxIndex) {
        return random.nextInt(maxIndex);
    }
}
