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
        <!--GATEWAY_ENGINE:2.3.131203-->
        <center>
            <h2>kVASy&reg; System Control - Gateway Engine 2.3.131203</h2>
            <p>
                <font size=2>Entwicklungslinie 2 Update 3<!-- Author: Steffen Baresel --></font>
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
        <!--GATEWAY_VERSION:2.3.131203-->
        Modul: <i><b>Repository</b></i><br>
        Version: <i><b>2.3.131203</b></i><br>
        Comments: -
        <br></br>
        Format: <i><b>JSON</b></i><br>
        <font size=2>Query:<br>
        <ul>
            <li><b>GetUserConfig</b> - List User Configuration From Repository URL: <a href="/gateway/exec/GetUserConfig">/gateway/exec/GetUserConfig</a></li>
            <li><b>WhoIsLoggedIn</b> - List Logged In Users URL: <a href="/gateway/exec/WhoIsLoggedIn">/gateway/exec/WhoIsLoggedIn</a></li>
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
            <li><b>GetCustomer</b> - Get configured Customer URL: <a href="/gateway/exec/GetCustomer">/gateway/exec/GetCustomer</a></li>
            <li><b>CreateCustomer</b> - Create Customer, required BASE64 values for parameter URL: <a href="/gateway/exec/CreateCustomer?cname=&cnumber=&cmail=&cesmail=&caddress=&ccomm=&ct1=&ct1an=&ct1pv=&ct1pi=&ct2=&ct2an=&ct2pv=&ct2pi=&ct3=&ct3an=&ct3pv=&ct3pi=&ct4=&ct4an=&ct4pv=&ct4pi=&ct5=&ct5an=&ct5pv=&ct5pi=&ct6=&ct6an=&ct6pv=&ct6pi=">/gateway/exec/CreateCustomer?cname=&cnumber=&cmail=&cesmail=&caddress=&ccomm=&ct1=&ct1an=&ct1pv=&ct1pi=&ct2=&ct2an=&ct2pv=&ct2pi=&ct3=&ct3an=&ct3pv=&ct3pi=&ct4=&ct4an=&ct4pv=&ct4pi=&ct5=&ct5an=&ct5pv=&ct5pi=&ct6=&ct6an=&ct6pv=&ct6pi=</a></li>
            <li><b>GetSingleCustomer</b> - Get single configured Customer, required BASE64 values for parameter URL: <a href="/gateway/exec/GetSingleCustomer?cuid">/gateway/exec/GetSingleCustomer?cuid=</a></li>
            <li><b>UpdateCustomer</b> - Update Customer, required BASE64 values for parameter URL: <a href="/gateway/exec/UpdateCustomer?cuid=&cname=&cnumber=&cmail=&cesmail=&caddress=&ccomm=&ct1=&ct1an=&ct1pv=&ct1pi=">/gateway/exec/UpdateCustomer?cuid=&cname=&cnumber=&cmail=&cesmail=&caddress=&ccomm=&ct1=&ct1an=&ct1pv=&ct1pi=</a></li>
            <li><b>DeleteContract</b> - Delete Contract assigned to Customer, required BASE64 values for parameter URL: <a href="/gateway/exec/DeleteContract?cuid=&ccid=">/gateway/exec/DeleteContract?cuid=&ccid=</a></li>
            <li><b>DeleteCustomer</b> - Delete Customer, required BASE64 values for parameter URL: <a href="/gateway/exec/DeleteCustomer?cuid=">/gateway/exec/DeleteCustomer?cuid=</a></li>
            <li><b>GetContractTypes</b> - Get configured Contract Types URL: <a href="/gateway/exec/GetContractTypes">/gateway/exec/GetContractTypes</a></li>
            <li><b>CreateContractType</b> - Create Contract Type, required BASE64 values for parameter URL: <a href="/gateway/exec/CreateContractType?cotrsn=&cotrln=&mactions=">/gateway/exec/CreateContractType?cotrsn=&cotrln=&mactions=</a></li>
            <li><b>GetSingleContractType</b> - Get single configured Contract, required BASE64 values for parameter URL: <a href="/gateway/exec/GetSingleContractType?cttyid">/gateway/exec/GetSingleContractType?cttyid=</a></li>
            <li><b>UpdateContractType</b> - Update Contract Type, required BASE64 values for parameter URL: <a href="/gateway/exec/UpdateContractType?cttyid=&cotrsn=&cotrln=&mactions=">/gateway/exec/UpdateContractType?cttyid=&cotrsn=&cotrln=&mactions=</a></li>
            <li><b>DeleteContractType</b> - Delete Contract Type, required BASE64 values for parameter URL: <a href="/gateway/exec/DeleteContractType?cttyid=">/gateway/exec/DeleteContractType?cttyid=</a></li>
            <li><b>GetCommentTypes</b> - Get configured Comment Types URL: <a href="/gateway/exec/GetCommentTypes">/gateway/exec/GetCommentTypes</a></li>
            <li><b>GetCustomerContractNumbers</b> - Get Customer assigned Contract Numbers, required BASE64 values for parameter URL: <a href="/gateway/exec/GetCustomerContractNumbers?cuid=">/gateway/exec/GetCustomerContractNumbers?cuid=</a></li>
            <li><b>GetConfigMailFormat</b> - Get configured Mail Format URL: <a href="/gateway/exec/GetConfigMailFormat">/gateway/exec/GetConfigMailFormat</a></li>
            <li><b>AddMailingConfig</b> - Add User mail Configuration, required BASE64 values for parameter URL: <a href="/gateway/exec/AddMailingConfig?uuid=&key=&val=">/gateway/exec/AddMailingConfig?uuid=&key=&val=</a></li>
            <li><b>GetMailConfig</b> - Get Global mail Configuration URL: <a href="/gateway/exec/GetMailConfig">/gateway/exec/GetMailConfig</a></li>
            <li><b>AddMailConfig</b> - Add Global mail Configuration, required BASE64 values for parameter URL: <a href="/gateway/exec/AddMailConfig?key=&val=">/gateway/exec/AddMailConfig?key=&val=</a></li>
            <li><b>GetUserMailFormat</b> - Get Summary of all mail Configurations, required BASE64 values for parameter URL: <a href="/gateway/exec/GetUserMailFormat?uuid=">/gateway/exec/GetUserMailFormat?uuid=</a></li>
            <li><b>GetCustomerMailing</b> - Get Customer To,Esk1,Esk2,Esk3 mail Configurations, required BASE64 values for parameter URL: <a href="/gateway/exec/GetCustomerMailing?cuid=&uuid=">/gateway/exec/GetCustomerMailing?cuid=&uuid=</a></li>
            <li><b>CreateServiceEntry</b> Create Service Entry, required BASE64 values for parameter URL: <a href="/gateway/exec/CreateServiceEntry?uuid=&cuid=&ccid=&enid=&tm=&dl=&co=">/gateway/exec/CreateServiceEntry?uuid=&cuid=&ccid=&enid=&tm=&dl=&co=</a>
            <li><b>GetServiceEntry</b> Get Service Entry, required BASE64 values for parameter URL: <a href="/gateway/exec/GetServiceEntry?uuid=">/gateway/exec/GetServiceEntry?uuid=</a>
            <li><b>AutoCompleteCustomer</b> Get Auto Complete Customer Infos, required BASE64 values for parameter URL: <a href="/gateway/exec/AutoCompleteCustomer?cunm=">/gateway/exec/AutoCompleteCustomer?cunm=</a>
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
