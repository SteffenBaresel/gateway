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
        <!--GATEWAY_ENGINE:3.0.150121-->
        <center>
            <h2>kVASy&reg; System Control - Gateway Engine 3.0.150121</h2>
            <p>
                <font size=2>Entwicklungslinie 3 Build 2015.01</font>
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
        <!--GATEWAY_VERSION:2.3.140303-->
        Modul: <i><b>Repository</b></i><br>
        Version: <i><b>2.3.140303</b></i><br>
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
            <li><b>GetCustomerRole</b> - Get Role Customer Mapping URL: <a href="/gateway/exec/GetCustomerRole">/gateway/exec/GetCustomerRole</a></li>
            <li><b>GetContractRole</b> - Get Role Contract Mapping URL: <a href="/gateway/exec/GetContractRole">/gateway/exec/GetContractRole</a></li>
            <li><b>UpdateCustomerRole</b> - Update Customer with Role URL: <a href="/gateway/exec/UpdateCustomerRole?cuid=&rlid=">/gateway/exec/UpdateCustomerRole?cuid=&rlid=</a></li>
            <li><b>UpdateContractRole</b> - Update Contract with Role URL: <a href="/gateway/exec/UpdateContractRole?ccid=&rlid=">/gateway/exec/UpdateContractRole?ccid=&rlid=</a></li>
            <li><b>ResetDashboard</b> - Reset Dashboard Configuration URL: <a href="/gateway/exec/ResetDashboard">/gateway/exec/ResetDashboard</a></li>
            <li><b>CreateMailEntry</b> Create Mail Entry, required BASE64 values for parameter URL: <a href="/gateway/exec/CreateMailEntry?msid=uuid=&cuid=&ccid=&mto=&mcc=&msubject=&mbody=&mesc">/gateway/exec/CreateMailEntry?msid=uuid=&cuid=&ccid=&mto=&mcc=&msubject=&mbody=&mesc</a>
            <li><b>GetCustomerServiceEntry</b> Get Service Entry for one Customer, required BASE64 values for parameter URL: <a href="/gateway/exec/GetCustomerServiceEntry?uuid=&cuid=&offset=&limit=">/gateway/exec/GetCustomerServiceEntry?uuid=&cuid=&offset=&limit=</a>
            <li><b>GetCustomerServiceEntries</b> Get Configured Customer by search URL: <a href="/gateway/exec/GetCustomerServiceEntries?uuid=&cuid=">/gateway/exec/GetCustomerServiceEntries?uuid=&cuid=</a>
            <li><b>MonitoringMailingOverview</b> - List configured Mailgroups and Mailschedules URL: <a href="/gateway/exec/MonitoringMailingOverview">/gateway/exec/MonitoringMailingOverview</a></li>
        </ul>
        </font>
        <br></br>        
        <hr>
        <br></br>
        <!--MONITORING_VERSION:3.0.140303-->
        Modul: <i><b>Monitoring</b></i><br>
        Version: <i><b>3.0.140303</b></i><br>
        Comments: -
        <br></br>
        Format: <i><b>JSON</b></i><br>
        <font size=2>Query:<br>
        <ul>
            <li><b>BigTaov</b> - Get Values for Big Taov Module URL: <a href="/gateway/monitoring/BigTaov">/gateway/monitoring/BigTaov</a></li>
            <li><b>LivetickerEntries</b> - Get Liveticker Entries URL: <a href="/gateway/monitoring/LivetickerEntries">/gateway/monitoring/LivetickerEntries</a></li>
            <li><b>MonitoringFull</b> - Get Full Monitoring Information URL: <a href="/gateway/monitoring/MonitoringFull">/gateway/monitoring/MonitoringFull</a></li>
            <li><b>HostInfo</b> - Get Full Monitoring Information URL: <a href="/gateway/monitoring/HostInfo?hstid=">//gateway/monitoring/HostInfo?hstid=</a></li>
            <li><b>ServiceHistory</b> - Get Full Monitoring Information URL: <a href="/gateway/monitoring/ServiceHistory?srvid=">/gateway/monitoring/ServiceHistory?srvid=</a></li>
            <li><b>ServiceAvail</b> - Get Full Monitoring Information URL: <a href="/gateway/monitoring/ServiceAvail?srvid=">/gateway/monitoring/ServiceAvaill?srvid=</a></li>
            <li><b>ServiceReCheck</b> - Reschedule Service Check URL: <a href="/gateway/monitoring/ServiceReCheck?user=&hstid=&srvid=&ts=&instid=">/gateway/monitoring/ServiceReCheck?user=&hstid=&srvid=&ts=&instid=</a></li>
            <li><b>GetCustomer</b> - Get configured Customer for Host URL: <a href="/gateway/monitoring/GetCustomer?hstid=">/gateway/monitoring/GetCustomer?hstid=</a></li>
            <li><b>GetCustomerContractNumbers</b> - Get Customer & Host assigned Contract Numbers, required BASE64 values for parameter URL: <a href="/gateway/monitoring/GetCustomerContractNumbers?cuid=&hstid=">/gateway/monitoring/GetCustomerContractNumbers?cuid=&hstid=</a></li>
            <li><b>GetCustomerMailing</b> - Get Customer for Host To,Esk1,Esk2,Esk3 mail Configurations, required BASE64 values for parameter URL: <a href="/gateway/monitoring/GetCustomerMailing?cuid=&uuid=&hstid=">/gateway/monitoring/GetCustomerMailing?cuid=&uuid=&hstid=</a></li>
            <li><b>AcknowledgeService</b> Acknowledge Service Problem, required BASE64 values for parameter URL: <a href="/gateway/monitoring/AcknowledgeService?hstna=&hstid=&srvna=&srvid=uuid=&cuid=&ccid=&enid=&tm=&dl=&co=">/gateway/monitoring/AcknowledgeService?hstna=&hstid=&srvna=&srvid=&uuid=&cuid=&ccid=&enid=&tm=&dl=&co=</a>
            <li><b>GetAcknowledgement</b> - Get configured Acknowledgement URL: <a href="/gateway/monitoring/GetAcknowledgement?ackid=">/gateway/monitoring/GetAcknowledgement?ackid=</a></li>
            <li><b>GetHostCustomer</b> - Get Host Customer Mapping URL: <a href="/gateway/monitoring/GetHostCustomer">/gateway/monitoring/GetHostCustomer</a></li>
            <li><b>GetHostContract</b> - Get Host Contract Mapping URL: <a href="/gateway/monitoring/GetHostContract">/gateway/monitoring/GetHostContract</a></li>
            <li><b>GetHostRole</b> - Get Host Role Mapping URL: <a href="/gateway/monitoring/GetHostRole">/gateway/monitoring/GetHostRole</a></li>
            <li><b>UpdateHostRole</b> - Update Customer with Role URL: <a href="/gateway/exec/UpdateHostRole?hstid=&rlid=">/gateway/exec/UpdateHostRole?hstid=&rlid=</a></li>
            <li><b>UpdateHostCustomer</b> - Update Contract with Role URL: <a href="/gateway/exec/UpdateHostCustomer?hstid=&cuid=">/gateway/exec/UpdateHostCustomer?hstid=&cuid=</a></li>
            <li><b>UpdateHostContract</b> - Update Contract with Role URL: <a href="/gateway/exec/UpdateHostContract?hstid=&ccid=">/gateway/exec/UpdateHostContract?hstid=&ccid=</a></li>
            <li><b>ServiceDowntime</b> Downtime Service, required BASE64 values for parameter URL: <a href="/gateway/monitoring/ServiceDowntime?hstid=&srvid=&instid=&cuid=&ccid=&dstart=&dend=&comment=">/gateway/monitoring/ServiceDowntime?hstid=&srvid=&instid=&cuid=&ccid=&dstart=&dend=&comment=</a>
            <li><b>ServiceStatus</b> List Services by state, required BASE64 values for parameter URL: <a href="/gateway/monitoring/ServiceStatus?s=">/gateway/monitoring/ServiceStatus?s=</a>
            <li><b>CurProbs</b> List current Services Problems, required BASE64 values for parameter URL: <a href="/gateway/monitoring/CurProbs">/gateway/monitoring/CurProbs</a>
            <li><b>DbStatus</b> List Database Services by state, required BASE64 values for parameter URL: <a href="/gateway/monitoring/DbStatus?s=">/gateway/monitoring/DbStatus?s=</a>
        </ul>
        </font>
        <br></br>
        <hr>
        <br></br>
        <!--SEARCH_VERSION:3.0.140805-->
        Modul: <i><b>Search</b></i><br>
        Version: <i><b>3.0.140805</b></i><br>
        Comments: -
        <br></br>
        Format: <i><b>JSON</b></i><br>
        <font size=2>Query:<br>
        <ul>
            <li><b>Autocomplete</b> - Get Values for Autocomplete, input BASE64 encoded URL: <a href="/gateway/search/Autocomplete?t=&v=">/gateway/search/Autocomplete?t=&v=</a></li>
            <li><b>SearchHosts</b> - Search for Hosts, input BASE64 encoded URL: <a href="/gateway/search/SearchHosts?v=">/gateway/search/SearchHosts?v=</a></li>
            <li><b>SearchServices</b> - Search for Services, input BASE64 encoded URL: <a href="/gateway/search/SearchServices?v=">/gateway/search/SearchServices?v=</a></li>
            <li><b>SearchCustomer</b> - Search for Customer, input BASE64 encoded URL: <a href="/gateway/search/SearchCustomer?v=">/gateway/search/SearchCustomer?v=</a></li>
            <li><b>SearchCustomerServiceEntries</b> - Search for Customer Service Entries, input BASE64 encoded URL: <a href="/gateway/search/SearchCustomerServiceEntries?v=">/gateway/search/SearchCustomerServiceEntries?v=</a></li>
        </ul>
        </font>
        <br></br>
        <hr>
        <br></br>
        <!--REPORTING:3.0.150121-->
        Modul: <i><b>Reporting</b></i><br>
        Version: <i><b>3.0.150121</b></i><br>
        Comments: -
        <br></br>
        Format: <i><b>JSON</b></i><br>
        <font size=2>Query:<br>
        <ul>
            <li><b>TestReport</b> - Test Report URL: <a href="/gateway/reporting/TestReport">/gateway/reporting/TestReport</a></li>    
        </ul>
        </font>
        <br></br>
        <center><font size="1"><b><br>2015 Steffen Baresel</b></font></center>
    </body>
</html>
