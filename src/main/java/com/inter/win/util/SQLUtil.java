package com.inter.win.util;

import java.util.List;
import java.util.Map;

public interface SQLUtil {
    String getCreateSql(String key, List<Map<String, Map<String, String>>> value);

    String getAlertTableSql();

    String getAlertColumnSql(String key, Map<String, Map<String, String>> value);
}
