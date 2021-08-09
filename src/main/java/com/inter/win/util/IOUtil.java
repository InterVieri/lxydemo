package com.inter.win.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;
@Slf4j
public class IOUtil {
    public static void main(String[] args) {

    }
    public static String upload(List<List<String>> list) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            String fileName = UUID.randomUUID().toString().replace("-", "");
            File file = new File(fileName + ".txt");
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (List<String> stringList : list) {
                for (String line : stringList) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            bw.flush();
            return fileName;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public static void download(String filename, HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        BufferedInputStream bis = null;
        try {
            File file = new File(filename + ".txt");
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            byte[] buff = new byte[1024];
            outputStream = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            while (i != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                i = bis.read(buff);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            try {
                outputStream.close();
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }

        }

    }
}
