/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.gateway;

import de.siv.ksc.mailing.*;
import de.siv.ksc.modules.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sbaresel
 */
public class SendHtmlMail extends HttpServlet {
    
    Properties props = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String to, String cc, String from, String subject, String text)
        throws ServletException, IOException, FileNotFoundException {

        if (props == null) {
            props = Basics.getConfiguration();
        }
            
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        
        String tocc = props.getProperty("MAIL.FXCC") + "," + cc;
        boolean ctsSuccess = true;
        String fulltext = null;
        
        if (props.getProperty("MAIL.ATTN").length() > 0) {
            fulltext = text + "" + Basics.readFile(props.getProperty("MAIL.ATTN")); 
        } else {
            fulltext = text;
        }
        
        try {            
            try {
                MailHtml.send(to,tocc,from,subject,fulltext);
            } catch (NamingException ex) {
                ctsSuccess = false;
                Logger.getLogger(SendHtmlMail.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                ctsSuccess = false;
                Logger.getLogger(SendHtmlMail.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchProviderException ex) {
            ctsSuccess = false;
            Logger.getLogger(SendHtmlMail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            ctsSuccess = false;
            Logger.getLogger(SendHtmlMail.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (ctsSuccess) {
            out.println("{\"SEND\":\"1\",\"TO\":\"" + to + "\",\"CC\":\"" + tocc + "\",\"FROM\":\"" + from + "\",\"SUBJECT\":\"" + subject + "\"}");
        } else {
            out.println("{\"SEND\":\"0\",\"TO\":\"" + to + "\",\"CC\":\"" + tocc + "\",\"FROM\":\"" + from + "\",\"SUBJECT\":\"" + subject + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, Base64Coder.decodeString(request.getParameter("to")), Base64Coder.decodeString(request.getParameter("cc")), Base64Coder.decodeString(request.getParameter("from")), Base64Coder.decodeString(request.getParameter("subject")), Base64Coder.decodeString(request.getParameter("text")));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, Base64Coder.decodeString(request.getParameter("to")), Base64Coder.decodeString(request.getParameter("cc")), Base64Coder.decodeString(request.getParameter("from")), Base64Coder.decodeString(request.getParameter("subject")), Base64Coder.decodeString(request.getParameter("text")));
    }
}