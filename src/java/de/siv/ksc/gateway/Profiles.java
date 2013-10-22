/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.gateway;

/*
 * Import needed java sources.
 */

import de.siv.ksc.modules.*;
import java.io.FileNotFoundException;
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
public class Profiles extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (props == null) {
                props = Basics.getConfiguration();
            }
            
            response.setContentType("application/json; charset=UTF-8");
            String line; 
            PrintWriter out = response.getWriter(); 
            Context ctx = new InitialContext(); 
            DataSource ds  = (DataSource) ctx.lookup( props.getProperty("JNDI.REPO.RESSOURCE") ); 
            Connection cn  = null; 
            Statement  st  = null; 
            ResultSet  rs  = null;
            
            try {
                cn = ds.getConnection(); 
                st = cn.createStatement(); 
                rs = st.executeQuery( "Select tid,decode(tus,'base64') from repo_config" );
                line = "{\"SERVLET_INFO\":\"" + getServletInfo() + "\",\"DATABASE\":\"" + cn.getMetaData().getDatabaseProductName() + " " + cn.getMetaData().getDatabaseProductVersion() + "\",\"ROWS\":[";
                while ( rs.next() ) { 
                    line += "{\"" + rs.getString( 1 ) + "\",\"" + rs.getString( 2 ) + "\"},"; 
                } 
                line = line.substring(0, line.length()-1); line += "]}"; 
                String replace = line.replace("\n", "").replace("\r", ""); 
                out.println(replace);
            } catch (SQLException ex) {
                Logger.getLogger(Profiles.class.getName()).log(Level.SEVERE, null, ex);
            } finally { }
        } catch (NamingException ex) {
            Logger.getLogger(Profiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        if (props == null) {
            try {
                props = Basics.getConfiguration();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Profiles.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Profiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Java de.siv.ksc.gateway.Profiles Servlet; JNDI: " + props.getProperty("JNDI.REPO.RESSOURCE");
        //\"PATH\":\"" + request.getContextPath() + "\",\"URL\":\"" + request.getParameter("url") + "\"
    }
}
