package net.risesoft.api.utils.jdbc.filedTypeMapping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import java.sql.DatabaseMetaData;

public class JdbcTypeMappingCache {

	private static Map<String, Map<String, Integer>> databaseTypeMappingCache = new HashMap<>();
	private static Map<String, List<String>> databaseTypeListCache = new HashMap<>();
	
    private static Map<String, List<TypeDefinition>> databaseTypeCache = new HashMap<>();


	/**
	 * 获取数据库字段类型 和 java.sql.types 的映射关系
	 * 
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @param baseType
	 * @return
	 */
	public static Map<String, Integer> getFieldTypeMapping(String driver, String url, String username, String password,
			String baseType) {
		if (databaseTypeMappingCache.containsKey(baseType)) {

			return databaseTypeMappingCache.get(baseType);
		}

		List<String> typeNameList = new ArrayList<>();
		Map<String, Integer> typeMappingCache = new HashMap<>();
		try (Connection connection = getConnection(driver, url, username, password)) {
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet typeInfo = metaData.getTypeInfo();
			while (typeInfo.next()) {
				String typeName = typeInfo.getString("TYPE_NAME");
				if (!typeName.startsWith("_")) {
					int dataType = typeInfo.getInt("DATA_TYPE");
					typeMappingCache.put(typeName, dataType);
					typeNameList.add(typeName);

				}

			}

			databaseTypeMappingCache.put(baseType, typeMappingCache);
			databaseTypeListCache.put(baseType, typeNameList);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return typeMappingCache;
	}

	/**
	 * 获取数据库可选字段类型
	 * 
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @param baseType
	 * @return
	 */
	public static List<String> getFieldTypeList(String driver, String url, String username, String password,
			String baseType) {
		if (databaseTypeListCache.containsKey(baseType)) {
			return databaseTypeListCache.get(baseType);
		}

		List<String> typeNameList = new ArrayList<>();
		Map<String, Integer> typeMappingCache = new HashMap<>();
		try (Connection connection = getConnection(driver, url, username, password)) {
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet typeInfo = metaData.getTypeInfo();
			while (typeInfo.next()) {
				String typeName = typeInfo.getString("TYPE_NAME");
				if (!typeName.startsWith("_")) {
					int dataType = typeInfo.getInt("DATA_TYPE");
	                int precision = typeInfo.getInt("PRECISION");
                    System.out.println(precision);
					typeNameList.add(typeName+"--"+precision);

					typeMappingCache.put(typeName, dataType);
				}
			}

			databaseTypeListCache.put(baseType, typeNameList);
			databaseTypeMappingCache.put(baseType, typeMappingCache);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return typeNameList;
	}
	
	public static List<TypeDefinition> getFieldType(String driver, String url, String username, String password,
            String baseType) {
        if (databaseTypeCache.containsKey(baseType)) {
            return databaseTypeCache.get(baseType);
        }
        Connection connection = null;
        ResultSet typeInfo = null;
        List<TypeDefinition> typeDefinitionList = new ArrayList<TypeDefinition>();
        try {
        	connection = getConnection(driver, url, username, password);
            DatabaseMetaData metaData = connection.getMetaData();
            typeInfo = metaData.getTypeInfo();
            while (typeInfo.next()) {
                String typeName = typeInfo.getString("TYPE_NAME");
                if (!typeName.startsWith("_")) {
                	boolean istrue = typeDefinitionList.stream().anyMatch(e -> e.getTypeName() != null && e.getTypeName().equals(typeName));
                	if(!istrue) {
                		int typeNum = typeInfo.getInt("DATA_TYPE");
                        int precision = typeInfo.getInt("PRECISION");
                        
                        typeDefinitionList.add(new TypeDefinition(typeName, typeNum, precision));
                	}
                }
            }
            databaseTypeCache.put(baseType, typeDefinitionList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
        	ReleaseResource(connection, typeInfo);
		}
        return typeDefinitionList;
    }


	private static Connection getConnection(String driver, String url, String username, String password)
			throws SQLException, ClassNotFoundException {
		Class.forName(driver);
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		props.setProperty("remarks", "true");
		props.setProperty("useInformationSchema", "true");
		return DriverManager.getConnection(url, props);
	}
	
	public static void ReleaseResource(Connection connection, ResultSet rs) {
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

}
