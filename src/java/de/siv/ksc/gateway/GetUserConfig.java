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
    
    @SuppressWarnings("empty-statement")
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
                rs = st.executeQuery("SELECT decode(val1,'base64'),decode(val2,'base64'),decode(val3,'base64') FROM config_portal WHERE usr = encode('" + uid + "','base64') AND mod = encode('DASHBOARD','base64') ORDER BY cpid ASC");
                line = "[{\"LOCAL_BACKEND\":\"" + props.getProperty("BACKEND.IP") + ":" + props.getProperty("BACKEND.PORT") + "\",\"PICTURE_TYPE\":\"" + props.getProperty("PICTURE.WEB.TYPE") + "\",\"PICTURE_PATH\":\"" + props.getProperty("PICTURE.WEB.PATH") + "/DefaultProfile.png\",\"DASHBOARD\":[";
                while ( rs.next() ) { 
                    String tv1; if (rs.getString( 1 ) == null) { tv1 = Base64Coder.encodeString( "-" ); } else { tv1 = Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 1 ) ) ); }
                    String tv2; if (rs.getString( 2 ) == null) { tv2 = Base64Coder.encodeString( "-" ); } else { tv2 = Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 2 ) ) ); }
                    String tv3; if (rs.getString( 3 ) == null) { tv3 = Base64Coder.encodeString( "-" ); } else { tv3 = Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 3 ) ) ); }
                    line += "{\"TITLE\":\"" + tv1 + "\",\"DESC\":\"" + tv2 + "\",\"TARGET\":\"" + tv3 + "\"},"; 
                } 
                line = line.substring(0, line.length()-1); line += "],\"USER_CONFIG\":["; 
                /* Zweite Query */
                st2 = cn.createStatement(); 
                rs2 = st2.executeQuery("SELECT decode(key,'base64'),decode(val1,'base64'),decode(val2,'base64'),decode(val3,'base64') FROM config_portal WHERE usr = encode('" + uid + "','base64') AND mod = encode('Config','base64') ORDER BY cpid ASC");
                while ( rs2.next() ) {
                    String tky; if (rs2.getString( 1 ) == null) { tky = Base64Coder.encodeString( "-" ); } else { tky = Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 1 ) ) ); }
                    String tv1; if (rs2.getString( 2 ) == null) { tv1 = Base64Coder.encodeString( "-" ); } else { tv1 = Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 2 ) ) ); }
                    String tv2; if (rs2.getString( 3 ) == null) { tv2 = Base64Coder.encodeString( "-" ); } else { tv2 = Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 3 ) ) ); }
                    line += "{\"KEY\":\"" + tky + "\",\"ACTION\":\"" + tv1 + "\",\"DESC\":\"" + tv2 + "\"},";
                }
                line = line.substring(0, line.length()-1); line += "],";
                /* Dritte Query */
                st3 = cn.createStatement(); 
                rs3 = st3.executeQuery("SELECT decode(usdc,'base64'),decode(umai,'base64'),decode(upic,'base64') FROM profiles_user WHERE usnm = encode('" + uid + "','base64')");
                while ( rs3.next() ) { 
                    String fnam; if (rs3.getString( 1 ) == null) { fnam = Base64Coder.encodeString( "-" ); } else { fnam = Base64Coder.encodeString( Basics.encodeHtml( rs3.getString( 1 ) ) ); }
                    String mail; if (rs3.getString( 2 ) == null) { mail = Base64Coder.encodeString( "-" ); } else { mail = Base64Coder.encodeString( Basics.encodeHtml( rs3.getString( 2 ) ) ); }
                    String pict; if (rs3.getString( 3 ) == null) { pict = Base64Coder.encodeString( "-" ); } else { pict = Base64Coder.encodeString( Basics.encodeHtml( rs3.getString( 3 ) ) ); }
                    line += "\"UID\":\"" + Base64Coder.encodeString( uid ) + "\",\"NAME\":\"" + fnam + "\",\"MAIL\":\"" + mail + "\",\"PICTURE\":\"" + pict + "\",";
                }
                line = line.substring(0, line.length()-1);
                // User Group
                line += ",\"USER_GROUPS\":[" + Functions.UsersGroups(uid) + "]";
                // USer Permission
                line += ",\"USER_PERM\":[" + Functions.UsersPermissions(uid) + "]";
                //
                line += "}]";
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
