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
        <!--GATEWAY_ENGINE:2.3.131125-->
        <center>
            <h2>kVASy&reg; System Control - Gateway Engine 2.3.131125</h2>
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
        <!--GATEWAY_VERSION:2.3.131125-->
        Modul: <i><b>Repository</b></i><br>
        Version: <i><b>2.3.131125</b></i><br>
        Comments: -
        <br></br>
        Format: <i><b>JSON</b></i><br>
        <font size=2>Query:<br>
        <ul>
            <li><b>GetUserConfig</b> - List User Configuration From Repository URL: <a href="/gateway/exec/GetUserConfig">/gateway/exec/GetUserConfig</a></li>
            <li><b>SendHtmlMail</b> - Send Mail from Web, required BASE64 values for parameter URL: <a href="/gateway/exec/SendHtmlMail?to=&cc=&from=&subject=&text=">/gateway/exec/SendHtmlMail?to=&cc=&from=&subject=&text=</a></li>
            <li><b>AddDashboardLink</b> - Add Dashboard Link, required BASE64 values for parameter URL: <a href="/gateway/exec/AddDashboardLink?title=&desc=&target=">/gateway/exec/AddDashboardLink?title=&desc=&target=</a></li>
            <li><b>UpdateUserMail</b> - Update User Mailaddress, required BASE64 values for parameter URL: <a href="/gateway/exec/UpdateUserMail?mail=">/gateway/exec/UpdateUserMail?mail=</a></li>
            <li><b>FormPicture</b> - Formular to upload picture URL: <a href="/gateway/exec/FormPicture">/gateway/exec/FormPicture</a></li>
            <li><b>UploadPicture</b> - Upload picture to database URL: <a href="/gateway/exec/UploadPicture">/gateway/exec/UploadPicture</a></li>
            <li><b>UserPicture</b> - Display User picture from database URL: <a href="/gateway/exec/UserPicture">/gateway/exec/UserPicture</a></li>
            <li><b>UserManagementOverview</b> - List User, Groups, Roles and Privileges URL: <a href="/gateway/exec/UserManagementOverview">/gateway/exec/UserManagementOverview</a></li>
            <li><b>AddEntry</b> - Add User, Groups, Roles and Privileges, required BASE64 values for parameter URL: <a href="/gateway/exec/AddEntry?mod=&id=&desc">/gateway/exec/AddEntry?mod=&id=&desc=</a></li>
            <li><b>DeleteEntry</b> - Delete User, Groups, Roles and Privileges, required BASE64 values for parameter URL: <a href="/gateway/exec/DeleteEntry?mod=&id=">/gateway/exec/DeleteEntry?mod=&id=</a></li>
            <li><b>ActivateUser</b> - Activate/deactivate User, required BASE64 values for parameter URL: <a href="/gateway/exec/ActivateUser?mod=&id=">/gateway/exec/ActivateUser?mod=&id=</a></li>
            <li><b>PermissionRolePrivilege</b> - List Role - Privilege Mapping URL: <a href="/gateway/exec/PermissionRolePrivilege">/gateway/exec/PermissionRolePrivilege</a></li>
            <li><b>UpdateRolePriv</b> - Update Role with Privilege, required BASE64 values for parameter URL: <a href="/gateway/exec/UpdateRolePriv?rlid=&prid=">/gateway/exec/UpdateRolePriv?lrid=&prid=</a></li>
            <li><b>PermissionGroupRole</b> - List Group - Role Mapping URL: <a href="/gateway/exec/PermissionGroupRole">/gateway/exec/PermissionGroupRole</a></li>
            <li><b>UpdateGroupRole</b> - Update Group with Role, required BASE64 values for parameter URL: <a href="/gateway/exec/UpdateGroupRole?grid=&rlid=">/gateway/exec/UpdateGroupRole?grid=&rlid=</a></li>
            <li><b>PermissionUserGroup</b> - List User - Group Mapping URL: <a href="/gateway/exec/PermissionUserGroup">/gateway/exec/PermissionUserGroup</a></li>
            <li><b>UpdateUserGroup</b> - Update User with Group, required BASE64 values for parameter URL: <a href="/gateway/exec/UpdateUserGroup?uuid=&grid=">/gateway/exec/UpdateUserGroup?uuid=&grid=</a></li>
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
