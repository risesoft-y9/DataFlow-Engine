package net.risesoft.api.utils.jdbc;

public final class Constant {
    static final int TIMEOUT_SECONDS = 15;
    static final int MAX_TRY_TIMES = 4;
    static final int SOCKET_TIMEOUT_INSECOND = 172800;

    public static final String MYSQL_DATABASE = "Unknown database";
    public static final String MYSQL_CONNEXP = "Communications link failure";
    public static final String MYSQL_ACCDENIED = "Access denied";
    public static final String MYSQL_TABLE_NAME_ERR1 = "Table";
    public static final String MYSQL_TABLE_NAME_ERR2 = "doesn't exist";
    public static final String MYSQL_SELECT_PRI = "SELECT command denied to user";
    public static final String MYSQL_COLUMN1 = "Unknown column";
    public static final String MYSQL_COLUMN2 = "field list";
    public static final String MYSQL_WHERE = "where clause";

    public static final String ORACLE_DATABASE = "ORA-12505";
    public static final String ORACLE_CONNEXP = "The Network Adapter could not establish the connection";
    public static final String ORACLE_ACCDENIED = "ORA-01017";
    public static final String ORACLE_TABLE_NAME = "table or view does not exist";
    public static final String ORACLE_SELECT_PRI = "insufficient privileges";
    public static final String ORACLE_SQL = "invalid identifier";
    public static final int DEFAULT_BATCH_SIZE = 2048;

    public static final int DEFAULT_BATCH_BYTE_SIZE = 32 * 1024 * 1024;

    public static String TABLE_NAME_PLACEHOLDER = "@table";

    public static String CONN_MARK = "connection";

    public static String TABLE_NUMBER_MARK = "tableNumber";

    public static String INSERT_OR_REPLACE_TEMPLATE_MARK = "insertOrReplaceTemplate";

    public static final String OB10_SPLIT_STRING = "||_dsc_ob10_dsc_||";
    public static final String OB10_SPLIT_STRING_PATTERN = "\\|\\|_dsc_ob10_dsc_\\|\\|";


}
