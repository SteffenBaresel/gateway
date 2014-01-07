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
public class CreateServiceEntry extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String uuid, String cuid, String ccid, String comtid, String tm, String dl, String co, String esk)
        throws ServletException, IOException, FileNotFoundException, ParseException {

        if (props == null) {
            props = Basics.getConfiguration();
        }
            
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        String ctsSuccess = "0";
        
        
        try {
            ctsSuccess = Functions.CreateServiceEntry(uuid,cuid,ccid,comtid,tm,dl,co,esk);
        } catch (NamingException ex) {
            ctsSuccess = "0";
            Logger.getLogger(CreateCustomer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ctsSuccess = "0";
            Logger.getLogger(CreateCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        out.println("{\"EXEC\":\"" + ctsSuccess + "\",\"CO\":\"" + co + "\"}");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileNotFoundException {
        try {
            processRequest(request,response,request.getParameter("uuid"),request.getParameter("cuid"),request.getParameter("ccid"),request.getParameter("comtid"),request.getParameter("tm"),request.getParameter("dl"),request.getParameter("co"),request.getParameter("esk"));
        } catch (ParseException ex) {
            Logger.getLogger(CreateServiceEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //
    }
}
