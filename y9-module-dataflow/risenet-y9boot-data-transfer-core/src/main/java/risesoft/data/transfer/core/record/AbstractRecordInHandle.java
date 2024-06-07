package risesoft.data.transfer.core.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import risesoft.data.transfer.core.context.JobContext;

/**
 * 封装了 RecordInHandle 事件 保留一条一条执行的方法 批调用改成循环调用一条一条 在不覆盖的时候提供初始化方法
 * 
 * @typeName AbstractRecordInHandle
 * @date 2024年3月13日
 * @author lb
 */
public abstract class AbstractRecordInHandle implements RecordInHandle {


	@Override
	public abstract void doIn(Record record);

	@Override
	public void doIn(List<Record> records) {
		for (Record record : records) {
			doIn(record);
		}
	}

}
