package com.inter.win.handler;

import java.util.List;
import java.util.Map;

public class MysqlSQLHandler implements SQLHandler {
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

    @Override
    public String getChangeTypeSql(String key, Map<String, Map<String, String>> value) {
        return null;
    }

}
