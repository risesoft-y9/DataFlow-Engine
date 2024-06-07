package net.risesoft.api.utils;

import java.util.HashMap;

/**
 * @Description : 批量操作
 * @ClassName BatchOperation
 * @Author lb
 * @Date 2023/2/21 15:54
 * @Version 1.0
 */
public class BatchOperation<T>{

    private int count ;

    private T value;
    private CommitFunction commitFunction;
    public BatchOperation(int count,T value,CommitFunction<T> commitFunction) {
        this.count = count;
        this.value = value;
        this.commitFunction= commitFunction;
    }
    public BatchOperation(int count,T value) {
        this.count = count;
        this.value = value;
    }

    public synchronized void commit(CommitFunction<T> commitFunction) {
        this.count--;
        commitFunction.commit(value);
        if (count==0){
            this.commitFunction.commit(value);
        }
    }

    public synchronized void onCommit(CommitFunction<T> commitFunction){
        this.commitFunction = commitFunction;
        if (count==0){
            this.commitFunction.commit(value);
        }
    }
}
