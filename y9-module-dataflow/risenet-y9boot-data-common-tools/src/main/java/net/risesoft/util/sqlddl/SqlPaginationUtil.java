package net.risesoft.util.sqlddl;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.pojo.Y9Page;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlPaginationUtil {
	
	public static Y9Page<Map<String, Object>> listTableData(String dataId, Connection connection, String columnNameAndValues, String tableName, 
			int pageNum, int pageSize) {
        List<Map<String, Object>> tableDataList = new ArrayList<>();
        int totalRecords = 0;
        int totalPages = 0;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder();

        try (Statement stmt = connection.createStatement()) {
            int offset = (pageNum - 1) * pageSize;
            String dialect = DbMetaDataUtil.getDatabaseDialectName(connection, false);
            String conditionSql = StringUtils.isBlank(dataId) ? "" : ("DATAID = '" + dataId + "' ");
            if (StringUtils.isNotBlank(columnNameAndValues)) {
                String[] arr = columnNameAndValues.split(";");

                for (int i = 0; i < arr.length; i++) {
                    String[] arrTemp = arr[i].split(":");
                    if (arrTemp.length == 2) {
                        String value = arrTemp[1];
                        String columnName = arrTemp[0].toUpperCase();
                        if (StringUtils.isBlank(conditionSql)) {
                            conditionSql += "INSTR(T." + columnName + ",'" + value + "') > 0 ";
                        } else {
                            conditionSql += " AND INSTR(T." + columnName + ",'" + value + "') > 0 ";
                        }
                    }
                }
            }

            if ("oracle".equalsIgnoreCase(dialect) || "dm".equalsIgnoreCase(dialect) || "kingbase".equalsIgnoreCase(dialect)) {
                sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\"  T WHERE ");
                if (StringUtils.isNotBlank(conditionSql)) {
                    sqlStr.append(conditionSql).append(" AND ");
                }
                sqlStr.append(" ROWNUM BETWEEN " + (offset + 1) + " AND " + (offset + pageSize));
            } else {
                sqlStr = new StringBuilder("SELECT * FROM " + tableName + "  T ");
                if (StringUtils.isNotBlank(conditionSql)) {
                    sqlStr.append("WHERE ").append(conditionSql);
                }
                sqlStr.append(" LIMIT ").append(offset).append(",").append(pageSize);
            }

            rs = stmt.executeQuery(sqlStr.toString());
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> rowMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    rowMap.put(columnName, columnValue);
                }
                tableDataList.add(rowMap);
            }
            // 获取总记录数
            String countSql = "SELECT COUNT(*) FROM " + tableName + " T "
                + (StringUtils.isNotBlank(conditionSql) ? " WHERE " + conditionSql : "");
            try (Statement countStmt = connection.createStatement();
                ResultSet countRs = countStmt.executeQuery(countSql)) {
                if (countRs.next()) {
                    totalRecords = countRs.getInt(1);
                }
            }
            // 计算总页数
            totalPages = (int)Math.ceil((double)totalRecords / pageSize);
        } catch (Exception ex) {
        	return Y9Page.failure(0, 0, 0, null, "获取数据失败：" + ex.getMessage(), 500);
        } finally {
            if (rs != null) {
                try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
            }
            if (connection != null) {
            	try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
            }
        }
        return Y9Page.success(pageNum, totalPages, totalRecords, tableDataList, "获取数据成功");
    }

}
