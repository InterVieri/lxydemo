package com.inter.win.util;

import java.io.*;

public class GitUtil {
    public static void main(String[] args) throws Exception {
        convertGitConfig(METADATA_URL);
        changeFile(METADATA_URL);
        convertGitConfig(STAND_URL);
        changeFile(STAND_URL);
        convertGitConfig(QA_URL);
        changeFile(QA_URL);
    }
    private static final String STAND_URL = "E:\\java\\project\\yusys-standard-sc\\.git\\config";
    private static final String QA_URL = "E:\\java\\project\\yusys-dataqa-sc\\.git\\config";
    private static final String METADATA_URL = "E:\\java\\project\\yusys-metadata-sc\\.git\\config";
    // r	以只读的方式打开文本，也就意味着不能用write来操作文件
    // rw	读操作和写操作都是允许的
    // rws	每当进行写操作，同步的刷新到磁盘，刷新内容和元数据
    // rwd	每当进行写操作，同步的刷新到磁盘，刷新内容
    private static final String MODE = "rw";
    public static void changeFile(String fileName) {
        File oldFile = new File(fileName);
        if (oldFile.exists()) {
            oldFile.delete();
        }
        File newFile = new File(fileName + "temp");
        if (newFile.exists()) {
            newFile.renameTo(oldFile);
        }
    }
    public static void convertGitConfig(String fileName) throws Exception {
        RandomAccessFile randomAccessFile = null;
        BufferedWriter br = null;
        try {
            randomAccessFile = new RandomAccessFile(fileName, MODE);
            File file = new File(fileName + "temp");
            if (file.exists()) {
                file.createNewFile();
            }
            br = new BufferedWriter(new FileWriter(file));
            String line = null;
            while ((line = randomAccessFile.readLine()) != null) {
                String content = line;
                if (line.contains("url") && !line.contains("#")) {
                    content = line.replace("\t", "");
                    content = "\t#" + content;
                }
                if (line.contains("#url")) {
                    content = line.replace("\t", "");
                    content = content.replace("#", "");
                    content = "\t" + content;
                }
                System.out.println(line);
                br.write(content);
                br.newLine();
            }
        } finally {
            br.close();
            randomAccessFile.close();
        }

    }

}
