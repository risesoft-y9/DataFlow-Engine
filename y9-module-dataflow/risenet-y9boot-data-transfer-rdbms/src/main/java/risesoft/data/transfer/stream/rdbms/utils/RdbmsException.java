package risesoft.data.transfer.stream.rdbms.utils;



import risesoft.data.transfer.core.exception.ErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

/**
 * Created by judy.lt on 2015/6/5.
 */
public class RdbmsException extends TransferException{
    public RdbmsException(ErrorCode errorCode, String message){
        super(errorCode,message);
    }

    public static TransferException asConnException(DataBaseType dataBaseType,Exception e,String userName,String dbName){
        if (dataBaseType.equals(DataBaseType.MySql)){
            DBUtilErrorCode dbUtilErrorCode = mySqlConnectionErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_CONN_DB_ERROR && dbName !=null ){
                return TransferException.as(dbUtilErrorCode,"该数据库名称为："+dbName+" 具体错误信息为："+e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_CONN_USERPWD_ERROR ){
                return TransferException.as(dbUtilErrorCode,"该数据库用户名为："+userName+" 具体错误信息为："+e);
            }
            return TransferException.as(dbUtilErrorCode," 具体错误信息为："+e);
        }

        if (dataBaseType.equals(DataBaseType.Oracle)){
            DBUtilErrorCode dbUtilErrorCode = oracleConnectionErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_CONN_DB_ERROR && dbName != null){
                return TransferException.as(dbUtilErrorCode,"该数据库名称为："+dbName+" 具体错误信息为："+e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_CONN_USERPWD_ERROR ){
                return TransferException.as(dbUtilErrorCode,"该数据库用户名为："+userName+" 具体错误信息为："+e);
            }
            return TransferException.as(dbUtilErrorCode," 具体错误信息为："+e);
        }
        return TransferException.as(DBUtilErrorCode.CONN_DB_ERROR," 具体错误信息为："+e);
    }

    public static DBUtilErrorCode mySqlConnectionErrorAna(String e){
        if (e.contains(Constant.MYSQL_DATABASE)){
            return DBUtilErrorCode.MYSQL_CONN_DB_ERROR;
        }

        if (e.contains(Constant.MYSQL_CONNEXP)){
            return DBUtilErrorCode.MYSQL_CONN_IPPORT_ERROR;
        }

        if (e.contains(Constant.MYSQL_ACCDENIED)){
            return DBUtilErrorCode.MYSQL_CONN_USERPWD_ERROR;
        }

        return DBUtilErrorCode.CONN_DB_ERROR;
    }

    public static DBUtilErrorCode oracleConnectionErrorAna(String e){
        if (e.contains(Constant.ORACLE_DATABASE)){
            return DBUtilErrorCode.ORACLE_CONN_DB_ERROR;
        }

        if (e.contains(Constant.ORACLE_CONNEXP)){
            return DBUtilErrorCode.ORACLE_CONN_IPPORT_ERROR;
        }

        if (e.contains(Constant.ORACLE_ACCDENIED)){
            return DBUtilErrorCode.ORACLE_CONN_USERPWD_ERROR;
        }

        return DBUtilErrorCode.CONN_DB_ERROR;
    }

    public static TransferException asQueryException(DataBaseType dataBaseType, Exception e,String querySql,String table,String userName){
        if (dataBaseType.equals(DataBaseType.MySql)){
            DBUtilErrorCode dbUtilErrorCode = mySqlQueryErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_QUERY_TABLE_NAME_ERROR && table != null){
                return TransferException.as(dbUtilErrorCode,"表名为："+table+" 执行的SQL为:"+querySql+" 具体错误信息为："+e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_QUERY_SELECT_PRI_ERROR && userName != null){
                return TransferException.as(dbUtilErrorCode,"用户名为："+userName+" 具体错误信息为："+e);
            }

            return TransferException.as(dbUtilErrorCode,"执行的SQL为: "+querySql+" 具体错误信息为："+e);
        }

        if (dataBaseType.equals(DataBaseType.Oracle)){
            DBUtilErrorCode dbUtilErrorCode = oracleQueryErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_QUERY_TABLE_NAME_ERROR && table != null){
                return TransferException.as(dbUtilErrorCode,"表名为："+table+" 执行的SQL为:"+querySql+" 具体错误信息为："+e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_QUERY_SELECT_PRI_ERROR){
                return TransferException.as(dbUtilErrorCode,"用户名为："+userName+" 具体错误信息为："+e);
            }

            return TransferException.as(dbUtilErrorCode,"执行的SQL为: "+querySql+" 具体错误信息为："+e);

        }

        return TransferException.as(DBUtilErrorCode.SQL_EXECUTE_FAIL, "执行的SQL为: "+querySql+" 具体错误信息为："+e);
    }

    public static DBUtilErrorCode mySqlQueryErrorAna(String e){
        if (e.contains(Constant.MYSQL_TABLE_NAME_ERR1) && e.contains(Constant.MYSQL_TABLE_NAME_ERR2)){
            return DBUtilErrorCode.MYSQL_QUERY_TABLE_NAME_ERROR;
        }else if (e.contains(Constant.MYSQL_SELECT_PRI)){
            return DBUtilErrorCode.MYSQL_QUERY_SELECT_PRI_ERROR;
        }else if (e.contains(Constant.MYSQL_COLUMN1) && e.contains(Constant.MYSQL_COLUMN2)){
            return DBUtilErrorCode.MYSQL_QUERY_COLUMN_ERROR;
        }else if (e.contains(Constant.MYSQL_WHERE)){
            return DBUtilErrorCode.MYSQL_QUERY_SQL_ERROR;
        }
        return DBUtilErrorCode.READ_RECORD_FAIL;
    }

