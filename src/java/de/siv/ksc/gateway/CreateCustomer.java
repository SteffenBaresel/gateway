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
public class CreateCustomer extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String cname, String cnumber, String cmail, String cesmail, String caddress, String ccomm, String ct1, String ct1an, String ct1pv, String ct1pi, String ct2, String ct2an, String ct2pv, String ct2pi, String ct3, String ct3an, String ct3pv, String ct3pi, String ct4, String ct4an, String ct4pv, String ct4pi, String ct5, String ct5an, String ct5pv, String ct5pi, String ct6, String ct6an, String ct6pv, String ct6pi, String repcom)
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
            ctsSuccess = Functions.CreateCustomer(cname,cnumber,cmail,cesmail,caddress,ccomm,ct1,ct1an,ct1pv,ct1pi,ct2,ct2an,ct2pv,ct2pi,ct3,ct3an,ct3pv,ct3pi,ct4,ct4an,ct4pv,ct4pi,ct5,ct5an,ct5pv,ct5pi,ct6,ct6an,ct6pv,ct6pi,repcom);
        } catch (NamingException ex) {
            ctsSuccess = "0";
            Logger.getLogger(CreateCustomer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ctsSuccess = "0";
            Logger.getLogger(CreateCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        out.println("{\"EXEC\":\"" + ctsSuccess + "\",\"CADDRESS\":\"" + caddress + "\",\"CCOMM\":\"" + ccomm + "\"}");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request,response,request.getParameter("cname"),request.getParameter("cnumber"),request.getParameter("cmail"),request.getParameter("cesmail"),request.getParameter("caddress"),request.getParameter("ccomm"),request.getParameter("ct1"),request.getParameter("ct1an"),request.getParameter("ct1pv"),request.getParameter("ct1pi"),request.getParameter("ct2"),request.getParameter("ct2an"),request.getParameter("ct2pv"),request.getParameter("ct2pi"),request.getParameter("ct3"),request.getParameter("ct3an"),request.getParameter("ct3pv"),request.getParameter("ct3pi"),request.getParameter("ct4"),request.getParameter("ct4an"),request.getParameter("ct4pv"),request.getParameter("ct4pi"),request.getParameter("ct5"),request.getParameter("ct5an"),request.getParameter("ct5pv"),request.getParameter("ct5pi"),request.getParameter("ct6"),request.getParameter("ct6an"),request.getParameter("ct6pv"),request.getParameter("ct6pi"),request.getParameter("repcom"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //
    }
}
