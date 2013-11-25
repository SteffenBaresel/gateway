/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.modules;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

/**
 *
 * @author sbaresel
 */
public class Basics {
    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    static public Properties getConfiguration() throws FileNotFoundException, IOException {
        Properties props;
        props = new Properties();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream("C:\\config\\gateway.properties"));
        props.load(in);
        in.close(); 
        return props;
    }
    
    /**
     *
     * @param desc
     * @return
     */
    static public String encodeHtml(String desc) {
        String replace = desc.replace("\\303\\234", "&Uuml;").replace("\\303\\274", "&uuml;").replace("\\304", "&Auml;").replace("\\344", "&auml;").replace("\\326", "&Ouml;").replace("\\366", "&ouml;").replace("\\334", "&Uuml;").replace("\\374", "&uuml;").replace("\\337", "&szlig;");
        return replace;
    }
    
    static public String readFile(String filename) throws FileNotFoundException, IOException
    {
        String content = null;
        File file = new File(filename); //for ex foo.txt
        FileReader reader = new FileReader(file);
        char[] chars = new char[(int) file.length()];
        reader.read(chars);
        content = new String(chars);
        reader.close();
        return content;
    }
    
    static public String ConvertUtime(Long utime) throws FileNotFoundException, IOException
    {
        Date date = new Date(utime*1000L); // *1000 is to convert minutes to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        String formattedDate = sdf.format(date);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        return formattedDate;
    }
}
