<%-- 
    Document   : index
    Created on : 14.11.2013, 18:49:56
    Author     : sbaresel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <!--GATEWAY_ENGINE:2.3.131119-->
        <center>
            <h2>kVASy&reg; System Control - Gateway Engine 2.3.131119</h2>
            <p>
                <font size=2>Entwicklungslinie 2 Update 3 - Steffen Baresel</font>
            </p>
        </center>
        <div>
            <i>
                <font size=3>Fehler an:</font>
            </i>
            <br>
            <b>Mail: kvasysystemcontrol@siv.de</b>
            <br>
            <b>Tel: 0381 - 2524 0</b>
        </div>
        <br></br><br></br>
        <font size=2>
        <!--ul>
            <li>Get Core Version (json) URL: <a href="/?cv=g">/?cv=g</a></li>
            <li>Get Modul Version (json) URL: <a href="/?mv=g">/?mv=g</a></li>
        </ul-->
        </font>
        <hr>
        <div>
            <center>Nachfolgend eine &Uuml;bersicht der Funktionen der REST Schnittstelle:</center>
        </div>
        <hr>
        <br></br>
        <!--GATEWAY_VERSION:2.3.131119-->
        Modul: <i><b>Repository</b></i><br>
        Version: <i><b>2.3.131119</b></i><br>
        Comments: -
        <br></br>
        Format: <i><b>JSON</b></i><br>
        <font size=2>Query:<br>
        <ul>
            <li><b>GetUserConfig</b> - List User Configuration From Repository URL: <a href="/gateway/exec/GetUserConfig">/gateway/exec/GetUserConfig</a></li>
            <li><b>SendHtmlMail</b> - Send Mail from Web, required BASE64 values for parameter URL: <a href="/gateway/exec/SendHtmlMail?to=&cc=&from=&subject=&text=">/gateway/exec/SendHtmlMail?to=&cc=&from=&subject=&text=</a></li>
            <li><b>AddDashboardLink</b> - Add Dashboard Link, required BASE64 values for parameter URL: <a href="/gateway/exec/AddDashboardLink?title=&desc=&target=">/gateway/exec/AddDashboardLink?title=&desc=&target=</a></li>
            <li><b>UpdateUserMail</b> - Update User Mailaddress, required BASE64 values for parameter URL: <a href="/gateway/exec/UpdateUserMail?mail=">/gateway/exec/UpdateUserMail?mail=</a></li>
        </ul>
        </font>
        <br></br>
        <hr>
        <br></br>
        <!--MONITORING_VERSION:2.1.131028-->
        <!--Modul: <i><b>Monitoring</b></i><br>
        Version: <i><b>2.1.131028</b></i><br>
        Comments: -
        <br></br>
        Format: <i><b>JSON</b></i><br>
        <font size=2>Query:<br>
        <ul>
            <!--li><b>Installation/Configuration</b>
                <ul>
                    <li><b>PrepareMonitoring</b> - Prepare Database Backend for Monitoring usage URL: <a href="/gateway/repository/PrepareMonitoring">/gateway/repository/PrepareMonitoring</a></li-->
                </ul>
            </li><br>
            <!--li><b>GetUserConfig</b> - List User Configuration From Repository URL: <a href="/gateway/repository/GetUserConfig">/gateway/repository/GetUserConfig</a></li-->
        </ul>
        </font>
        <br></br>
        <hr-->
        <center><font size="1"><b>SIV.AG &copy;2013</b></font></center>
    </body>
</html>
