package risesoft.data.transfer.stream.es.out;

import java.util.List;

import net.risesoft.elastic.client.ElasticsearchRestClient;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ValueUtils;

/**
 * elastic输出流工厂
 * @author pzx
 *
 */
public class ElasticsearchOutputStreamFactory implements DataOutputStreamFactory {

	private String url;// 连接地址
	private String username;// 用户名
	private String password;// 密码
	private String indexName;// 索引表
	private List<String> columns;// 字段列表
	private String writerType;
	private Logger logger;
	private ElasticsearchRestClient elasticsearchRestClient;

	public ElasticsearchOutputStreamFactory(Configuration configuration, JobContext jobContext) {
		this.url = ValueUtils.getRequired(configuration.getString("jdbcUrl"), "缺失连接地址");
		this.password = configuration.getString("password", "");
		this.username = configuration.getString("userName", "");
		this.indexName = ValueUtils.getRequired(configuration.getString("tableName"), "缺失索引表名称");
		this.columns = ValueUtils.getRequired(configuration.getList("column", String.class), "缺失字段列表");
		this.writerType = configuration.getString("writerType", "insert");
		logger = jobContext.getLoggerFactory().getLogger(ElasticsearchOutputStreamFactory.class);
		elasticsearchRestClient = new ElasticsearchRestClient(url, username, password);
	}

	@Override
	public void init() {

	}

	@Override
	public void close() throws Exception {
		
	}

	@Override
	public DataOutputStream getStream() {
		return new ElasticsearchOutputStream(elasticsearchRestClient, indexName, columns, writerType, logger);
	}
}
