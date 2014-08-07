/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.modules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author sbaresel
 */
public class Search {
    // Search Autocomplete
    
    static Properties props = null;
    
    static public String Autocomplete(String Uid, String Type, String Val) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String line = "";
        
        /*
         * Get User Roles
         */
        
        String sor = "";
        String sorr = "";
        ResultSet rsUro = Functions.GetUserRoles(Uid);
        while(rsUro.next()) {
            sor+= "e.rlid=" + rsUro.getString( 1 ) + " or ";
            sorr+= "f.rlid=" + rsUro.getString( 1 ) + " or ";
        }
        sor = sor.substring(0, sor.length()-4);
        sorr = sorr.substring(0, sorr.length()-4);
        
        /*
         * build connections
         */
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring");
        Connection cn = ds.getConnection();
        
        Context ctxr = new InitialContext(); 
        DataSource dsr  = (DataSource) ctxr.lookup("jdbc/repository");
        Connection cnr = dsr.getConnection();
        
        /*
         * select type
         */
        
        if (Base64Coder.decodeString(Type).equals("Host")) {
        
            line = "{\"AC\":[";
            String sql = "select a.hstln,a.hstid,a.ipaddr,b.htypln,a.instid from monitoring_info_host a, class_hosttypes b, monitoring_host_role_mapping e where a.htypid=b.htypid and a.hstid=e.hstid and ( " + sor + " ) and a.hstln ~* ? group by a.hstln,a.hstid,a.ipaddr,b.htypln,a.instid order by a.hstln ASC";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1,Base64Coder.decodeString( Val ));
            ResultSet rs = ps.executeQuery();
            
