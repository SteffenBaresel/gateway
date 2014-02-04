/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.gateway;

import de.siv.ksc.modules.Base64Coder;
import de.siv.ksc.modules.Basics;
import de.siv.ksc.modules.Functions;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author sbaresel
 */
public class FormPicture extends HttpServlet {
    
    Properties props = null;
    
    @SuppressWarnings("empty-statement")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

            if (props == null) {
                props = Basics.getConfiguration();
            }
            
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "*");
            response.setContentType("text/html; charset=utf-8");
            
            PrintWriter out = response.getWriter();
            
            out.println("" +
"<!DOCTYPE html>" +
"<html>" +
"    <head>" +
"        <link rel='shortcut icon' href='../layout/images/favicon.ico' type='image/vnd.microsoft.icon' />" +
"        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>" +
"        <meta name='author' content='Steffen Baresel'>" +
"	 <meta name='description' content='Upload Profil Bild'>" +
"	 <title>Upload Profil Bild</title>" +
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
"            #submit { position: absolute; top: 235px; left: 675px;}" +
"            #file { position: absolute; top: 235px; left: 75px;}" +
"        </style>" +
"        <script type='text/javascript'>" +
"        </script>" +
"    </head>" +
"    <body>" +
"        <div id='body'>" +
"            <div id='Header'>" +
"                <img src='../layout/images/SIV_AG_Logo_RGB_Web.png' />" +
"                <h2>Upload Profil Bild</h2>" +
"                <h4>f&uuml;r kVASy&reg; System Control<font color='#666'> - Version 3 Build 2014.02</font></h4>" +
"                <p>W&auml;hlen Sie ein passendes Profilbild aus, welches Sie in der Applikation (z.B. Aufgabenkorb) benutzen<br>" +
"                wollen. Optimale Gr&ouml;&szlig;e: 150x150 Pixel</p>" +
"            </div>" +
"        <form id='form1' enctype='multipart/form-data' action='../exec/UploadPicture' method='post'>" +
"            <input id='file' type='file'  name='photo' />" +
"            <input id='submit' type='submit' value='Upload'/>" +
"        </form>" +
"            <div id='Footer'><p>2014</p></div>" +
"        </div>" +
"    </body>" +
"</html>");
            
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response, request.getRemoteUser() );
    }
}
