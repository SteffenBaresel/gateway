/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.monitoring;

import de.siv.ksc.modules.Basics;
import de.siv.ksc.modules.Monitoring;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sbaresel
 */
public class GetCustomer extends HttpServlet {
    
    Properties props = null;
    
    @SuppressWarnings("empty-statement")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String uid, String hstid)
            throws ServletException, IOException {
        try {
            if (props == null) {
                props = Basics.getConfiguration();
            }
            
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "*");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = response.getWriter(); 
            out.println(Monitoring.GetCustomer(uid,hstid));
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetCustomer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GetCustomer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(GetCustomer.class.getName()).log(Level.SEVERE, null, ex);
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
        processRequest(request, response, uid, request.getParameter("hstid"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //
    }
}

