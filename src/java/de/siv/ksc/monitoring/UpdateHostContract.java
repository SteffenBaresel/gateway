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
public class UpdateHostContract extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String hstid, String ccid)
        throws ServletException, IOException, FileNotFoundException {

        if (props == null) {
            props = Basics.getConfiguration();
        }
            
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        String ctsSuccess = "0";
        
        try {
            ctsSuccess = Monitoring.UpdateHostContract( hstid, ccid);
        } catch (NamingException ex) {
            ctsSuccess = "0";
            Logger.getLogger(UpdateHostContract.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ctsSuccess = "0";
            Logger.getLogger(UpdateHostContract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if ("1".equals(ctsSuccess)) {
            out.println("{\"EXEC\":\"1\",\"HSTID\":\"" + hstid + "\",\"CCID\":\"" + ccid + "\"}");
        } else {
            out.println("{\"EXEC\":\"0\",\"HSTID\":\"" + hstid + "\",\"CCID\":\"" + ccid + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, request.getParameter("hstid"), request.getParameter("ccid"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //
    }
}
