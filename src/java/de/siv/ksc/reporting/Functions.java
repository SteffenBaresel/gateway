/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.reporting;

import de.siv.ksc.cron.Cron;
import de.siv.ksc.modules.Base64Coder;
import de.siv.ksc.modules.Basics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author sbaresel
 */
public class Functions {
    
    static Properties props = null;
    
    static public String GetLastPageComment(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Get User Roles
         */
        
        /*String sor = "";
        ResultSet rsUro = de.siv.ksc.modules.Cron.GetUserRoles(Uid);
        while(rsUro.next()) {
            sor+= "e.rlid=" + rsUro.getString( 1 ) + " or ";
        }
        sor = sor.substring(0, sor.length()-4);*/
        
        /*
         * Get User Roles Ende
         */
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        String sqlGC = "SELECT DECODE(VALTEXT,'base64') FROM config_reporting WHERE KEY=encode('LastPageComment','base64')";
        PreparedStatement ps = cn.prepareStatement(sqlGC);
        ResultSet rs = ps.executeQuery();
        
        String out = "";
        if (rs.next()) {
            if (rs.getString(1).isEmpty()) {
                out = "{\"COMMENT\":\"" + Base64Coder.encodeString("Es wurde noch keine Bemerkung hinterlegt.") + "\"}";
            } else {
                out = "{\"COMMENT\":\"" + Base64Coder.encodeString(Basics.encodePdf(rs.getString(1))) + "\"}";
            }
        }
        
        cn.close();
        return out;
    }
    
    static public void UpdateConfigReporting(String key, String val) throws FileNotFoundException, IOException, NamingException, SQLException, ParseException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement psD = cn.prepareStatement("UPDATE config_reporting SET VALTEXT=? WHERE KEY=?");
        psD.setString(1,val.replace("78", "+"));
        psD.setString(2,key);
        psD.executeUpdate(); 
        /*
         * Close Connection
         */
        cn.close();
        
