package com.inter.win.util;

import java.util.List;
import java.util.Map;

public class MysqlSqlUtil implements SQLUtil{
    @Override
    public String getCreateSql(String key, List<Map<String, Map<String, String>>> value) {
        return null;
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

}