            while ( rs.next() ) {
                line+= "{\"HOST_NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 1 ) ) ) + "\",\"HOST_ID\":\"" + rs.getString( 2 ) + "\",\"IP\":\"" + Base64Coder.encodeString( rs.getString( 3 ) ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 4 ) ) ) + "\",\"INST_ID\":\"" + rs.getString( 5 ) + "\"},";
            }
            
            line = line.substring(0, line.length()-1); line+= "]}";
        
        } else if (Base64Coder.decodeString(Type).equals("Service")) {
            
            line = "{\"AC\":[";
            String sql = "select c.srvid,c.srvna,a.hstln,a.instid from monitoring_info_host a, monitoring_info_service c, monitoring_host_role_mapping e where a.hstid=c.hstid and a.hstid=e.hstid and ( " + sor + " ) and c.srvna ~* ? group by c.srvid,c.srvna,a.hstln,a.instid order by c.srvna ASC";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1,Base64Coder.decodeString( Val ));
            ResultSet rs = ps.executeQuery();
            
            while ( rs.next() ) {
                line+= "{\"SERVICE_NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 2 ) ) ) + "\",\"SERVICE_ID\":\"" + rs.getString( 1 ) + "\",\"HOST_NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml(  rs.getString( 3 ) ) ) + "\",\"INST_ID\":\"" + rs.getString( 4 ) + "\"},";
            }
            
            line = line.substring(0, line.length()-1); line+= "]}";
            
        } else if (Base64Coder.decodeString(Type).equals("Customer")) {
            
            line = "{\"AC\":[";
            String sql = "SELECT a.cuid,a.cunr,decode(a.cunm,'base64') as cunm,decode(a.cuaddr,'base64') as cuaddr,decode(a.cumail,'base64') as cumail,decode(a.cueskmail,'base64') as cueskmail,decode(a.cucomm,'base64') as cucomm FROM managed_service_cinfo a, profiles_customer_role_mapping e WHERE a.cuid=e.cuid AND ( " + sor + " ) and ( encode(decode(a.cunm,'base64'),'escape') ~* ? OR encode(decode(a.cuaddr,'base64'),'escape') ~* ? OR to_char(a.cunr,'999999999999') ~* ? ) ORDER BY 3";
            PreparedStatement ps = cnr.prepareStatement(sql);
            ps.setString(1,Base64Coder.decodeString( Val ));
            ps.setString(2,Base64Coder.decodeString( Val ));
            ps.setString(3,Base64Coder.decodeString( Val ));
            ResultSet rs = ps.executeQuery();
        
            while (rs.next()) { 
                line+= "{\"CUID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\",\"CUADDR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(4) ) ) + "\",\"CUMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(5) ) ) + "\",\"CUESKMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(6) ) ) + "\",\"CUCOMM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(7) ) ) + "\"},";
            }
        
            line = line.substring(0, line.length()-1); line += "]}";
            
        }
        
        // Repository Customer / Contract
        
        /*
        
        String sqlSE = "select decode(c.usdc,'base64'),decode(c.usnm,'base64'),d.ccnr,decode(e.cotrln,'base64'),decode(b.cunm,'base64'),decode(a.comt,'base64'),a.delay,a.utim,a.esk,bit_length(c.upic) from managed_service_cservices a,managed_service_cinfo b,profiles_user c,managed_service_ccontracts d,class_contracttypes e, profiles_contract_role_mapping f where a.cuid=b.cuid and a.uuid=c.uuid and a.ccid=d.ccid and d.cttyid=e.cttyid and d.ccid=f.ccid and ( " + sorr + " ) order by a.msid DESC limit 20";
        PreparedStatement psr = cnr.prepareStatement(sqlSE);
        ResultSet rsr = psr.executeQuery();
        
        line+= "\"COMMENTS\":[";
        while ( rsr.next() ) {
            line+= "{\"NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsr.getString( 1 ) ) ) + "\",\"UID\":\"" + Base64Coder.encodeString( rsr.getString( 2 ) ) + "\",\"AN\":\"" + Base64Coder.encodeString( rsr.getString( 3 ) ) + "\",\"CONM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsr.getString( 4 ) ) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsr.getString( 5 ) ) ) + "\",\"TEXT\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsr.getString( 6 ) ) ) + "\",\"TS\":\"" + Base64Coder.encodeString( rsr.getString( 8 ) ) + "\",\"ESK\":\"" + Base64Coder.encodeString( rsr.getString( 9 ) ) + "\",\"PCTRL\":\"" + rsr.getString( 10 ) + "\"},";
        }
        line = line.substring(0, line.length()-1); line+= "],";
        */
        
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        cnr.close();
        return replace;
    }
    
    /*
     * Search Hosts
     */
    
    static public String SearchHosts(String Uid, String Val) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Get User Roles
         */
        
        String sor = "";
        ResultSet rsUro = Functions.GetUserRoles(Uid);
        while(rsUro.next()) {
            sor+= "e.rlid=" + rsUro.getString( 1 ) + " or ";
        }
        sor = sor.substring(0, sor.length()-4);
        
        /*
         * Get User Roles Ende
         */
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring");
        Connection cn = ds.getConnection(); 
        String tmphn="";
        /*
         * Monitoring Info
         */
        String line = "[";
        String sql9 = "select a.hstln,a.hstid,a.ipaddr,b.htypln,b.htypicon,c.srvid,c.srvna,d.current_state,decode(d.output,'base64'),d.created,a.instid,d.ack,d.ackid,d.dtm,d.dtmid from monitoring_info_host a, class_hosttypes b, monitoring_info_service c, monitoring_status d, monitoring_host_role_mapping e where a.htypid=b.htypid and a.hstid=c.hstid and c.srvid=d.srvid and a.hstid=e.hstid and ( " + sor + " ) and a.hstln ~* ? order by a.hstln ASC,d.current_state DESC,c.srvid DESC";
        PreparedStatement psLt = cn.prepareStatement(sql9);
        psLt.setString(1,Base64Coder.decodeString( Val ));
        ResultSet rsLt = psLt.executeQuery();
        while ( rsLt.next() ) {
            if (rsLt.getString( 1 ).equals(tmphn)) { } else { line = line.substring(0, line.length()-1); line+= "]},{\"HOST_NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsLt.getString( 1 ) ) ) + "\",\"HOST_ID\":\"" + rsLt.getString( 2 ) + "\",\"HOST_ADDRESS\":\"" + Base64Coder.encodeString( rsLt.getString( 3 ) ) + "\",\"HOST_TYLN\":\"" + Base64Coder.encodeString( rsLt.getString( 4 ) ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( rsLt.getString( 5 ) ) + "\",\"INSTID\":\"" + rsLt.getString( 11 ) + "\",\"SERVICES\":["; }
            line+= "{\"SERVICE_ID\":\"" + rsLt.getString( 6 ) + "\",\"SERVICE_NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsLt.getString( 7 ) ) ) + "\",\"STATE\":\"" + rsLt.getString( 8 ) + "\",\"OUTPUT\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsLt.getString( 9 ) ) ) + "\",\"CREATED\":\"" + rsLt.getString( 10 ) + "\",\"CREATED_ISO\":\"" + Basics.ConvertUtime( rsLt.getLong( 10 ) ) + "\",\"INSTID\":\"" + rsLt.getString( 11 ) + "\",\"ACK\":\"" + rsLt.getString( 12 ) + "\",\"ACKID\":\"" + rsLt.getString( 13 ) + "\",\"DTM\":\"" + rsLt.getString( 14 ) + "\",\"DTMID\":\"" + rsLt.getString( 15 ) + "\"},";
            tmphn = rsLt.getString( 1 );
        }
        line = line.substring(0, line.length()-1); line+= "]}"; line = line.substring(3);
        
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return "[" + replace + "]";
    }
    
    /*
     * Search Services
     */
    
    static public String SearchServices(String Uid, String Val) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        /*
         * Get User Roles
         */
        
        String sor = "";
        ResultSet rsUro = Functions.GetUserRoles(Uid);
        while(rsUro.next()) {
            sor+= "e.rlid=" + rsUro.getString( 1 ) + " or ";
        }
        sor = sor.substring(0, sor.length()-4);
        
        /*
         * Get User Roles Ende
         */
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring");
        Connection cn = ds.getConnection(); 
        String tmphn="";
        /*
         * Monitoring Info
         */
        String line = "[";
        String sql9 = "select a.hstln,a.hstid,a.ipaddr,b.htypln,b.htypicon,c.srvid,c.srvna,d.current_state,decode(d.output,'base64'),d.created,a.instid,d.ack,d.ackid,d.dtm,d.dtmid from monitoring_info_host a, class_hosttypes b, monitoring_info_service c, monitoring_status d, monitoring_host_role_mapping e where a.htypid=b.htypid and a.hstid=c.hstid and c.srvid=d.srvid and a.hstid=e.hstid and ( " + sor + " ) and c.srvna ~* ? order by a.hstln ASC,d.current_state DESC,c.srvid DESC";
        PreparedStatement psLt = cn.prepareStatement(sql9);
        psLt.setString(1,Base64Coder.decodeString( Val ));
        ResultSet rsLt = psLt.executeQuery();
        while ( rsLt.next() ) {
            if (rsLt.getString( 1 ).equals(tmphn)) { } else { line = line.substring(0, line.length()-1); line+= "]},{\"HOST_NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsLt.getString( 1 ) ) ) + "\",\"HOST_ID\":\"" + rsLt.getString( 2 ) + "\",\"HOST_ADDRESS\":\"" + Base64Coder.encodeString( rsLt.getString( 3 ) ) + "\",\"HOST_TYLN\":\"" + Base64Coder.encodeString( rsLt.getString( 4 ) ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( rsLt.getString( 5 ) ) + "\",\"INSTID\":\"" + rsLt.getString( 11 ) + "\",\"SERVICES\":["; }
            line+= "{\"SERVICE_ID\":\"" + rsLt.getString( 6 ) + "\",\"SERVICE_NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsLt.getString( 7 ) ) ) + "\",\"STATE\":\"" + rsLt.getString( 8 ) + "\",\"OUTPUT\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rsLt.getString( 9 ) ) ) + "\",\"CREATED\":\"" + rsLt.getString( 10 ) + "\",\"CREATED_ISO\":\"" + Basics.ConvertUtime( rsLt.getLong( 10 ) ) + "\",\"INSTID\":\"" + rsLt.getString( 11 ) + "\",\"ACK\":\"" + rsLt.getString( 12 ) + "\",\"ACKID\":\"" + rsLt.getString( 13 ) + "\",\"DTM\":\"" + rsLt.getString( 14 ) + "\",\"DTMID\":\"" + rsLt.getString( 15 ) + "\"},";
            tmphn = rsLt.getString( 1 );
        }
        line = line.substring(0, line.length()-1); line+= "]}"; line = line.substring(3);
        
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return "[" + replace + "]";
    }
    
    static public String SearchCustomer(String Uid, String Val) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        String sor = "";
        ResultSet rsUro = Functions.GetUserRoles(Uid);
        while(rsUro.next()) {
            sor+= "e.rlid=" + rsUro.getString( 1 ) + " or ";
        }
        sor = sor.substring(0, sor.length()-4);
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 

        String out = "{\"VAL\":\"" + Val + "\",\"VAL_DECODE\":\"" + Base64Coder.decodeString( Val ) + "\",\"CUSTOMER\":[";
        
        if (Base64Coder.decodeString( Val ).startsWith("#")) {
        
            String iVal = Base64Coder.decodeString( Val ).substring( Base64Coder.decodeString( Val ).indexOf( "#" )+1 );
            String sql = "SELECT a.cuid,a.cunr,decode(a.cunm,'base64') as cunm,decode(a.cuaddr,'base64') as cuaddr,decode(a.cumail,'base64') as cumail,decode(a.cueskmail,'base64') as cueskmail,decode(a.cucomm,'base64') as cucomm FROM managed_service_cinfo a, profiles_customer_role_mapping e WHERE a.cuid=e.cuid AND ( " + sor + " ) and a.cuid=? ORDER BY 3";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt( 1,Integer.parseInt( iVal ) );
            ResultSet rs = ps.executeQuery();

            //out+= "\"" + iVal + "\":\"" + sql + "\"\"";
            
            while (rs.next()) { 
                out+= "{\"CUID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\",\"CUADDR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(4) ) ) + "\",\"CUMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(5) ) ) + "\",\"CUESKMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(6) ) ) + "\",\"CUCOMM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(7) ) ) + "\"},";
            }
            out = out.substring(0, out.length()-1);
            out += "]}";
            
        } else {
            
            String sql = "SELECT a.cuid,a.cunr,decode(a.cunm,'base64') as cunm,decode(a.cuaddr,'base64') as cuaddr,decode(a.cumail,'base64') as cumail,decode(a.cueskmail,'base64') as cueskmail,decode(a.cucomm,'base64') as cucomm FROM managed_service_cinfo a, profiles_customer_role_mapping e WHERE a.cuid=e.cuid AND ( " + sor + " ) and ( encode(decode(a.cunm,'base64'),'escape') ~* ? OR encode(decode(a.cuaddr,'base64'),'escape') ~* ? OR to_char(a.cunr,'999999999999') ~* ? ) ORDER BY 3";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1,Base64Coder.decodeString( Val ));
            ps.setString(2,Base64Coder.decodeString( Val ));
            ps.setString(3,Base64Coder.decodeString( Val ));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) { 
                out+= "{\"CUID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\",\"CUADDR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(4) ) ) + "\",\"CUMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(5) ) ) + "\",\"CUESKMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(6) ) ) + "\",\"CUCOMM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(7) ) ) + "\"},";
            }
            out = out.substring(0, out.length()-1);
            out += "]}";
        
        }    
            
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    static public String SearchCustomerServiceEntries(String Uid, String Val, String Offset, String Limit) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        String sor = "";
        ResultSet rsUro = Functions.GetUserRoles(Uid);
        while(rsUro.next()) {
            sor+= "f.rlid=" + rsUro.getString( 1 ) + " or ";
        }
        sor = sor.substring(0, sor.length()-4);
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        String count = null;
        String out = null;
        
        if (Base64Coder.decodeString( Val ).startsWith("#")) {
        
            String iVal = Base64Coder.decodeString( Val ).substring( Base64Coder.decodeString( Val ).indexOf( "#" )+1 );
            String sqlC = "select count(*) from managed_service_cservices a,managed_service_cinfo b,profiles_user c,managed_service_ccontracts d,class_contracttypes e, profiles_contract_role_mapping f where a.cuid=b.cuid and a.uuid=c.uuid and a.ccid=d.ccid and d.cttyid=e.cttyid and d.ccid=f.ccid and ( " + sor + " ) and a.cuid=?";
            PreparedStatement psC = cn.prepareStatement(sqlC);
            psC.setInt( 1,Integer.parseInt( iVal ) );
            ResultSet rsC = psC.executeQuery();

            if ( rsC.next() ) { count = rsC.getString( 1 ); }

            String sqlSE = "select decode(c.usdc,'base64'),decode(c.usnm,'base64'),d.ccnr,decode(e.cotrln,'base64'),decode(b.cunm,'base64'),decode(a.comt,'base64'),a.delay,a.utim,a.esk,bit_length(c.upic) from managed_service_cservices a,managed_service_cinfo b,profiles_user c,managed_service_ccontracts d,class_contracttypes e, profiles_contract_role_mapping f where a.cuid=b.cuid and a.uuid=c.uuid and a.ccid=d.ccid and d.cttyid=e.cttyid and d.ccid=f.ccid and ( " + sor + " ) and a.cuid=? order by a.msid DESC offset ? limit ?";
            PreparedStatement ps = cn.prepareStatement(sqlSE);
            ps.setInt(1, Integer.parseInt( iVal ) );
            ps.setInt(2, Integer.parseInt( Offset ));
            ps.setInt(3, Integer.parseInt( Limit ));
            ResultSet rs = ps.executeQuery();

            out = "{\"COUNT\":\"" + Base64Coder.encodeString( count ) + "\",\"ROWS\":[";
            while ( rs.next() ) {
                out += "{\"NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 1 ) ) ) + "\",\"UID\":\"" + Base64Coder.encodeString( rs.getString( 2 ) ) + "\",\"AN\":\"" + Base64Coder.encodeString( rs.getString( 3 ) ) + "\",\"CONM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 4 ) ) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 5 ) ) ) + "\",\"TEXT\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 6 ) ) ) + "\",\"TS\":\"" + Base64Coder.encodeString( rs.getString( 8 ) ) + "\",\"ESK\":\"" + Base64Coder.encodeString( rs.getString( 9 ) ) + "\",\"PCTRL\":\"" + rs.getString( 10 ) + "\"},";
            }
            out = out.substring(0, out.length()-1); out += "]}";
            
        } else {
            
            String iVal = Base64Coder.decodeString( Val ).substring( Base64Coder.decodeString( Val ).indexOf( "#" )+1 );
            String sqlC = "select count(*) from managed_service_cservices a,managed_service_cinfo b,profiles_user c,managed_service_ccontracts d,class_contracttypes e, profiles_contract_role_mapping f where a.cuid=b.cuid and a.uuid=c.uuid and a.ccid=d.ccid and d.cttyid=e.cttyid and d.ccid=f.ccid and ( " + sor + " ) and ( encode(decode(b.cunm,'base64'),'escape') ~* ? OR encode(decode(b.cuaddr,'base64'),'escape') ~* ? OR to_char(b.cunr,'999999999999') ~* ? )";
            PreparedStatement psC = cn.prepareStatement(sqlC);
            psC.setString(1,Base64Coder.decodeString( Val ));
            psC.setString(2,Base64Coder.decodeString( Val ));
            psC.setString(3,Base64Coder.decodeString( Val ));
            ResultSet rsC = psC.executeQuery();

            if ( rsC.next() ) { count = rsC.getString( 1 ); }

            String sqlSE = "select decode(c.usdc,'base64'),decode(c.usnm,'base64'),d.ccnr,decode(e.cotrln,'base64'),decode(b.cunm,'base64'),decode(a.comt,'base64'),a.delay,a.utim,a.esk,bit_length(c.upic) from managed_service_cservices a,managed_service_cinfo b,profiles_user c,managed_service_ccontracts d,class_contracttypes e, profiles_contract_role_mapping f where a.cuid=b.cuid and a.uuid=c.uuid and a.ccid=d.ccid and d.cttyid=e.cttyid and d.ccid=f.ccid and ( " + sor + " ) and ( encode(decode(b.cunm,'base64'),'escape') ~* ? OR encode(decode(b.cuaddr,'base64'),'escape') ~* ? OR to_char(b.cunr,'999999999999') ~* ? ) order by a.msid DESC offset ? limit ?";
            PreparedStatement ps = cn.prepareStatement(sqlSE);
            ps.setString(1,Base64Coder.decodeString( Val ));
            ps.setString(2,Base64Coder.decodeString( Val ));
            ps.setString(3,Base64Coder.decodeString( Val ));
            ps.setInt(4, Integer.parseInt( Offset ));
            ps.setInt(5, Integer.parseInt( Limit ));
            ResultSet rs = ps.executeQuery();

            out = "{\"COUNT\":\"" + Base64Coder.encodeString( count ) + "\",\"ROWS\":[";
            while ( rs.next() ) {
                out += "{\"NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 1 ) ) ) + "\",\"UID\":\"" + Base64Coder.encodeString( rs.getString( 2 ) ) + "\",\"AN\":\"" + Base64Coder.encodeString( rs.getString( 3 ) ) + "\",\"CONM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 4 ) ) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 5 ) ) ) + "\",\"TEXT\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 6 ) ) ) + "\",\"TS\":\"" + Base64Coder.encodeString( rs.getString( 8 ) ) + "\",\"ESK\":\"" + Base64Coder.encodeString( rs.getString( 9 ) ) + "\",\"PCTRL\":\"" + rs.getString( 10 ) + "\"},";
            }
            out = out.substring(0, out.length()-1); out += "]}";
        
        }    
            
        String replace = out.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
}