        if ("Cron".equals(Base64Coder.decodeString(key.replace("78", "+")))) {
            Cron.ParseCronFile(Base64Coder.decodeString(val.replace("78", "+")));
        }
    }
    
    static public String GetLastPageContactsComment(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Get User Roles
         */
        
        /*String sor = "";
        ResultSet rsUro = de.siv.ksc.modules.Cron.GetUserRoles(Uid);
        while(rsUro.next()) {
            sor+= "e.rlid=" + rsUro.getString( 1 ) + " or ";
        }
        sor = sor.substring(0, sor.length()-4);*/
        
        /*
         * Get User Roles Ende
         */
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        String sqlGC = "SELECT DECODE(VALTEXT,'base64') FROM config_reporting WHERE KEY=encode('LastPageContactsComment','base64')";
        PreparedStatement ps = cn.prepareStatement(sqlGC);
        ResultSet rs = ps.executeQuery();
        
        String out = "";
        if (rs.next()) {
            if (rs.getString(1).isEmpty()) {
                out = "{\"COMMENT\":\"" + Base64Coder.encodeString("Es wurde noch keine Bemerkung hinterlegt.") + "\"}";
            } else {
                out = "{\"COMMENT\":\"" + Base64Coder.encodeString(Basics.encodePdf(rs.getString(1))) + "\"}";
            }
        }
        
        cn.close();
        return out;
    }
    
    static public String GetCron(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Get User Roles
         */
        
        /*String sor = "";
        ResultSet rsUro = de.siv.ksc.modules.Cron.GetUserRoles(Uid);
        while(rsUro.next()) {
            sor+= "e.rlid=" + rsUro.getString( 1 ) + " or ";
        }
        sor = sor.substring(0, sor.length()-4);*/
        
        /*
         * Get User Roles Ende
         */
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        String sqlGC = "SELECT DECODE(VALTEXT,'base64') FROM config_reporting WHERE KEY=encode('Cron','base64')";
        PreparedStatement ps = cn.prepareStatement(sqlGC);
        ResultSet rs = ps.executeQuery();
        
        String out = "{";
        if (rs.next()) {
            if (rs.getString(1).isEmpty()) {
                out+= "\"COMMENT\":\"" + Base64Coder.encodeString("Es wurde noch keine Konfiguration hinterlegt.") + "\"";
            } else {
                out+= "\"COMMENT\":\"" + Base64Coder.encodeString(Basics.encodePdf(rs.getString(1))) + "\"";
            }
        }
        
        /*
         * Get Formatted Cron
         */
        
        String sqlM = "SELECT cronid,function,intervall FROM cron_reporting";
        PreparedStatement psM = cn.prepareStatement(sqlM);
        ResultSet rsM = psM.executeQuery();
        
        out+= ",\"CLEANED\":[";
        
        while (rsM.next()) {
            out+= "{\"CRONID\":\"" + Base64Coder.encodeString(Basics.encodeHtml(rsM.getString(1))) + "\",\"FUNCTION\":\"" + Base64Coder.encodeString(Basics.encodeHtml(rsM.getString(2))) + "\",\"INTERVALL\":\"" + Base64Coder.encodeString(Basics.encodeHtml(rsM.getString(3))) + "\"},";
        }
        out = out.substring(0, out.length()-1); out+= "]}";
        String replace = out.replace("\":]", "\":[]");
        cn.close();
        
        
        return replace;
    }
    
    static public void ScheduleReport(String Uid, String cuid, String from, String to) throws FileNotFoundException, IOException, NamingException, SQLException, ParseException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Get User Roles
         */
        
        /*String sor = "";
        ResultSet rsUro = de.siv.ksc.modules.Cron.GetUserRoles(Uid);
        while(rsUro.next()) {
            sor+= "e.rlid=" + rsUro.getString( 1 ) + " or ";
        }
        sor = sor.substring(0, sor.length()-4);*/
        
        /*
         * Get Current Cron Text
         */
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        String sql = "SELECT DECODE(VALTEXT,'base64') FROM config_reporting WHERE KEY=encode('Cron','base64')";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        String ctext="";
        if (rs.next()) { ctext = rs.getString(1); }
        
        /*
         * Get Customer Number
         */
        
        String sqlCN = "SELECT cunr,decode(cunm,'base64'),decode(cumail,'base64') FROM managed_service_cinfo WHERE cuid=?";
        PreparedStatement psCN = cn.prepareStatement(sqlCN);
        psCN.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        ResultSet rsCN = psCN.executeQuery();
        
        String cunr=""; String cunm=""; String cumail="[Keine Mail Adresse hinterlegt]";
        if (rsCN.next()) { cunr = rsCN.getString(1); cunm = rsCN.getString(2); cumail = rsCN.getString(3); }
        
        cn.close();
        
        /*
         * Reorg Text
         */
        
        String newLine;
        File f = new File("C:/Windows");
        if (f.isDirectory()) {
            newLine = "00 20 15 ? * 1 * CREATE_REPORT[" + cunr + ";" + Base64Coder.decodeString( from ) + ";" + Base64Coder.decodeString( to ) + ";C:/servicereport_(yyyy)-(mm)-(dd)_" + Basics.encodePdfReport( cunm ) + ".pdf;" + cumail + "]"; 
        } else {
            newLine = "00 20 15 ? * 1 * CREATE_REPORT[" + cunr + ";" + Base64Coder.decodeString( from ) + ";" + Base64Coder.decodeString( to ) + ";/tmp/servicereport_(yyyy)-(mm)-(dd)_" + Basics.encodePdfReport( cunm ) + ".pdf;" + cumail + "]"; 
        }
        
        
        String newText = Base64Coder.encodeString( Basics.encodePdf( ctext + "\n" + newLine ) );
        
        /*
         * Update Crontable
         */
        
        UpdateConfigReporting(Base64Coder.encodeString("Cron"),newText);
        
    }
}
