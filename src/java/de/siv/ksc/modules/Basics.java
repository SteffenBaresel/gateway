/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
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
        //BufferedInputStream in = new BufferedInputStream(new FileInputStream("C:\\config\\gateway.properties"));
        //props.load(in);
        //in.close(); 
        return props;
    }
    
    /**
     *
     * @param desc
     * @return
     */
    static public String encodeHtml(String desc) {
        String replace = desc.replace("\\256", "&reg;").replace("\\303\\234", "&Uuml;").replace("\\303\\274", "&uuml;").replace("\\304", "&Auml;").replace("\\303\\204", "&Auml;").replace("\\344", "&auml;").replace("\\303\\244", "&auml;").replace("\\326", "&Ouml;").replace("\\303\\226", "&Ouml;").replace("\\366", "&ouml;").replace("\\303\\266", "&ouml;").replace("\\334", "&Uuml;").replace("\\374", "&uuml;").replace("\\337", "&szlig;").replace("\\303\\237", "&szlig;").replace("\\012", "<br>").replace("\\302\\264", "&acute;").replace("\\011", "").replace("\\342\\200\\223", "-").replace("\"", "&quot;");
        return replace;
    }
    
    static public String encodePdf(String desc) {
        String replace = desc.replace("\\256", "&reg;").replace("\\303\\234", "Ü").replace("\\303\\274", "ü").replace("\\304", "Ä").replace("\\303\\204", "Ä").replace("\\344", "ä").replace("\\303\\244", "ä").replace("\\326", "Ö").replace("\\303\\226", "Ö").replace("\\366", "ö").replace("\\303\\266", "ö").replace("\\334", "Ü").replace("\\374", "ü").replace("\\337", "ß").replace("\\303\\237", "ß").replace("\\012", "\n").replace("\\302\\264", "&acute;").replace("\\011", "").replace("\\342\\200\\223", "-").replace("<br>", "\n").replace("\\015", "");
        return replace;
    }
    
    static public String encodePdfReport(String desc) {
        String replace = desc.replace("\\256", "&reg;").replace("\\303\\234", "Ue").replace("\\303\\274", "ue").replace("\\304", "Ae").replace("\\303\\204", "Ae").replace("\\344", "ae").replace("\\303\\244", "ae").replace("\\326", "Oe").replace("\\303\\226", "Oe").replace("\\366", "oe").replace("\\303\\266", "oe").replace("\\334", "Ue").replace("\\374", "ue").replace("\\337", "ss").replace("\\303\\237", "ss").replace("\\012", "_").replace("\\302\\264", "&acute;").replace("\\011", "").replace("\\342\\200\\223", "-").replace("<br>", "\n").replace("\\015", "").replaceAll("\\s+", "_");
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
    
    static public String LongConvertUtime(Long utime) throws FileNotFoundException, IOException
    {
        Date date = new Date(utime*1000L); // *1000 is to convert minutes to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // the format of your date
        String formattedDate = sdf.format(date);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        return formattedDate;
    }
    
    static public String ConvertDate(String date) throws FileNotFoundException, IOException, ParseException
    {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        Long utime = sdf.parse(date).getTime();
        utime = utime/1000;
        return utime.toString();
    }
    
    static public Long LongConvertDate(String date) throws FileNotFoundException, IOException, ParseException
    {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        Long utime = sdf.parse(date).getTime();
        utime = utime/1000;
        return utime;
    }
    
    static public Long ReportLongConvertDate(String date) throws FileNotFoundException, IOException, ParseException
    {
        //DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // the format of your date
        Long utime = sdf.parse(date).getTime();
        utime = utime/1000;
        return utime;
    }
    
    static public Long CronConvertDate(String date) throws FileNotFoundException, IOException, ParseException
    {
        DateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss ZZZ yyyy", Locale.ENGLISH); // the format of your date
        Long utime = sdf.parse(date).getTime();
        utime = utime/1000;
        return utime;
    }
    
    static public String UrlDescape(String s) { 
        String replace = s.replace("%3A",":").replace("%3D","=").replace("+"," ").replace("%20"," ").replace("%22","\"").replace("%23","#").replace("%25","%").replace("%3C","<").replace("%3E",">").replace("%5B","[").replace("%5C","\\").replace("%5D","]").replace("%5E","^").replace("%60","`").replace("%7B","{").replace("%7C","|").replace("%7D","}").replace("%7E","~").replace("%7F","").replace("%28","(").replace("%29",")").replace("%2B","+");
        return replace;
    }
    
    static public String InterpreteDate(String s) throws FileNotFoundException, IOException {
        String out="";
        if (s.matches("^((20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
            /* Format yyyy-mm-dd*/
            out = s;
        } else if (s.matches("^(-[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?d)")) {
            /* Format -31d,-1d,-10000d */
            Long i = Long.parseLong(s.substring(1, s.length()-1));
            Long ms = i*24*60*60;
            Long c = System.currentTimeMillis()/1000;
            out = LongConvertUtime(c-ms);
        } else if (s.matches("^\\.")) {
            /* Format . = today */
            out = LongConvertUtime(System.currentTimeMillis()/1000);
        } else if (s.matches("^([0-9]?[0-9]?[0-9]?[0-9]?[0-9]?d)")) {
            /* Format 31d,1d,10000d */
            Long i = Long.parseLong(s.substring(1, s.length()-1));
            Long ms = i*24*60*60;
            Long c = System.currentTimeMillis()/1000;
            out = LongConvertUtime(ms+c);
        } else {
            /* Date Format yyyy-mm-dd*/
            out = s;
        }
        return out;
    }
    
    static public String InterpreteTags(String s) throws FileNotFoundException, IOException {
        /* Month */
        Long utime = System.currentTimeMillis()/1000;
        Date date = new Date(utime*1000L); // *1000 is to convert minutes to milliseconds
        SimpleDateFormat y = new SimpleDateFormat("yyyy"); // the format of your date
        SimpleDateFormat m = new SimpleDateFormat("MM"); // the format of your date
        SimpleDateFormat d = new SimpleDateFormat("dd"); // the format of your date
        
        String year = y.format(date);
        String month = m.format(date);
        String day = d.format(date);
        
        y.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        m.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        d.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        
        /**/ 
        String out = s.replaceAll("\\(mm\\)", month).replaceAll("\\(dd\\)", day).replaceAll("\\(yyyy\\)", year);
        return out;
    }
}
