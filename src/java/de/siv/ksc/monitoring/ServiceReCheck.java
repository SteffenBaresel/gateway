/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.monitoring;

import de.siv.ksc.modules.Basics;
import de.siv.ksc.modules.Functions;
import de.siv.ksc.modules.Monitoring;
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
public class ServiceReCheck extends HttpServlet {
    
    Properties props = null;
    
    @SuppressWarnings("empty-statement")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String uid, String hstid, String srvid, String ts, String instid)
            throws ServletException, IOException {
        
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        String ctsSuccess = "0";
                
        try {
            ctsSuccess = Monitoring.ServiceReCheck(uid,hstid,srvid,ts,instid);
        } catch (FileNotFoundException ex) {
            ctsSuccess = "0";
            Logger.getLogger(BigTaov.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ctsSuccess = "0";
            Logger.getLogger(BigTaov.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            ctsSuccess = "0";
            Logger.getLogger(BigTaov.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            ctsSuccess = "0";
            Logger.getLogger(BigTaov.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if ("1".equals(ctsSuccess)) {
            out.println("{\"EXEC\":\"1\"}");
        } else {
            out.println("{\"EXEC\":\"0\"}");
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
        processRequest(request, response, uid, request.getParameter("hstid"), request.getParameter("srvid"), request.getParameter("ts"), request.getParameter("instid") );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response, request.getRemoteUser() );
    }
}
