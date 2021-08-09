package com.inter.win.resolver;

import com.inter.win.util.Constants;
import com.inter.win.util.TypeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Component
@PropertySource(value = {"classpath:sql.properties"})
public class Resolver {
    @Value("${columns-sql.mysql}")
    private String mysqlSql;
    @Value("${columns-sql.oracle}")
    private String oracleSql;
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
            sql = mysqlSql;
            sql = sql.replace("%tables%", tableNamesStr);
            sql = sql.replace("%schema%", schema);
        } else {
            DatabaseMetaData metaData = conn.getMetaData();
            sql = oracleSql;
            sql = sql.replace("%tables%", tableNamesStr);
            sql = sql.replace("%owner%", metaData.getUserName());
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
                if (dbType.toUpperCase().equals("NUMBER")) {
                    length = rs.getString("DATA_PRECISION") + "," + rs.getString("DATA_SCALE");
                } else {
                    length = rs.getString("DATA_LENGTH");
                }
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

    public static void main(String[] args) {
        String sql = "SELECT DISTINCT U.DATA_LENGTH,U.TABLE_NAME,U.column_name,U.DATA_TYPE AS TYPE_NAME,U.NULLABLE,pk_table.KEY_NAME AS COLUMN_KEY,t.COMMENTS FROM ALL_COL_COMMENTS t, ALL_TAB_COLS U LEFT JOIN (SELECT cu.COLUMN_NAME AS KEY_NAME FROM user_cons_columns cu, user_constraints au WHERE cu.constraint_name = au.constraint_name AND au.OWNER = ''%owner%''AND au.constraint_type = 'P' AND au.TABLE_NAME IN (%tables%)) pk_table ON U.COLUMN_NAME = pk_table.KEY_NAME WHERE t.TABLE_NAME = U.TABLE_NAME AND t.COLUMN_NAME = U.QUALIFIED_COL_NAME AND U.OWNER = t.OWNER AND U.OWNER = '%owner%' AND U.TABLE_NAME IN (%tables%)";
        sql = sql.replace("%tables%", "aaa");
        sql = sql.replace("%owner%", "bbb");
        System.out.println(sql);
    }
}
