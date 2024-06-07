package risesoft.data.transfer.stream.es.in._6;

import java.util.List;

import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.stream.es.utils.EsTemplate;

/**
 * 6.8版本的es数据输入流
 * 
 * @typeName ElasticsearchOutputStreamFactory
 * @date 2024年1月3日
 * @author lb
 */
public class ElasticsearchIutputStreamFactory implements DataInputStreamFactory {

	private EsTemplate esTemplate;

	@ConfigBean
	static class ConfigModel {

		@ConfigField(description = "host", required = true)
		private String host;
		@ConfigField(description = "用户名")
		private String userName;
		@ConfigField(description = "密码")
		private String password;
		@ConfigField(description = "索引", required = true)
		private String index;
		@ConfigField(description = "字段", required = true)
		private String[] cloumns;
		@ConfigField(description = "查询字段")
		private String query;
		@ConfigField(description = "排序")
		private String sort;
		@ConfigField(description = "从这个位置开始", value = "0")
		private Integer form;
		@ConfigField(description = "每次获取多大的值", value = "10")
		private Integer size;
		@ConfigField(description = "切分为多少块", value = "-1")
		private Integer tableNumber;
		@ConfigField(description = "切分系数", value = "-1")
		private Integer splitFactor;
	}

	private Logger logger;

	private ConfigModel configModel;

	public ElasticsearchIutputStreamFactory(ConfigModel config, LoggerFactory loggerFactory,
			Configuration configuration) {
		logger = loggerFactory.getLogger(configuration.getString("name", "ElasticsearchIutput6"));
		this.configModel = config;
		esTemplate = new EsTemplate(configModel.userName, configModel.password, configModel.host);
	}

	@Override
	public DataInputStream getStream() {
		// TODO Auto-generated method stub
		//TODO 创建一个用于执行任务的流对象
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		// TODO 获取 文档结构 保留字段文档类型

	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		//TODO 清空资源

	}

	@Override
	public List<Data> splitToData(int executorSize) throws Exception {
		int numberSize = configModel.tableNumber != -1 ? configModel.tableNumber : configModel.splitFactor;
		if (logger.isInfo()) {
			logger.info(this, "sub data to" + numberSize);
		}
		int count = esTemplate.getCount(configModel.index, configModel.query);
		// 调用接口获取size
		if (numberSize != -1) {

		} else {

		}
// size 为每次获取的大小 当 系数和 块数全部为-1的时候按照size 来分
		return null;
	}

}
