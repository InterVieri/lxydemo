package com.inter.win.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ColumnUtil {
    public static List<List<String>> compareColumns(Map<String, List<Map<String, Map<String, String>>>> targetConnColumenMap, Map<String, List<Map<String, Map<String, String>>>> sourceConnColumenMap, String targetDatabaseDriver) {
        SQLUtil sqlUtil = SQLUtilFactory.getInstance(targetDatabaseDriver);
        List<List<String>> allList = new ArrayList<>();
        List<String> tableList = new ArrayList<>();
        List<String> columnList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> tableSqlList = new ArrayList<>();
        List<String> columnSqlList = new ArrayList<>();
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
                            String alertSql = sqlUtil.getAlertColumnSql(entry.getKey(), sColValueMap);
                            columnSqlList.add(alertSql);
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
                List<Map<String, Map<String, String>>> value = entry.getValue();
                String createSql = sqlUtil.getCreateSql(entry.getKey(), value);
                tableSqlList.add(createSql);
            }
        }
        allList.add(tableList);
        allList.add(columnList);
        allList.add(typeList);
        allList.add(tableSqlList);
        allList.add(columnSqlList);
        return allList;
    }
    public static Map<String, List<Map<String, Map<String, String>>>>[] getColumns(String[] targetConn, String[]... sourceConns) throws Exception {
        System.out.println("开始获取字段===>");
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
            databaseType = TypeUtil.getDatabaseType(targetConn[1]);
            if (databaseType == 1) {
                metaData = conn.getMetaData();
                tables = metaData.getTables(null, null, "%", null);
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    ConnUtil.putMap(conn, rs, tMap, tableName, targetConnColumenMap, databaseType);
                }
            } else {
                String createSql = "select table_name from all_tables a where a.OWNER = upper('"+targetConn[2]+"')";
                PreparedStatement stmt = conn.prepareStatement(createSql);
                ResultSet tableRs = stmt.executeQuery(createSql);
                while (tableRs.next()) {
                    String tableName = tableRs.getString("table_name");
                    ConnUtil.putMap(conn, rs, tMap, tableName, targetConnColumenMap, databaseType);
                }
            }
            for (String[] sourceConn : sourceConns) {
                conn = DBUtil.conn(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
                databaseType = TypeUtil.getDatabaseType(sourceConn[1]);
                if (databaseType == 1) {
                    metaData = conn.getMetaData();
                    tables = metaData.getTables(null, null, "%", null);
                    while (tables.next()) {
                        String tableName = tables.getString("TABLE_NAME");
                        ConnUtil.putMap(conn, rs, sMap, tableName, sourceConnColumenMap, databaseType);
                    }
                } else {
                    String createSql = "select table_name from all_tables a where a.OWNER = upper('"+targetConn[2]+"')";
                    PreparedStatement stmt = conn.prepareStatement(createSql);
                    ResultSet tableRs = stmt.executeQuery(createSql);
                    while (tableRs.next()) {
                        String tableName = tableRs.getString("table_name");
                        ConnUtil.putMap(conn, rs, sMap, tableName, sourceConnColumenMap, databaseType);
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
}
