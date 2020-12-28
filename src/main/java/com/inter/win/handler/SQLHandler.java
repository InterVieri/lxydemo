package com.inter.win.handler;

import java.util.List;
import java.util.Map;

public interface SQLHandler {
    String getCreateSql(String key, List<Map<String, Map<String, String>>> value);

    String getAlertTableSql();

    String getCreateIndexSql(Map<String, String> params);

    String getAlertColumnSql(String key, Map<String, Map<String, String>> value);

    String getChangeTypeSql(String key, Map<String, Map<String, String>> value);
}
