/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.modules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    
    static public void AddDashboardLink(String Uid, String Title, String Desc, String Target) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        Statement st = cn.createStatement(); 
        /*
         * Create Configuration entries config_portal WHERE usr = encode('" + uid + "','base64') AND mod = encode('DASHBOARD','base64')
         */
        st.execute("INSERT INTO config_portal(USR,MOD,KEY,VAL1,VAL2,VAL3) VALUES (encode('" + Uid + "','base64'),encode('DASHBOARD','base64'),encode('LINK','base64'),'" + Title + "','" + Desc + "','" + Target + "')");
        /*
         * Close Connection
         */
        cn.close();
    }
    
    static public String UsersPermissions(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        Statement st = cn.createStatement(); 
        ResultSet rs = st.executeQuery("select decode(e.prnm, 'base64'),decode(e.prdc, 'base64') from profiles_user a, profiles_user_group_mapping b, profiles_group_role_mapping c, profiles_role_priv_mapping d, profiles_privilege e where a.uuid = b.uuid and b.grid = c.grid and c.rlid = d.rlid and d.prid = e.prid and a.usnm = encode('" + Uid + "','base64')");
        while ( rs.next() ) {
            out += "\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(1) + " (" + rs.getString(2) + ")" ) ) + "\",";
        }
        out = out.substring(0, out.length()-1);
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String UsersGroups(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        Statement st = cn.createStatement(); 
        ResultSet rs = st.executeQuery("select decode(b.grdc,'base64') from profiles_user a, profiles_group b, profiles_user_group_mapping c where a.uuid = c.uuid AND b.grid = c.grid AND a.usnm = encode('" + Uid + "','base64')");
        while ( rs.next() ) {
            out += "\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(1) ) ) + "\",";
        }
        out = out.substring(0, out.length()-1);
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public void UpdateUserMail(String Uid, String Mail) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        Statement st = cn.createStatement(); 
        /*
         * Update Mail Konfiguration
         */
        st.execute("UPDATE profiles_user SET umai = '" + Mail + "' where usnm = encode('" + Uid + "','base64')");
        /*
         * Close Connection
         */
        cn.close();
    }
}
