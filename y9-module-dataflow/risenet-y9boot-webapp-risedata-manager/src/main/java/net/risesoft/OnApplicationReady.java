package net.risesoft;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
import net.risesoft.security.dao.EnvironmentDao;
import net.risesoft.y9public.repository.DataMappingRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnApplicationReady implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {

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

	@Autowired
	private EnvironmentDao environmentDao;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (environmentDao.hasPublic() == 0) {
			environmentDao.create("Public", "Public", "默认环境");
			environmentDao.create("dev", "dev", "测试环境");
		}
	}

}
