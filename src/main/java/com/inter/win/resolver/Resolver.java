package com.inter.win.resolver;

import com.inter.win.util.Constants;
import com.inter.win.util.TypeUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class Resolver {

    public List<Map<String, String>> putIndexMap(Connection conn, ResultSet rs, String tableName, int database) throws SQLException {
        System.out.println("获取到表名称" + tableName);
        List<Map<String, String>> indexList = new ArrayList<>();
        String sql = "";
        if (database == 1) {
            String schema = conn.getCatalog();
            sql = "SHOW INDEX FROM " + schema + "." + tableName;
        } else {

        }
        PreparedStatement stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Map<String, String> result = new ConcurrentHashMap<>();
            String keyName = rs.getString("Key_name");
            if (!"PRIMARY".equals(keyName.toUpperCase())) {
                result.put("indexName", keyName);
                String index_type = rs.getString("Index_type");
                String column_name = rs.getString("Column_name");
                String non_unique = rs.getString("Non_unique");
                result.put("indexType", index_type);
                result.put("tableName", tableName);
                result.put("columnName", column_name);
                result.put("nonUnique", non_unique);
                indexList.add(result);
            }
        }
        return indexList;
    }

    public void putMap(Connection conn, ResultSet rs, Map<String, Map<String, String>> tMap, List<String> tableNames, Map<String, List<Map<String, Map<String, String>>>> targetConnColumenMap, int database) throws Exception {
        String sql = "";
        StringBuilder tableNamesBuilder = new StringBuilder();
        for (String t : tableNames) {
            tableNamesBuilder.append("'").append(t).append("',");
        }
        String tableNamesStr = tableNamesBuilder.toString().substring(0, tableNamesBuilder.length() - 1);
        if (database == 1) {
            String schema = conn.getCatalog();
            sql = "SELECT distinct CHARACTER_MAXIMUM_LENGTH,TABLE_NAME,NUMERIC_PRECISION,COLUMN_NAME,DATA_TYPE AS TYPE_NAME,COLUMN_COMMENT AS COMMENTS,IS_NULLABLE AS NULLABLE,COLUMN_KEY FROM information_schema.columns WHERE TABLE_NAME in (" + tableNamesStr + ") AND TABLE_SCHEMA = '" + schema + "'";
        } else {
            sql = "select distinct t.DATA_LENGTH,t.TABLE_NAME,t.column_name,t.data_type AS TYPE_NAME,t.NULLABLE,uc.comments,uc.comments AS COMMENTS,pk_table.KEY_NAME AS COLUMN_KEY from all_tab_columns t " +
                    "LEFT JOIN user_col_comments uc ON t.COLUMN_NAME = uc.COLUMN_NAME " +
                    "LEFT JOIN (select cu.COLUMN_NAME AS KEY_NAME from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and au.table_name in (" + tableNamesStr + ")" +
                    ")pk_table ON t.COLUMN_NAME = pk_table.KEY_NAME " +
                    "where t.table_name in (" + tableNamesStr + ") AND uc.table_name in (" + tableNamesStr + ")";
        }
        PreparedStatement stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            List<Map<String, Map<String, String>>> list = null;
            String tableName = rs.getString("TABLE_NAME");
            tableName = tableName.toUpperCase();
            if (targetConnColumenMap.containsKey(tableName)) {
                list = targetConnColumenMap.get(tableName);
            } else {
                list = new ArrayList<>();
            }
            tMap = new ConcurrentHashMap<>();
            Map<String, String> map = new HashMap<>();
            String colName = rs.getString("COLUMN_NAME");

            String column_key = rs.getString("COLUMN_KEY");
            if (!StringUtils.isEmpty(column_key)) {
                map.put("key", Constants.TRUE);
            } else {
                map.put("key", Constants.FASLE);
            }
            map.put("code", colName.toUpperCase());
            String dbType = rs.getString("TYPE_NAME");
            String length = "";
            if (database == 1) {
                if (dbType.toUpperCase().equals("VARCHAR")) {
                    length = rs.getString("CHARACTER_MAXIMUM_LENGTH");
                } else if ("INT".equals(dbType.toUpperCase()) || "TINYINT".equals(dbType.toUpperCase()) || "FLOAT".equals(dbType.toUpperCase())){
                    length = rs.getString("NUMERIC_PRECISION");
                }
            } else {
                length = rs.getString("DATA_LENGTH");
            }
            map.put("length", length);
            map.put("dbType", dbType);
            map.put("valueType", TypeUtil.changeDbType(dbType));
            map.put("comment", rs.getString("COMMENTS"));
            map.put("null", rs.getString("NULLABLE"));
            tMap.put(colName.toUpperCase(), map);
            list.add(tMap);
            targetConnColumenMap.put(tableName, list);
        }
    }
}
