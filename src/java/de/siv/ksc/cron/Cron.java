/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.cron;

import de.siv.ksc.modules.Basics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.quartz.CronExpression;

/**
 *
 * @author sbaresel
 */
public class Cron {
        
    static Properties props = null;
    
    static public void ParseCronFile(String text) throws FileNotFoundException, IOException, NamingException, SQLException, ParseException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Read Text Line By Line
         */
        
        String buffer;
        BufferedReader reader = new BufferedReader(new StringReader(text));
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        TruncateCronTable(cn);
        
        while ( (buffer = reader.readLine()) != null ) {
            if(buffer.length() > 0) {
                if ( !buffer.startsWith("#") ) {
                    //System.out.println(TranslateLine(buffer));
                    FillCronTable(buffer,cn);
                }
            }
        }
        
        cn.close();
        
    }
    
    static public void TruncateCronTable(Connection cn) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Truncate Cron Table
         */
        PreparedStatement ps = cn.prepareStatement("TRUNCATE TABLE cron_reporting");
        ps.executeUpdate(); 
        
    }
    
    static public void FillCronTable(String buffer,Connection cn) throws FileNotFoundException, IOException, NamingException, SQLException, ParseException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Split buffer in line parts
         */
        String[] line = buffer.split(" ");
        
        /*
         * Update Cron Table
         */
        
        String expression = line[0]+" "+ line[1] +" "+ line[2] +" "+ line[3] +" "+ line[4] +" "+ line[5] +" "+ line[6];
        //System.out.println(expression);
        CronExpression cronExpression = new CronExpression(expression);
        String dt = cronExpression.getNextValidTimeAfter(new Date()).toString();
        
        PreparedStatement ps = cn.prepareStatement("INSERT INTO cron_reporting(FUNCTION,DATESTART,INTERVALL,STATUS,CREATED) VALUES (?,?,?,?,?)");
        ps.setString(1,line[7]);
        ps.setLong(2,Basics.CronConvertDate(dt));
        ps.setString(3,dt);
        ps.setString(4, expression);
        ps.setLong(5,System.currentTimeMillis()/1000);
        ps.executeUpdate();
        
    }
    
}
