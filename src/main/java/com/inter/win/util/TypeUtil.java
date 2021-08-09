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
            case "NUMBER":
            case "INT":
            case "SMALLINT":
            case "TINYINT":
            case "INTEGER":
            case "BIGINT":
                return "2";
            case "DATETIME":
            case "TIMESTAMP":
            case "DATE":
                return "7";
            case "TEXT":
            case "CLOB":
            case "LONGVARCHAR":
            case "LONGTEXT":
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
    public static int getDatabaseType(String url) {
        if (url.contains("mysql")) {
            return 1;
        } else {
            return 2;
        }
    }
    public static String getAdapter(String type) {
        return type.toLowerCase().equals("mysql") ? Constants.MYSQL_DRIVER : Constants.ORACLE_DRIVER;
    }

    public static String convertOracleNumberType2Mysql(String length) {
        String[] arr = length.split(",");
        if ("0".equals(arr[1])) {
            return "INT";
        } else {
            return "DOUBLE";
        }

    }
}
