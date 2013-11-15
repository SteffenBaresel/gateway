/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.gateway;

import de.siv.ksc.modules.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author sbaresel
 */
public class GetUserConfig extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String uid)
            throws ServletException, IOException {
        try {
            if (props == null) {
                props = Basics.getConfiguration();
            }
            
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "*");
            response.setContentType("application/json; charset=utf-8");
            String line; 
            PrintWriter out = response.getWriter(); 
            Context ctx = new InitialContext(); 
            DataSource ds  = (DataSource) ctx.lookup("jdbc/repository");
            Connection cn  = null; 
            Statement  st  = null; 
            Statement  st2  = null; 
            Statement  st3  = null; 
            ResultSet  rs  = null;
            ResultSet  rs2  = null;
            ResultSet  rs3  = null;
            
            try {
                cn = ds.getConnection(); 
                st = cn.createStatement(); 
                rs = st.executeQuery("SELECT decode(tv1,'base64'),decode(tv2,'base64'),decode(tv3,'base64') FROM repo_config WHERE tus = encode('" + uid + "','base64') AND tmd = encode('DASHBOARD','base64') ORDER BY tid ASC");
                line = "[{\"LOCAL_BACKEND\":\"" + props.getProperty("BACKEND.IP") + ":" + props.getProperty("BACKEND.PORT") + "\",\"PICTURE_TYPE\":\"" + props.getProperty("PICTURE.WEB.TYPE") + "\",\"PICTURE_PATH\":\"" + props.getProperty("PICTURE.WEB.PATH") + "/DefaultProfile.png\",\"DASHBOARD\":[";
                while ( rs.next() ) { 
                    line += "{\"TITLE\":\"" + Basics.encodeHtml( rs.getString( 1 ) ) + "\",\"DESC\":\"" + Basics.encodeHtml( rs.getString( 2 ) ) + "\",\"TARGET\":\"" + Basics.encodeHtml( rs.getString( 3 ) ) + "\"},"; 
                } 
                line = line.substring(0, line.length()-1); line += "],\"USER_CONFIG\":["; 
                /* Zweite Query */
                st2 = cn.createStatement(); 
                rs2 = st2.executeQuery("SELECT decode(tky,'base64'),decode(tv1,'base64'),decode(tv2,'base64'),decode(tv3,'base64') FROM repo_config WHERE tus = encode('" + uid + "','base64') AND tmd = encode('Config','base64') ORDER BY tid ASC");
                while ( rs2.next() ) { 
                    line += "{\"KEY\":\"" + Basics.encodeHtml( rs2.getString( 1 ) ) + "\",\"ACTION\":\"" + Basics.encodeHtml( rs2.getString( 2 ) ) + "\",\"DESC\":\"" + Basics.encodeHtml( rs2.getString( 3 ) ) + "\"},";
                }
                line = line.substring(0, line.length()-1); line += "],";
                /* Dritte Query */
                st3 = cn.createStatement(); 
                rs3 = st3.executeQuery("SELECT decode(usdc,'base64'),decode(umai,'base64'),decode(upic,'base64') FROM profiles_user WHERE usnm = encode('" + uid + "','base64')");
                while ( rs3.next() ) { 
                    line += "\"NAME\":\"" + Basics.encodeHtml( rs3.getString( 1 ) ) + "\",\"MAIL\":\"" + rs3.getString( 2 ) + "\",\"PICTURE\":\"" + rs3.getString( 3 ) + "\",";
                }
                line = line.substring(0, line.length()-1); line += "}]";
                //
                String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]"); 
                out.println(replace);
            } catch (SQLException ex) {
                Logger.getLogger(GetUserConfig.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try { 
                    cn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(GetUserConfig.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (NamingException ex) {
            Logger.getLogger(GetUserConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uid = null;
        if (request.getParameter("user") == null) {
            uid = request.getRemoteUser();
        } else {
            uid = request.getParameter("user");
        }
        processRequest(request, response, uid );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response, request.getRemoteUser() );
    }
}
