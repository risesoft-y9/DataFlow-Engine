package net.risedata.rpc.consumer.utils;

import java.util.ArrayList;

/**
 * @Description :
 * @ClassName NoNullArrList
 * @Author lb
 * @Date 2022/10/19 11:20
 * @Version 1.0
 */
public class NoNullArrayList extends ArrayList {

    @Override
    public synchronized boolean add(Object o) {
        if (o==null){
            throw new RuntimeException("object is null");
        }
        return super.add(o);
    }


    @Override
    public synchronized Object remove(int index) {
        return super.remove(index);
    }

    @Override
    public  synchronized boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public synchronized void add(int index, Object element) {
        if (element==null){
            throw new RuntimeException("object is null");
        }
        super.add(index, element);
    }


}
