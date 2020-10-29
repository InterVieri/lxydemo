package com.inter.win.util;

public class SQLUtilFactory {
    public static SQLUtil getInstance(String driver) {
        SQLUtil sqlUtil = null;
        switch (driver) {
            case Constants.ORACLE_DRIVER:
                sqlUtil = new OracleSqlUtil();
                break;
            case Constants.MYSQL_DRIVER:
                sqlUtil = new MysqlSqlUtil();
                break;
            default:
                break;
        }
        return sqlUtil;
    }
}
