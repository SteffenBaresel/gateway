/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.creator;

import de.siv.ksc.modules.Base64Coder;
import de.siv.ksc.modules.Basics;
import de.siv.ksc.modules.Functions;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author sbaresel
 */
public class CreatorFunctions {
    static Properties props = null;
    
    static public String Distributor(String request) throws FileNotFoundException, IOException, JSONException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        String out="";
        
        JSONObject object = new JSONObject(request);
            
        if (object.has("FUNCTION")) {
            String json = object.getString("FUNCTION");
            String function[] = json.split("\\[", -1);
            String fct = function[0];
            String val = function[1];
            val = val.substring(0, val.length()-1);
            String parameter[] = val.split("\\;", -1);
            int size = 0;
            for (String value : parameter) { size++; }
        
            /* Result: function parameter[1] parameter[2] parameter[3] */
        
            if ("CREATE_SERVICE_ENTRY".equals(fct)) {
                /* Get Auftragsnummer */
                String ANr = parameter[0];
                /* Get Timestamp yyyy-mm-dd hh:mm:ss */
                String DAT = parameter[1];
                /* Get Author */
                String WHO = parameter[2];
                /* Get Text */
                String TXT = parameter[3];
                try {
                    /* Execute Report */
                    /* CreateServiceEntry(uuid, cuid, ccid, comtid, tm, dl, co, esk) */
                    Functions.CreateServiceEntry(Base64Coder.encodeString(GetUuid(WHO)), Base64Coder.encodeString(GetCuid(ANr)), Base64Coder.encodeString(GetCcid(ANr)), Base64Coder.encodeString("3"), Base64Coder.encodeString(DAT), Base64Coder.encodeString("15"), Base64Coder.encodeString(TXT), Base64Coder.encodeString("0"));
                    out = "{\"MESSAGE\":\"Entry Success.\",\"ANr\":\"" + ANr + "\",\"DAT\":\"" + DAT + "\",\"WHO\":\"" + WHO + "\",\"TXT\":\"" + Basics.encodePdf(TXT) + "\"}";
                } catch (ParseException ex) {
                    Logger.getLogger(CreatorFunctions.class.getName()).log(Level.SEVERE, null, ex);
                    out = new JSONObject().put("ERROR", "Function do not exist.").toString();
                }
            } else {
                out = new JSONObject().put("ERROR", "Function do not exist.").toString();
            }
        } else {
            out = new JSONObject().put("ERROR", "This Request is not supported.").toString();
        }
            
        return out;
    }
    
    static public String GetUuid(String Name) throws NamingException, SQLException {
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection();
        String sql = "SELECT uuid FROM profiles_user WHERE usnm=?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setString(1, Base64Coder.encodeString(Name));
        ResultSet rs = ps.executeQuery();
        
        String uuid="";
        if (rs.next()) { uuid = rs.getString(1); } else { uuid="1"; }
        
        cn.close();
        return uuid;
    }
    
    static public String GetCuid(String Anr) throws NamingException, SQLException {
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection();
        String sql = "select a.cuid from managed_service_cinfo a, managed_service_ccontracts b where a.cuid=b.cuid and b.ccnr=?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1,Long.parseLong( Anr ));
        ResultSet rs = ps.executeQuery();
        
        String cuid="";
        if (rs.next()) { cuid = rs.getString(1); } else { cuid="0"; }
        
        cn.close();
        return cuid;
    }
    
    static public String GetCcid(String Anr) throws NamingException, SQLException {
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection();
        String sql = "select ccid from managed_service_ccontracts where ccnr=?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1,Long.parseLong( Anr ));
        ResultSet rs = ps.executeQuery();
        
        String ccid="";
        if (rs.next()) { ccid = rs.getString(1); } else { ccid="0"; }
        
        cn.close();
        return ccid;
    }
}
