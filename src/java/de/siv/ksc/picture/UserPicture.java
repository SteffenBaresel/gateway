/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.picture;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author sbaresel
 */
public class UserPicture extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String uid)
    throws ServletException, IOException {
        
        try {
            
            byte[] byteImg = null;
            Context ctx = new InitialContext(); 
            DataSource ds  = (DataSource) ctx.lookup("jdbc/repository");
            Connection cn  = ds.getConnection();
            Statement st = cn.createStatement(); 
            ResultSet rs = st.executeQuery("SELECT upic FROM profiles_user WHERE usnm = encode('" + uid + "','base64')");
            while(rs.next()) {
                byteImg = rs.getBytes(1);
            }
            
            //response.setContentType("image/png");

            OutputStream os = response.getOutputStream();

            os.write(byteImg);
            os.close();
            
            rs.close();
            cn.close();
            
        }
        catch(Exception ex) {
             System.out.println(ex.getMessage());
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
        processRequest(request, response, uid);
    } 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String uid = null;
        if (request.getParameter("user") == null) {
            uid = request.getRemoteUser();
        } else {
            uid = request.getParameter("user");
        }
        processRequest(request, response, uid);
    }
}
