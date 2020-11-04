package com.inter.win.util;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OracleSqlUtil implements SQLUtil{
    @Override
    public String getCreateSql(String key, List<Map<String, Map<String, String>>> value) {
        StringBuilder sql = new StringBuilder();
        String pkSql = "alter table :1 add primary key (:2);" + System.getProperty("line.separator");
        StringBuilder commentSql = new StringBuilder();
        sql.append("create table ")
                .append(key).append(" (").append(System.getProperty("line.separator"));
        int i = 0;
        for (Map<String, Map<String, String>> map : value) {
            Set<Map.Entry<String, Map<String, String>>> entries = map.entrySet();
            String columnName = "";
            String valueType = "";
            String dbType = "";
            String notNull = "";
            String comment = "";
            String length = "";
            for (Map.Entry<String, Map<String, String>> entry : entries) {
                columnName = entry.getKey();
                Map<String, String> valueMap = entry.getValue();
                valueType = valueMap.get("valueType");
                length = valueMap.get("length");
                dbType = TypeUtil.getDbType(valueType, Constants.ORACLE_DRIVER);
                if (!StringUtils.isEmpty(valueMap.get("null")) && "Y".equals(valueMap.get("null"))) {
                    notNull = "not null";
                }
                // 拼接comment
                if (valueMap.containsKey("comment")) {
                    comment = valueMap.get("comment");
                }
                // 生成主键生成
                if (valueMap.containsKey("key") && Constants.TRUE.equals(valueMap.get("key"))) {
                    pkSql = pkSql.replace(":1", key);
                    pkSql = pkSql.replace(":2", entry.getKey());
                }
            }
            sql.append(columnName).append("\t");
            sql.append(dbType);
            if (!dbType.equals("CLOB") && !dbType.equals("DATE")) {
                sql.append("(").append(length).append(")");
            }
            sql.append(" ");
            sql.append(notNull);
            if (i == value.size() - 1) {
                sql.append(")").append(System.getProperty("line.separator")).append(";").append(System.getProperty("line.separator"));
            } else {
                sql.append(",").append(System.getProperty("line.separator"));
            }
            commentSql.append("comment on column ")
                    .append(key)
                    .append(".")
                    .append(columnName)
                    .append(" is '")
                    .append(comment)
                    .append("';")
                    .append(System.getProperty("line.separator"));
            i ++;
        }
        sql.append(commentSql).append(pkSql);
        return sql.toString();
    }

    @Override
    public String getAlertTableSql() {
        return null;
    }

    @Override
    public String getCreateIndexSql(Map<String, String> params) {
        Set<Map.Entry<String, String>> entries = params.entrySet();
        String tableName = params.get("tableName");
        String indexName = params.get("indexName");
        String columnName = params.get("columnName");
        String nonUnique = params.get("nonUnique");
        String sql = "";
        if ("0".equals(nonUnique)) {
            sql = "ALTER TABLE " + tableName  + " ADD CHECK ( " + columnName + " IS NOT NULL);";
        } else {
            sql = "create index " + indexName + " on " + tableName + "(" + columnName + ");";
        }
        return sql;
    }

    @Override
    public String getAlertColumnSql(String key, Map<String, Map<String, String>> value) {
        StringBuilder alertSql = new StringBuilder();
        StringBuilder commentSql = new StringBuilder();
        alertSql.append("alter table ")
                .append(key).append(" add (");
        String columnName = "";
        String valueType = "";
        String dbType = "";
        String notNull = "";
        String comment = "";
        String length = "";
        Set<Map.Entry<String, Map<String, String>>> entries = value.entrySet();
        for (Map.Entry<String, Map<String, String>> mapEntry : entries) {
            columnName = mapEntry.getKey();
            Map<String, String> valueMap = mapEntry.getValue();
            valueType = valueMap.get("valueType");
            dbType = TypeUtil.getDbType(valueType, Constants.ORACLE_DRIVER);
            length = valueMap.get("length");
            if (!StringUtils.isEmpty(valueMap.get("null")) && "Y".equals(valueMap.get("null"))) {
                notNull = "not null";
            }
            // 拼接comment
            if (valueMap.containsKey("comment")) {
                comment = valueMap.get("comment");
            }
        }
        alertSql.append(columnName)
                .append(" ")
                .append(dbType);
        if (!dbType.equals("CLOB") && !dbType.equals("DATE")) {
            alertSql.append("(").append(length).append(") ");
        }
        alertSql.append("default '' ")
                .append(notNull)
                .append(");")
                .append(System.getProperty("line.separator"));
        commentSql.append("comment on column ")
                .append(key)
                .append(".")
                .append(columnName)
                .append(" is '")
                .append(comment)
                .append("';")
                .append(System.getProperty("line.separator"));
        alertSql.append(commentSql);

        return alertSql.toString();
    }


}
