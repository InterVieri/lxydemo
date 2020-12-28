package com.inter.win.handler;

import com.inter.win.util.Constants;

public class SQLHandlerFactory {
    public static SQLHandler getInstance(String driver) {
        SQLHandler sqlUtil = null;
        switch (driver) {
            case Constants.ORACLE_DRIVER:
                sqlUtil = new OracleSQLHandler();
                break;
            case Constants.MYSQL_DRIVER:
                sqlUtil = new MysqlSQLHandler();
                break;
            default:
                break;
        }
        return sqlUtil;
    }
}
