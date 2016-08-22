package com.wd.erp.asbrpc.utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AresFileReader {
	
	private static int MAX_MSG_LEN = 1024 * 16;
	
    public static byte[] readFileByBytes(String fileName) {
        InputStream in = null;
        try {
            // 一次读多个字节
            byte[] tempbytes = new byte[MAX_MSG_LEN];
            int byteread = 0;
            in = new FileInputStream(fileName);
            int totalRead = 0;

            System.out.println("======================  file content   ===============");
            while ((byteread = in.read(tempbytes, totalRead, MAX_MSG_LEN - totalRead)) != -1) {
            	totalRead += byteread;
                System.out.write(tempbytes, 0 + totalRead, byteread);
            }
            System.out.println("======================  file content   ===============");
            return tempbytes;
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
        return null;
    }
}
