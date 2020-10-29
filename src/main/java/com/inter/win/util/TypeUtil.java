package com.inter.win.util;

public class TypeUtil {
    public static String changeDbType(String dbType) {
        dbType = dbType.toUpperCase();
        switch (dbType) {
            case "VARCHAR":
            case "VARCHAR2":
            case "CHAR":
                return "1";
            case "DECIMAL":
                return "4";
            case "NUMBER":
            case "INT":
            case "SMALLINT":
            case "TINYINT":
            case "INTEGER":
                return "2";
            case "BIGINT":
                return "6";
            case "DATETIME":
            case "TIMESTAMP":
            case "DATE":
                return "7";
            case "TEXT":
            case "CLOB":
                return "8";
            default:
                return "1";
        }
    }

    public static String getDbType(String type, String driver) {
        String dbType = "CHAR";
        switch (driver) {
            case Constants.ORACLE_DRIVER:
                dbType = getOracleType(type);
                break;
            case Constants.MYSQL_DRIVER:
                dbType = getMySqlType(type);
                break;
            default:
                break;
        }
        return dbType;
    }
    private static String getOracleType(String type) {
        switch (type) {
            case "1":
                return "VARCHAR2";
            case "4":
                return "DECIMAL";
            case "2":
                return "NUMBER";
            case "6":
                return "BIGINT";
            case "7":
                return "DATE";
            case "8":
                return "CLOB";
            default:
                return "1";
        }
    }
    private static String getMySqlType(String type) {
        switch (type) {
            case "1":
                return "VARCHAR";
            case "4":
                return "DECIMAL";
            case "2":
                return "INT";
            case "6":
                return "BIGINT";
            case "7":
                return "DATETIME";
            case "8":
                return "TEXT";
            default:
                return "1";
        }
    }
}