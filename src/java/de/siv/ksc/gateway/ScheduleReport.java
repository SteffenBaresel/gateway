/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.gateway;

import de.siv.ksc.modules.Basics;
import de.siv.ksc.reporting.Functions;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
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
public class ScheduleReport extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String uid, String cu, String fr, String to)
        throws ServletException, IOException, FileNotFoundException, ParseException {

        if (props == null) {
            props = Basics.getConfiguration();
        }
            
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        boolean ctsSuccess = true;
        
        try {
            Functions.ScheduleReport(uid,cu,fr,to);
            out.println("{\"ADD\":\"1\"}");
        } catch (NamingException ex) {
            ctsSuccess = false;
            out.println("{\"ADD\":\"0\"}");
            Logger.getLogger(UpdateConfigReporting.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ctsSuccess = false;
            out.println("{\"ADD\":\"0\"}");
            Logger.getLogger(UpdateConfigReporting.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*if (ctsSuccess) {
            out.println("{\"ADD\":\"1\"}");
        } else {
            out.println("{\"ADD\":\"0\"}");
        }*/
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileNotFoundException {
        String uid = null; if (request.getParameter("user") == null) { uid = request.getRemoteUser(); } else { uid = request.getParameter("user"); }
        try {
            processRequest(request, response, uid, request.getParameter("cu"), request.getParameter("fr"), request.getParameter("to"));
        } catch (ParseException ex) {
            Logger.getLogger(UpdateConfigReporting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileNotFoundException {
        String uid = null; if (request.getParameter("user") == null) { uid = request.getRemoteUser(); } else { uid = request.getParameter("user"); }
        try {
            processRequest(request, response, uid, request.getParameter("cu"), request.getParameter("fr"), request.getParameter("to"));
        } catch (ParseException ex) {
            Logger.getLogger(UpdateConfigReporting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
