package risesoft.data.transfer.core.stream.out;


import java.util.List;
import risesoft.data.transfer.core.record.Record;

/**
 * 输出结果包含 成功的条数以及失败的条数和失败的数据
 * @typeName OutResult
 * @date 2023年12月5日
 * @author lb
 */
public class OutResult {
	/**
	 * 成功大小
	 */
	private int successSize;
	/**
	 * 脏数据/失败数据
	 */
	private List<Record> fail;

	public OutResult(int successSize, List<Record> fail) {
		super();
		this.successSize = successSize;
		this.fail = fail;
	}

	public int getSuccessSize() {
		return successSize;
	}

	public void setSuccessSize(int successSize) {
		this.successSize = successSize;
	}

	public List<Record> getFail() {
		return fail;
	}

	public void setFail(List<Record> fail) {
		this.fail = fail;
	}

}
