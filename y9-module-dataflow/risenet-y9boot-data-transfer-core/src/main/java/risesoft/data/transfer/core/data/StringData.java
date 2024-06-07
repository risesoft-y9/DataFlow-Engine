package risesoft.data.transfer.core.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串数据任务
 * 
 * @typeName StringData
 * @date 2023年12月15日
 * @author lb
 */
public class StringData implements Data {
	private String value;

	public StringData(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<Data> as(List<String> strs) {

		List<Data> res = new ArrayList<Data>();
		for (String string : strs) {
			res.add(new StringData(string));
		}
		return res;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "StringData [value=" + value + "]";
	}
	
	

}
