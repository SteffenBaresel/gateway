/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.gateway;

import de.siv.ksc.modules.Basics;
import de.siv.ksc.modules.Functions;
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
public class ResetDashboard extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String uid)
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
            Functions.ResetDashboard(uid);
        } catch (NamingException ex) {
            ctsSuccess = false;
            Logger.getLogger(AddMailConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ctsSuccess = false;
            Logger.getLogger(AddMailConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (ctsSuccess) {
            out.println("{\"RESET\":\"1\"}");
        } else {
            out.println("{\"RESET\":\"0\"}");
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
        processRequest(request, response, uid);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response, request.getParameter("key"), request.getParameter("val"));
    }
}