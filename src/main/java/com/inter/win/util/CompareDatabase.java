package com.inter.win.util;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class CompareDatabase {
    public static void main(String[] args) {
        String[] targetConn = {"oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@172.16.60.20:1521:zhpoc", "dataman_sc", "dataman_sc"};
        String[] sourceConn1 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/metadata", "root", "root"};
        String[] sourceConn2 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/data_standard", "root", "root"};
        String[] sourceConn3 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/data_qa_new", "root", "root"};
        String[] sourceConn4 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/metadata_common_admin", "root", "root"};
        try {
            Map<String, List<Map<String, Map<String, String>>>>[] maps = getColumns(targetConn, sourceConn1, sourceConn2, sourceConn3, sourceConn4);
            List<List<String>> lists = compareColumns(maps[0], maps[1]);
            System.out.println(lists);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<List<String>> compareColumns(Map<String, List<Map<String, Map<String, String>>>> targetConnColumenMap, Map<String, List<Map<String, Map<String, String>>>> sourceConnColumenMap) {
        List<List<String>> allList = new ArrayList<>();
        List<String> tableList = new ArrayList<>();
        List<String> columnList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        Set<Map.Entry<String, List<Map<String, Map<String, String>>>>> entries = sourceConnColumenMap.entrySet();
        for (Map.Entry<String, List<Map<String, Map<String, String>>>> entry : entries) {
            if (targetConnColumenMap.containsKey(entry.getKey())) {
                List<Map<String, Map<String, String>>> sValue = entry.getValue();
                List<Map<String, Map<String, String>>> tValue = targetConnColumenMap.get(entry.getKey());
                for (Map<String, Map<String, String>> sColValueMap : sValue) {
                    Set<Map.Entry<String, Map<String, String>>> sColValueMapEntries = sColValueMap.entrySet();
                    for (Map.Entry<String, Map<String, String>> sColValueMapEntry : sColValueMapEntries) {

                        List<Map<String, Map<String, String>>> collect = tValue.stream().filter(u -> u.containsKey(sColValueMapEntry.getKey())).collect(Collectors.toList());
                        if (collect.isEmpty()) {
                            columnList.add("目标库表" + entry.getKey() + "缺少字段" + sColValueMapEntry.getKey());
                        } else {
                            Map<String, String> value1 = sColValueMapEntry.getValue();
                            Map<String, String> value2 = collect.get(0).get(sColValueMapEntry.getKey());
                            String valueType1 = value1.get("valueType");
                            String valueType2 = value2.get("valueType");
                            if (!valueType1.toUpperCase().equals(valueType2.toUpperCase())) {
                                typeList.add("目标库表" + entry.getKey() + "字段" + sColValueMapEntry.getKey() + "数据格式不一致！");
                            }
                        }
                    }
                }
            } else {
                tableList.add("目标库缺少表" + entry.getKey());
            }
        }
        allList.add(tableList);
        allList.add(columnList);
        allList.add(typeList);
        return allList;
    }


    public static Map<String, List<Map<String, Map<String, String>>>>[] getColumns(String[] targetConn, String[]... sourceConns) throws Exception {
        Map<String, List<Map<String, Map<String, String>>>> targetConnColumenMap = new ConcurrentHashMap<>();
        Map<String, List<Map<String, Map<String, String>>>> sourceConnColumenMap = new ConcurrentHashMap<>();
        Map<String, Map<String, String>> tMap = null;
        Map<String, Map<String, String>> sMap = null;
        Connection conn = null;
        ResultSet tables = null;
        ResultSet rs = null;
        DatabaseMetaData metaData = null;
        int databaseType = 0;
        try {
            conn = DBUtil.conn(targetConn[0], targetConn[1], targetConn[2], targetConn[3]);
            databaseType = getDatabaseType(targetConn[1]);
            if (databaseType == 1) {
                metaData = conn.getMetaData();
                tables = metaData.getTables(null, null, "%", null);
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    putMap(conn, rs, tMap, tableName, targetConnColumenMap, databaseType);
                }
            } else {
                String createSql = "select table_name from all_tables a where a.OWNER = upper('"+targetConn[2]+"')";
                PreparedStatement stmt = conn.prepareStatement(createSql);
                ResultSet tableRs = stmt.executeQuery(createSql);
                while (tableRs.next()) {
                    String tableName = tableRs.getString("table_name");
                    putMap(conn, rs, tMap, tableName, targetConnColumenMap, databaseType);
                }
            }
            for (String[] sourceConn : sourceConns) {
                conn = DBUtil.conn(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
                databaseType = getDatabaseType(sourceConn[1]);
                if (databaseType == 1) {
                    metaData = conn.getMetaData();
                    tables = metaData.getTables(null, null, "%", null);
                    while (tables.next()) {
                        String tableName = tables.getString("TABLE_NAME");
                        putMap(conn, rs, sMap, tableName, sourceConnColumenMap, databaseType);
                    }
                } else {
                    String createSql = "select table_name from all_tables a where a.OWNER = upper('"+targetConn[2]+"')";
                    PreparedStatement stmt = conn.prepareStatement(createSql);
                    ResultSet tableRs = stmt.executeQuery(createSql);
                    while (tableRs.next()) {
                        String tableName = tableRs.getString("table_name");
                        putMap(conn, rs, sMap, tableName, sourceConnColumenMap, databaseType);
                    }
                }
            }
        } finally {
            if (tables != null) {
                tables.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return new Map[]{targetConnColumenMap, sourceConnColumenMap};
    }

    public static List<String> compareTables(String[] targetConn, String[]... sourceConns) throws Exception {
        List<String> sourceTables = new ArrayList<>();
        List<String> targetTables = getTables(targetConn[0], targetConn[1], targetConn[2], targetConn[3]);
        for (String[] sourceConn : sourceConns) {
            List<String> tables = getTables(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
            sourceTables.addAll(tables);
        }
        sourceTables.removeAll(targetTables);
        return sourceTables;
    }

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

    private static String changeDbType(String dbType) {
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
            default:
                return "1";
        }
    }

    private static int getDatabaseType(String url) {
        if (url.contains("mysql")) {
            return 1;
        } else {
            return 2;
        }
    }

    private static void putMap(Connection conn, ResultSet rs, Map<String, Map<String, String>> tMap, String tableName, Map<String, List<Map<String, Map<String, String>>>> targetConnColumenMap, int database) throws Exception {
        String sql = "";
        if (database == 1) {
            sql = "SELECT distinct COLUMN_NAME, DATA_TYPE AS TYPE_NAME FROM information_schema.columns WHERE TABLE_NAME= '" + tableName + "'";
        } else {
            sql = "select distinct column_name,data_type AS TYPE_NAME from all_tab_columns  t where t.table_name = '" + tableName + "'";
        }
        List<Map<String, Map<String, String>>> list = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            tMap = new ConcurrentHashMap<>();
            Map<String, String> map = new HashMap<>();
            String colName = rs.getString("COLUMN_NAME");
            map.put("code", colName.toUpperCase());
            String dbType = rs.getString("TYPE_NAME");
            map.put("dbType", dbType);
            map.put("valueType", changeDbType(dbType));
            tMap.put(colName.toUpperCase(), map);
            list.add(tMap);
        }
        targetConnColumenMap.put(tableName.toUpperCase(), list);
    }
}
