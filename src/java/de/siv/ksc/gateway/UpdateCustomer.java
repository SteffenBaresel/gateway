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
public class UpdateCustomer extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String cuid, String cname, String cnumber, String cmail, String cesmail, String caddress, String ccomm, String ct1, String ct1an, String ct1pv, String ct1pi, String repcom)
        throws ServletException, IOException, FileNotFoundException {

        if (props == null) {
            props = Basics.getConfiguration();
        }
            
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        Integer ctsSuccess = 0;
        
        
        try {
            ctsSuccess = Functions.UpdateCustomer(cuid,cname,cnumber,cmail,cesmail,caddress,ccomm,ct1,ct1an,ct1pv,ct1pi,repcom);
        } catch (NamingException ex) {
            ctsSuccess = 0;
            Logger.getLogger(UpdateCustomer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ctsSuccess = 0;
            Logger.getLogger(UpdateCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        out.println("{\"EXEC\":\"" + ctsSuccess + "\",\"CADDRESS\":\"" + caddress + "\",\"CCOMM\":\"" + ccomm + "\"}");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request,response,request.getParameter("cuid"),request.getParameter("cname"),request.getParameter("cnumber"),request.getParameter("cmail"),request.getParameter("cesmail"),request.getParameter("caddress"),request.getParameter("ccomm"),request.getParameter("ct1"),request.getParameter("ct1an"),request.getParameter("ct1pv"),request.getParameter("ct1pi"),request.getParameter("repcom"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //
    }
}