    public static DBUtilErrorCode oracleQueryErrorAna(String e){
        if (e.contains(Constant.ORACLE_TABLE_NAME)){
            return DBUtilErrorCode.ORACLE_QUERY_TABLE_NAME_ERROR;
        }else if (e.contains(Constant.ORACLE_SQL)){
            return DBUtilErrorCode.ORACLE_QUERY_SQL_ERROR;
        }else if (e.contains(Constant.ORACLE_SELECT_PRI)){
            return DBUtilErrorCode.ORACLE_QUERY_SELECT_PRI_ERROR;
        }
        return DBUtilErrorCode.READ_RECORD_FAIL;
    }

    public static TransferException asSqlParserException(DataBaseType dataBaseType, Exception e,String querySql){
        if (dataBaseType.equals(DataBaseType.MySql)){
            throw TransferException.as(DBUtilErrorCode.MYSQL_QUERY_SQL_PARSER_ERROR, "执行的SQL为:"+querySql+" 具体错误信息为：" + e);
        }
        if (dataBaseType.equals(DataBaseType.Oracle)){
            throw TransferException.as(DBUtilErrorCode.ORACLE_QUERY_SQL_PARSER_ERROR,"执行的SQL为:"+querySql+" 具体错误信息为：" +e);
        }
        throw TransferException.as(DBUtilErrorCode.READ_RECORD_FAIL,"执行的SQL为:"+querySql+" 具体错误信息为："+e);
    }

    public static TransferException asPreSQLParserException(DataBaseType dataBaseType, Exception e,String querySql){
        if (dataBaseType.equals(DataBaseType.MySql)){
            throw TransferException.as(DBUtilErrorCode.MYSQL_PRE_SQL_ERROR, "执行的SQL为:"+querySql+" 具体错误信息为：" + e);
        }

        if (dataBaseType.equals(DataBaseType.Oracle)){
            throw TransferException.as(DBUtilErrorCode.ORACLE_PRE_SQL_ERROR,"执行的SQL为:"+querySql+" 具体错误信息为：" +e);
        }
        throw TransferException.as(DBUtilErrorCode.READ_RECORD_FAIL,"执行的SQL为:"+querySql+" 具体错误信息为："+e);
    }

    public static TransferException asPostSQLParserException(DataBaseType dataBaseType, Exception e,String querySql){
        if (dataBaseType.equals(DataBaseType.MySql)){
            throw TransferException.as(DBUtilErrorCode.MYSQL_POST_SQL_ERROR, "执行的SQL为:"+querySql+" 具体错误信息为：" + e);
        }

        if (dataBaseType.equals(DataBaseType.Oracle)){
            throw TransferException.as(DBUtilErrorCode.ORACLE_POST_SQL_ERROR,"执行的SQL为:"+querySql+" 具体错误信息为：" +e);
        }
        throw TransferException.as(DBUtilErrorCode.READ_RECORD_FAIL,"执行的SQL为:"+querySql+" 具体错误信息为："+e);
    }

    public static TransferException asInsertPriException(DataBaseType dataBaseType, String userName,String jdbcUrl){
        if (dataBaseType.equals(DataBaseType.MySql)){
            throw TransferException.as(DBUtilErrorCode.MYSQL_INSERT_ERROR, "用户名为:"+userName+" jdbcURL为："+jdbcUrl);
        }

        if (dataBaseType.equals(DataBaseType.Oracle)){
            throw TransferException.as(DBUtilErrorCode.ORACLE_INSERT_ERROR,"用户名为:"+userName+" jdbcURL为："+jdbcUrl);
        }
        throw TransferException.as(DBUtilErrorCode.NO_INSERT_PRIVILEGE,"用户名为:"+userName+" jdbcURL为："+jdbcUrl);
    }

    public static TransferException asDeletePriException(DataBaseType dataBaseType, String userName,String jdbcUrl){
        if (dataBaseType.equals(DataBaseType.MySql)){
            throw TransferException.as(DBUtilErrorCode.MYSQL_DELETE_ERROR, "用户名为:"+userName+" jdbcURL为："+jdbcUrl);
        }

        if (dataBaseType.equals(DataBaseType.Oracle)){
            throw TransferException.as(DBUtilErrorCode.ORACLE_DELETE_ERROR,"用户名为:"+userName+" jdbcURL为："+jdbcUrl);
        }
        throw TransferException.as(DBUtilErrorCode.NO_DELETE_PRIVILEGE,"用户名为:"+userName+" jdbcURL为："+jdbcUrl);
    }

    public static TransferException asSplitPKException(DataBaseType dataBaseType, Exception e,String splitSql,String splitPkID){
        if (dataBaseType.equals(DataBaseType.MySql)){

            return TransferException.as(DBUtilErrorCode.MYSQL_SPLIT_PK_ERROR,"配置的SplitPK为: "+splitPkID+", 执行的SQL为: "+splitSql+" 具体错误信息为："+e);
        }

        if (dataBaseType.equals(DataBaseType.Oracle)){
            return TransferException.as(DBUtilErrorCode.ORACLE_SPLIT_PK_ERROR,"配置的SplitPK为: "+splitPkID+", 执行的SQL为: "+splitSql+" 具体错误信息为："+e);
        }

        return TransferException.as(DBUtilErrorCode.READ_RECORD_FAIL,splitSql+e);
    }
}
