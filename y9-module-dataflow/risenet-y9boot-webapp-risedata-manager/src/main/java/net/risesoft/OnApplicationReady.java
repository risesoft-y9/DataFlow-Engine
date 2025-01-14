package net.risesoft;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.y9public.repository.DataMappingRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {

	private final DataMappingRepository dataMappingRepository;

	@Resource(name = "jdbcTemplate4Public")
	private JdbcTemplate jdbcTemplate4Public;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (dataMappingRepository.count() == 0) {
			initSql();
		}
		LOGGER.info("init data finish");
	}

	public void initSql() {
		Connection conn = null;
		try {
			DruidDataSource dds = (DruidDataSource) jdbcTemplate4Public.getDataSource();
			conn = dds.getConnection();
			String dbType = dds.getDbType();
			String path = this.getClass().getClassLoader().getResource("/sql/data.sql").getPath();
			if (DbType.mysql.equals(dbType)) {
				path = this.getClass().getClassLoader().getResource("/sql/data-mysql.sql").getPath();
			}
			FileSystemResource rc = new FileSystemResource(path);
			EncodedResource er = new EncodedResource(rc, "UTF-8");
			ScriptUtils.executeSqlScript(conn, er);
		} catch (Exception e) {
			LOGGER.error("sql脚本执行发生异常-" + e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@PostConstruct
	public void init() {
		Integer count = jdbcTemplate4Public.queryForObject("select count(*) from Y9_DATASERVICE_ENVIRONMENT where name='Public'", Integer.class);
		if (count == null || count == 0) {
			jdbcTemplate4Public.update("INSERT INTO Y9_DATASERVICE_ENVIRONMENT (ID, NAME, DESCRIPTION) VALUES (?, ?, ?)", "Public", "Public", "默认环境");
			jdbcTemplate4Public.update("INSERT INTO Y9_DATASERVICE_ENVIRONMENT (ID, NAME, DESCRIPTION) VALUES (?, ?, ?)", "dev", "dev", "测试环境");
			LOGGER.info("init environment finish");
		}
	}

}
