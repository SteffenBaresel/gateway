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
public class CreateMailEntry extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String msid, String uuid, String cuid, String ccid, String mto, String mcc, String msubject, String mbody, String esk)
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
            Functions.CreateMailEntry(msid, uuid, cuid, ccid, mto, mcc, msubject, mbody, esk);
            ctsSuccess = "1";
        } catch (NamingException ex) {
            ctsSuccess = "0";
            Logger.getLogger(CreateMailEntry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ctsSuccess = "0";
            Logger.getLogger(CreateMailEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        out.println("{\"EXEC\":\"" + ctsSuccess + "\",\"MSID\":\"" + msid + "\",\"UUID\":\"" + uuid + "\",\"CUID\":\"" + cuid + "\",\"CCID\":\"" + ccid + "\",\"MTO\":\"" + mto + "\",\"MCC\":\"" + mcc + "\",\"MSUBJECT\":\"" + msubject + "\",\"MBODY\":\"" + mbody + "\",\"MESC\":\"" + esk + "\"}");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileNotFoundException {
        try {
            processRequest(request,response,request.getParameter("msid"),request.getParameter("uuid"),request.getParameter("cuid"),request.getParameter("ccid"),request.getParameter("mto"),request.getParameter("mcc"),request.getParameter("msubject"),request.getParameter("mbody"),request.getParameter("mesc"));
        } catch (ParseException ex) {
            Logger.getLogger(CreateMailEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //
    }
}
