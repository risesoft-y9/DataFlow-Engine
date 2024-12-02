package net.risesoft.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import net.risesoft.model.Config;
import net.risesoft.model.RequestModel;
import net.risesoft.util.ApiTest;
import net.risesoft.y9.json.Y9JsonUtil;
import risesoft.data.transfer.stream.rdbms.utils.DBUtil;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

public class HandleSingleJobService {
	
	/**
	 * 处理任务
	 * @param config
	 * @return
	 */
	public static String handle(Config config) {
		if(config.getId().equals("api")) {
			return handleApi(config.getContent());
		}else {
			return handleBase(config.getContent());
		}
	}
	
	public static String handleBase(String content) {
		String msg = "执行成功";
		Map<String, Object> map = Y9JsonUtil.readHashMap(content);
		Connection connection = DBUtil.getConnection(DataBaseType.RDBMS, (String)map.get("jdbcUrl"), 
				(String)map.get("userName"), (String)map.get("password"));
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.execute((String)map.get("sql"));
		} catch (SQLException e) {
			msg = e.getMessage();
			e.printStackTrace();
		} finally {
			if (connection != null) {
	            try {
	                connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        if (stmt != null) {
	            try {
	            	stmt.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
		}
		return msg;
	}
	
	public static String handleApi(String content) {
		String msg = "执行成功";
		try {
			RequestModel requestModel = Y9JsonUtil.readValue(content, RequestModel.class);
			Map<String, Object> dataMap = Y9JsonUtil.readHashMap(ApiTest.sendApi(requestModel));
			if(dataMap == null || !(boolean) dataMap.get("success")) {
				msg = "接口返回失败，检查接口";
			}
		} catch (Exception e) {
			msg = "接口调用报错：" + e.getMessage();
		}
		return msg;
	}

}
