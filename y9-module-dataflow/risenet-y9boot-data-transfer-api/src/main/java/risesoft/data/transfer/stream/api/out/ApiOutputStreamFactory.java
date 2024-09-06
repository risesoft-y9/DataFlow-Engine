package risesoft.data.transfer.stream.api.out;

import java.util.List;

import net.risesoft.model.RequestModel;
import net.risesoft.y9.json.Y9JsonUtil;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ValueUtils;

/**
 * 接口数据源-输出流工厂
 * @author pzx
 *
 */
public class ApiOutputStreamFactory implements DataOutputStreamFactory {

	private RequestModel requestModel;// 请求参数
	private List<String> columns;// 字段列表
	private Boolean isBody;// 参数是否是body参数
	private Logger logger;

	public ApiOutputStreamFactory(Configuration configuration, JobContext jobContext) {
		this.requestModel = Y9JsonUtil.readValue(configuration.getString("requestModel"), RequestModel.class);
		this.columns = ValueUtils.getRequired(configuration.getList("column", String.class), "缺失字段列表");
		this.isBody = configuration.getBool("isBody", false);
		logger = jobContext.getLoggerFactory().getLogger(ApiOutputStreamFactory.class);
	}

	@Override
	public void init() {

	}

	@Override
	public void close() throws Exception {
		
	}

	@Override
	public DataOutputStream getStream() {
		return new ApiOutputStream(requestModel, columns, isBody, logger);
	}
}
