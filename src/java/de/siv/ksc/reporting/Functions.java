/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.reporting;

import com.itextpdf.xmp.impl.Base64;
import de.siv.ksc.modules.Base64Coder;
import de.siv.ksc.modules.Basics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        ResultSet rsUro = de.siv.ksc.modules.Functions.GetUserRoles(Uid);
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
                out = "{\"COMMENT\":\"" + Base64.encode("Es wurde noch keine Bemerkung hinterlegt.") + "\"}";
            } else {
                out = "{\"COMMENT\":\"" + Base64.encode(Basics.encodePdf(rs.getString(1))) + "\"}";
            }
        }
        
        cn.close();
        return out;
    }
    
    static public void UpdateConfigReporting(String key, String val) throws FileNotFoundException, IOException, NamingException, SQLException {
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
    }
    
    static public String GetLastPageContactsComment(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Get User Roles
         */
        
        /*String sor = "";
        ResultSet rsUro = de.siv.ksc.modules.Functions.GetUserRoles(Uid);
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
                out = "{\"COMMENT\":\"" + Base64.encode("Es wurde noch keine Bemerkung hinterlegt.") + "\"}";
            } else {
                out = "{\"COMMENT\":\"" + Base64.encode(Basics.encodePdf(rs.getString(1))) + "\"}";
            }
        }
        
        cn.close();
        return out;
    }
}
