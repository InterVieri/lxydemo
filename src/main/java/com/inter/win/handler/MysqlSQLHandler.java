package com.inter.win.handler;

import com.inter.win.util.Constants;
import com.inter.win.util.TypeUtil;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MysqlSQLHandler implements SQLHandler {
    @Override
    public String getCreateSql(String key, List<Map<String, Map<String, String>>> value) {
        StringBuilder sql = new StringBuilder();
        sql.append("create table ")
                .append(key).append(" ( ").append(System.getProperty("line.separator"));
        int i = 0;
        String pk = "";
        for (Map<String, Map<String, String>> map : value) {
            Set<Map.Entry<String, Map<String, String>>> entries = map.entrySet();
            String columnName = "";
            String valueType = "";
            String dbType = "";
            String notNull = "null";
            String comment = "";
            String length = "";
            for (Map.Entry<String, Map<String, String>> entry : entries) {
                columnName = entry.getKey();
                Map<String, String> valueMap = entry.getValue();
                valueType = valueMap.get("valueType");
                length = valueMap.get("length");
                String type = valueMap.get("dbType");
                if ("NUMBER".equals(type.toUpperCase())) {
                    dbType = TypeUtil.convertOracleNumberType2Mysql(length);
                } else {
                    dbType = TypeUtil.getDbType(valueType, Constants.MYSQL_DRIVER);
                }

                if (!StringUtils.isEmpty(valueMap.get("null")) && "N".equals(valueMap.get("null"))) {
                    notNull = "not null";
                }
                // 拼接comment
                if (valueMap.containsKey("comment")) {
                    comment = valueMap.get("comment");
                }
                // 生成主键生成
                if (valueMap.containsKey("key") && Constants.TRUE.equals(valueMap.get("key"))) {
                    pk = entry.getKey();
                }
            }
            sql.append(columnName).append("\t");
            sql.append(dbType);
            if (!dbType.equals("TEXT") && !dbType.equals("DATE") && !dbType.equals("DATETIME") && !dbType.equals("INT")) {
                sql.append("(").append(length).append(")");
            }
            sql.append(" ");
            sql.append(notNull);
            sql.append(" comment '").append(comment).append("' ");
            sql.append(",").append(System.getProperty("line.separator"));
            if (i == value.size() - 1) {
                sql.append("PRIMARY KEY (").append(pk).append(")");
                sql.append(")").append(System.getProperty("line.separator")).append(";").append(System.getProperty("line.separator"));
            }
            i ++;
        }
        return sql.toString();
    }

    @Override
    public String getAlertTableSql() {
        return null;
    }

    @Override
    public String getCreateIndexSql(Map<String, String> params) {
        return null;
    }

    @Override
    public String getAlertColumnSql(String key, Map<String, Map<String, String>> value) {
        return null;
    }

    @Override
    public String getChangeTypeSql(String key, Map<String, Map<String, String>> value) {
        return null;
    }

}
