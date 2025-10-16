package net.risedata.jdbc.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import net.risedata.jdbc.commons.utils.ClassTools;
import net.risedata.jdbc.exception.ConfigException;
import net.risedata.jdbc.executor.delete.Delete;
import net.risedata.jdbc.executor.delete.DeleteExecutor;
import net.risedata.jdbc.executor.delete.impl.DefaultDeleteExecutor;
import net.risedata.jdbc.executor.insert.Insert;
import net.risedata.jdbc.executor.insert.InsertExecutor;
import net.risedata.jdbc.executor.insert.impl.DefaultInsertExecutor;
import net.risedata.jdbc.executor.jdbc.JdbcExecutor;
import net.risedata.jdbc.executor.jdbc.impl.JdbcTemplateExecutor;
import net.risedata.jdbc.executor.log.PrintExecutor;
import net.risedata.jdbc.executor.log.impl.DefaultPrintExecutor;
import net.risedata.jdbc.executor.page.PageExecutor;
import net.risedata.jdbc.executor.page.impl.DerbyPageExecutor;
import net.risedata.jdbc.executor.page.impl.MysqlPageExecutor;
import net.risedata.jdbc.executor.page.impl.OraclePageExecutor;
import net.risedata.jdbc.executor.search.Search;
import net.risedata.jdbc.executor.search.SearchExecutor;
import net.risedata.jdbc.executor.search.impl.DefaultSearchExecutor;
import net.risedata.jdbc.executor.sync.SyncService;
import net.risedata.jdbc.executor.table.TableExecutor;
import net.risedata.jdbc.executor.table.impl.DefaultTableExecutor;
import net.risedata.jdbc.executor.update.Update;
import net.risedata.jdbc.executor.update.UpdateExecutor;
import net.risedata.jdbc.executor.update.impl.DefaultUpdateExecutor;
import net.risedata.jdbc.factory.InstanceFactoryManager;
import net.risedata.jdbc.factory.LoggerFactory;
import net.risedata.jdbc.factory.impl.SpringApplicationFactory;
import net.risedata.jdbc.mapping.columns.OneToManyColumnMapping;
import net.risedata.jdbc.repository.proxy.RepositoryCreateFactory;
import net.risedata.jdbc.utils.Log;

/**
 * spring boot 启动类
 * 
 * @author libo 2021年2月9日
 */
@Configuration
public class JdbcConfig implements ApplicationListener<ContextRefreshedEvent> {
	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JdbcConfig.class);
	public static final String SHOW_SQL_CONFIG = "net.risedata.jdbc.show.sql";
	public static final String DATASOURCE_TYPE = "net.risedata.jdbc.type";
	
	@Value("${" + SHOW_SQL_CONFIG + ":true}")
	private boolean isShow;

	@Value("${" + DATASOURCE_TYPE + ":}")
	private String type;

	public static SyncService SYNC;
	public static boolean isSync;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String, Object> objects = event.getApplicationContext().getBeansWithAnnotation(SpringBootApplication.class);
		Log.setDefaultPrintExecutor(event.getApplicationContext().getBean(PrintExecutor.class));
		Log.print("start ---- jdbc ");

		InstanceFactoryManager.init(new SpringApplicationFactory(event.getApplicationContext()));
		if (objects.keySet().size() > 0) {
			objects.forEach((k, v) -> {
				JdbcScan lScan = AnnotationUtils.findAnnotation(v.getClass(), JdbcScan.class);
				if (lScan != null) {
					int count = 0;
					long startTime = System.currentTimeMillis();
					if (!StringUtils.isEmpty(lScan.value()[0])) {
						List<Class<?>> classs = null;
						String[] paths = lScan.value();
						for (int i = 0; i < paths.length; i++) {
							classs = ClassTools.getClasses(paths[i]);
							for (Class<?> class1 : classs) {
								if (AnnotationUtils.findAnnotation(class1, Table.class) != null) {
									count++;
									Load.loadBean(class1);
								}
								;
							}
						}
						Log.print("load entiry" + count + " time for " + (System.currentTimeMillis() - startTime));
					}
				}
			});
		}

		if (isSync) {
			SYNC = event.getApplicationContext().getBean(SyncService.class);
		}
	}

	@ConditionalOnMissingBean(PageExecutor.class)
	@Bean
	public PageExecutor getPageExecutor(DataSource dataSource) {
		String url = null;
		Connection connection = null;
		try {
			if (StringUtils.isEmpty(type)) {
				connection = dataSource.getConnection();
				url = connection.getMetaData().getURL();
				type = url.substring(5, url.indexOf(":", 5));
			}
		} catch (SQLException e) {
			LOGGER.info("select page error:" + e.getMessage());
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		LOGGER.info("page type = " + type);
		switch (type) {
			case "oracle":
			case "dm":
				return new OraclePageExecutor();
			case "mysql":
			case "kingbase":
			case "postgresql":
				return new MysqlPageExecutor();
			case "derby":
				return new DerbyPageExecutor();
			default:
				throw new ConfigException(
						" The specified database does not have a corresponding paging implementation. Please add it manually");
		}
	}

	@ConditionalOnMissingBean(JdbcExecutor.class)
	@Bean
	public JdbcExecutor getJdbcExecutor(JdbcTemplate jt, PrintExecutor print) {
		if (isShow) {
			return LoggerFactory.getInstance(new JdbcTemplateExecutor(jt), JdbcExecutor.class, print);
		}
		return new JdbcTemplateExecutor(jt);
	}

	@ConditionalOnMissingBean(SearchExecutor.class)
	@Bean
	public SearchExecutor getSearchExecutor(JdbcExecutor je, PageExecutor pageExecutor) {
		SearchExecutor search = new DefaultSearchExecutor(je, pageExecutor);
		Search.setSearchExecutor(search);
		return search;
	}

	@ConditionalOnMissingBean(InsertExecutor.class)
	@Bean
	public InsertExecutor getInsertExecutor(JdbcExecutor je) {
		DefaultInsertExecutor insertExecutor = new DefaultInsertExecutor(je);
		Insert.setInsertExecutor(insertExecutor);
		return insertExecutor;
	}

	@ConditionalOnMissingBean(UpdateExecutor.class)
	@Bean
	public UpdateExecutor getUpdateExecutor(JdbcExecutor je) {
		UpdateExecutor update = new DefaultUpdateExecutor(je);
		Update.setUpdateExecutor(update);
		return update;
	}

	@ConditionalOnMissingBean(DeleteExecutor.class)
	@Bean
	public DeleteExecutor getDeleteExecutor(JdbcExecutor je) {
		DefaultDeleteExecutor deleteExecutor = new DefaultDeleteExecutor(je);
		Delete.setDeleteExecutor(deleteExecutor);
		return deleteExecutor;
	}

	@ConditionalOnMissingBean(TableExecutor.class)
	@Bean
	public TableExecutor getTableExecutor(JdbcExecutor je) {
		return new DefaultTableExecutor(je);
	}

	@ConditionalOnMissingBean(PrintExecutor.class)
	@Bean
	public DefaultPrintExecutor getPrintExecutor() {
		return new DefaultPrintExecutor();
	}

	@Bean
	public OneToManyColumnMapping getOneToManyColumnMapping() {
		return new OneToManyColumnMapping();
	}

	@Bean
	public RepositoryCreateFactory getRepositoryCreateFactory() {
		return new RepositoryCreateFactory();
	}

}
