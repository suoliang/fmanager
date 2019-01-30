package com.cyyun.fm.newspath;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileManager
{

    public static String read(String fileName, String encoding)
    {
        StringBuffer fileContent = new StringBuffer();
        try
        {
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
            {
                fileContent.append(line);
                fileContent.append(System.getProperty("line.separator"));
            }
            br.close();
            fis.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    public static void write(String fileContent, String fileName, String encoding, boolean flag)
    {
        try
        {
            String name = System.getProperty("user.dir") + "/" + fileName;
            FileOutputStream fos = new FileOutputStream(name, flag);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
            osw.write(fileContent);
            osw.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
/*
 * 　　调用示例：
 写入 FileManager.write("Hello, World!", "D:\\test.txt", "UTF-8");
 读取 System.out.println(FileManager.read("D:\\test.txt", "UTF-8"));
 */
