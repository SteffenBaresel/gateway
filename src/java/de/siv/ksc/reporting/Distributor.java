/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.reporting;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import de.siv.ksc.modules.Base64Coder;
import de.siv.ksc.modules.Basics;
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
import org.json.*;

/**
 *
 * @author sbaresel
 */
public class Distributor {
    
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
        
            if ("CREATE_REPORT".equals(fct)) {
                /* Get Cuid */
                String Cuid = GetCuid(parameter[0]);
                /* Interpret Date From */
                String From = Basics.InterpreteDate(parameter[1]);
                /* Interpret Date To */
                String To = Basics.InterpreteDate(parameter[2]);
                /* Get File Name */
                String File = Basics.InterpreteTags(parameter[3]);
                /* Get Mailaddresses */
                String Mail;
                if (size > 4) {
                    Mail = parameter[4];
                } else {
                    Mail = "[Keine Mailadresse angegeben.]";
                }
                /* Execute Report */
                try {
                    ReportingFunctions.createPdfFile(Cuid, From, To, File);
                } catch (DocumentException ex) {
                    Logger.getLogger(Distributor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(Distributor.class.getName()).log(Level.SEVERE, null, ex);
                }
                /* Output */
                out = "{\"MESSAGE\":\"Report Success.\",\"CUNM\":\"" + Base64Coder.encodeString(GetCunm(parameter[0])) + "\",\"CUNR\":\"" + parameter[0] + "\",\"FROM\":\"" + From + "\",\"TO\":\"" + To + "\",\"FILE\":\"" + Base64Coder.encodeString(File) + "\",\"MAIL\":\"" + Mail + "\"}";
            } else {
                out = new JSONObject().put("ERROR", "Function do not exist.").toString();
            }
        } else {
            out = new JSONObject().put("ERROR", "This Request is not supported.").toString();
        }
        
        return out;
    }
    
    static public String GetCuid(String Cunr) throws NamingException, SQLException {
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection();
        String sql = "SELECT cuid FROM managed_service_cinfo WHERE cunr=?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setInt(1,Integer.parseInt( Cunr ));
        ResultSet rs = ps.executeQuery();
        
        String cuid="";
        if (rs.next()) { cuid = rs.getString(1); } else { cuid="[Kunde existiert nicht]"; }
        
        cn.close();
        return cuid;
    }
    
    static public String GetCunm(String Cunr) throws NamingException, SQLException {
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection();
        String sql = "SELECT decode(cunm,'base64') FROM managed_service_cinfo WHERE cunr=?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setInt(1,Integer.parseInt( Cunr ));
        ResultSet rs = ps.executeQuery();
        
        String cunm="";
        if (rs.next()) { cunm = rs.getString(1); } else { cunm="[Kunde existiert nicht]"; }
        
        cn.close();
        return cunm;
    }
    
}
