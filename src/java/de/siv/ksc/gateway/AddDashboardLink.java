/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.gateway;

import de.siv.ksc.mailing.MailHtml;
import de.siv.ksc.modules.Base64Coder;
import de.siv.ksc.modules.Basics;
import de.siv.ksc.modules.Functions;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sbaresel
 */
public class AddDashboardLink extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String uid, String title, String desc, String target)
        throws ServletException, IOException, FileNotFoundException {

        if (props == null) {
            props = Basics.getConfiguration();
        }
            
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        boolean ctsSuccess = true;
        
        try {
            Functions.AddDashboardLink( uid, title, desc, target);
        } catch (NamingException ex) {
            ctsSuccess = false;
            Logger.getLogger(AddDashboardLink.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ctsSuccess = false;
            Logger.getLogger(AddDashboardLink.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (ctsSuccess) {
            out.println("{\"ADD\":\"1\"}");
        } else {
            out.println("{\"ADD\":\"0\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uid = null; if (request.getParameter("user") == null) { uid = request.getRemoteUser(); } else { uid = request.getParameter("user"); }
        processRequest(request, response, uid, request.getParameter("title"), request.getParameter("desc"), request.getParameter("target"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uid = null; if (request.getParameter("user") == null) { uid = request.getRemoteUser(); } else { uid = request.getParameter("user"); }
        processRequest(request, response, uid, request.getParameter("title"), request.getParameter("desc"), request.getParameter("target"));
    }
}
