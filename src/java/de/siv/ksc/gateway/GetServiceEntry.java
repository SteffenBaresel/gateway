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
public class GetServiceEntry extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String uid, String uuid, String offset, String limit)
            throws ServletException, IOException {
        try {
            if (props == null) {
                props = Basics.getConfiguration();
            }
            
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "*");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = response.getWriter(); 
            out.println(Functions.GetServiceEntry(uid,uuid,offset,limit));
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetServiceEntry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GetServiceEntry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(GetServiceEntry.class.getName()).log(Level.SEVERE, null, ex);
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
        processRequest(request, response, uid, request.getParameter("uuid"), request.getParameter("offset"), request.getParameter("limit") );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response, request.getRemoteUser() );
    }
}
