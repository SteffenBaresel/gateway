/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.picture;

import de.siv.ksc.modules.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import javax.naming.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author sbaresel
 */
public class UploadPicture extends HttpServlet {
    
    Properties props = null;
    
    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        String uid = null; if (request.getParameter("user") == null) { uid = request.getRemoteUser(); } else { uid = request.getParameter("user"); }
        
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.setContentType("text/html; charset=utf-8");
            
        PrintWriter out = response.getWriter();
        try {
            // Apache Commons-Fileupload library classes
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload sfu  = new ServletFileUpload(factory);

            if (! ServletFileUpload.isMultipartContent(request)) {
                System.out.println("sorry. No file uploaded");
                return;
            }

            // parse request
            List items = sfu.parseRequest(request);
            FileItem file = (FileItem) items.get(0);
                        
            Context ctx = new InitialContext(); 
            DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
            Connection cn = ds.getConnection(); 
            Statement st = cn.createStatement(); 
            PreparedStatement ps = cn.prepareStatement("UPDATE profiles_user SET upic = ? where usnm = encode('" + uid + "','base64')");
            ps.setBinaryStream(1, file.getInputStream(), (int) file.getSize());
            ps.executeUpdate();
            ps.close();
            
            /*
             * Close Connection
             */
            cn.close();
            
            out.println("" +
"<!DOCTYPE html>" +
"<html>" +
"    <head>" +
"        <link rel='shortcut icon' href='../layout/images/favicon.ico' type='image/vnd.microsoft.icon' />" +
"        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>" +
"        <meta name='author' content='Steffen Baresel'>" +
"	 <meta name='description' content='Upload Profil Bild'>" +
"	 <title>UploadProfilBild</title>" +
"        <script type='text/javascript' src='../script/jquery-1.10.2.js'></script>" +
"        <script type='text/javascript' src='../script/jquery-ui-1.10.4.custom.min.js'></script>" +
"        <link rel='stylesheet' href='../layout/jquery-ui-1.10.4.custom.css' />" +
"        <!--" +
"            Erweiterungen" +
"        -->" +
"        <style type='text/css'>" +
"            @font-face { font-family: SansProLight; src: url(../layout/SourceSansPro-Regular.ttf) format('truetype'); }" +
"            body { background: url('../layout/images/bg.png'); color: #000; font-size: 16px; font-family: SansProLight; }" +
"            #body { border: 1px solid #423939; width: 825px; height: 374px; background-color: #fff; -webkit-box-shadow: 0px 0px 2px 0px #423939; /* webkit browser*/ -moz-box-shadow: 0px 0px 2px 0px #423939; /* firefox */ box-shadow: 0px 0px 2px 0px #423939; }" +
"            #Header { width: 825px; height: 225px; }" +
"            #Header img { position: absolute; margin-left: 655px; margin-top: 20px;}" +
"            #Header h2 { position: absolute; margin-left: 50px; margin-top: 35px;}" +
"            #Header h4 { position: absolute; margin-left: 50px; margin-top: 60px;}" +
"            #Header p { position: absolute; margin-left: 50px; margin-top: 125px;}" +
"            #Footer { position: absolute; margin-left: 5px; top: 325px; width: 815px; height: 57px; border-top: 1px solid #423939; background-color: #fff; font-size: 10px; color: #000; text-align: center; }" +
"            #Footer p { margin-top: 20px; }" +
"            #menu a { text-decoration: none; color: #000; font-size: 12px;}" +
"            #menu { width: 825px; height: 25px; text-align: center; vertical-align: middle; }" +
"            #submit { position: absolute; top: 235px; left: 75px;}" +
"        </style>" +
"        <script type='text/javascript'>" +
"        </script>" +
"    </head>" +
"    <body>" +
"        <div id='body'>" +
"            <div id='Header'>" +
"                <img src='../layout/images/SIV_AG_Logo_RGB_Web.png' />" +
"                <h2>UploadProfilBild</h2>" +
"                <h4>f&uuml;r kVASy&reg; System Control<font color='#666'> - Version 3 Build 2014.02</font></h4>" +
"                <p>W&auml;hlen Sie ein passendes Profilbild aus, welches Sie in der Applikation (z.B. Aufgabenkorb) benutzen<br>" +
"                wollen. Optimale Gr&ouml;&szlig;e: 150x150 Pixel</p>" +
"            </div>" +
"            <div id='submit'>Das Profilbild wurde erfolgreich gespeichert. Sie k&ouml;nnen die Seite schlie&szlig;en!</div>" +
"            <div id='Footer'><p>2014</p></div>" +
"        </div>" +
"    </body>" +
"</html>");
        }
        catch(Exception ex) {
            out.println( "Error --> " + ex.getMessage());
        }
    } 
}
