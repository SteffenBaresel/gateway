/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.gateway;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import de.siv.ksc.modules.Basics;
import de.siv.ksc.reporting.ReportingFunctions;
import java.io.FileNotFoundException;
import java.io.IOException;
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
public class ServiceReport extends HttpServlet {
    
    Properties props = null;
    
    //protected void processRequest(HttpServletRequest request, HttpServletResponse response, String mod, String id, String desc)
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String cuid, String from, String to)
        throws ServletException, IOException, FileNotFoundException, NamingException, SQLException, BadElementException, ParseException {
        try {
            if (props == null) {
                props = Basics.getConfiguration();
            }
                  
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "*");
            response.setContentType("application/pdf");
            try {
                new ReportingFunctions().createPdf(response,cuid,from,to);
            } catch (DocumentException ex) {
                Logger.getLogger(ServiceReport.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GetUserConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileNotFoundException {
        try {
            processRequest(request, response, request.getParameter("cuid"), request.getParameter("from"), request.getParameter("to"));
        } catch (BadElementException ex) {
            Logger.getLogger(ServiceReport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(ServiceReport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(ServiceReport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServiceReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //
    }
}
