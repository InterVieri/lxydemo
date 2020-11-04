package com.inter.win.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TableUtil {
    public static List<String> getTables(String driver, String url, String user, String pwd) throws Exception {
        List<String> tableNameList = new ArrayList<>();
        Connection conn = null;
        ResultSet tables = null;
        try {
            conn = DBUtil.conn(driver, url, user, pwd);
            DatabaseMetaData metaData = conn.getMetaData();
            tables = metaData.getTables(null, null, "%", null);
            while (tables.next()) {
                tableNameList.add(tables.getString("TABLE_NAME"));
            }
        } finally {
            if (tables != null) {
                tables.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return tableNameList;
    }
    public static List<String> compareTables(String[] targetConn, String[]... sourceConns) throws Exception {
        List<String> sourceTables = new ArrayList<>();
        List<String> targetTables = TableUtil.getTables(targetConn[0], targetConn[1], targetConn[2], targetConn[3]);
        for (String[] sourceConn : sourceConns) {
            List<String> tables = TableUtil.getTables(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
            sourceTables.addAll(tables);
        }
        sourceTables.removeAll(targetTables);
        return sourceTables;
    }
}
