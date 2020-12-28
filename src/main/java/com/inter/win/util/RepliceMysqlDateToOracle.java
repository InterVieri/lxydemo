package com.inter.win.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class RepliceMysqlDateToOracle {
    public static void main(String[] args) {
        replice("C:\\Users\\admin\\Desktop\\111.sql");
    }
    public static void replice(String filePath){
        File file = new File(filePath);
        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            if (file.exists()) {
                fileReader = new FileReader(file);
                br = new BufferedReader(fileReader);
                int nullNum = 0;
                while (nullNum < 5) {
                    String s = br.readLine();
                    if (s == null || "".equals(s)) {
                        nullNum ++;
                    } else {
                        List<String> matcherStrs = MatcherAssist.getMatcherStrs(s, "([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])");
                        List<String> dates = MatcherAssist.getDates(s, "-");
                        for (int i = 0; i < matcherStrs.size(); i++) {
                            String dateTime = "'" + dates.get(i) + " " + matcherStrs.get(i) + "'";
                            s = s.replace(dateTime, "to_date(" + dateTime + ", 'yyyy/mm/dd HH24:MI:SS')");
                            s = s.replace("to_date(to_date(" + dateTime +  ", 'yyyy/mm/dd HH24:MI:SS'), 'yyyy/mm/dd HH24:MI:SS')", "to_date(" + dateTime + ", 'yyyy/mm/dd HH24:MI:SS')");
                        }
                        System.out.println(s);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
