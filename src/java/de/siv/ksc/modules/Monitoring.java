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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author sbaresel
 */
public class Monitoring {
    
    static Properties props = null;
    
    static public String BigTaov(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String line = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring");
        Connection cn = ds.getConnection(); 
        /*
         * Host Status
         */
        line = "{\"HOSTS\":[";
        PreparedStatement psHst = cn.prepareStatement("select a.hstln,a.hstid,a.ipaddr,b.htypln,c.srvid,c.srvna,d.current_state,d.ack,d.ackid from monitoring_info_host a, class_hosttypes b, monitoring_info_service c, monitoring_status d where a.htypid=b.htypid and a.hstid=c.hstid and c.srvna like 'SYSTEM_ICMP_REQUEST' and c.srvid=d.srvid order by a.hstid ASC,d.current_state DESC");
        ResultSet rsHst = psHst.executeQuery();
        while ( rsHst.next() ) {
            line+= "{\"HOST_NAME\":\"" + Base64Coder.encodeString( rsHst.getString( 1 ) ) + "\",\"HOST_ID\":\"" + rsHst.getString( 2 ) + "\",\"IP\":\"" + rsHst.getString( 3 ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( rsHst.getString( 4 ) ) + "\",\"STATE\":\"" + rsHst.getString( 7 ) + "\",\"ACK\":\"" + rsHst.getString( 8 ) + "\",\"ACKID\":\"" + rsHst.getString( 9 ) + "\"},";
        }
        line = line.substring(0, line.length()-1); line+= "],\"SERVICES\":[";
        /*
         * Service Status
         */
        PreparedStatement psSrv = cn.prepareStatement("select a.hstln,a.hstid,a.ipaddr,b.htypln,c.srvid,c.srvna,d.current_state,d.ack,d.ackid from monitoring_info_host a, class_hosttypes b, monitoring_info_service c, monitoring_status d where a.htypid=b.htypid and a.hstid=c.hstid and c.srvna not like 'SYSTEM_ICMP_REQUEST' and c.srvid=d.srvid order by d.current_state DESC,c.srvid DESC");
        ResultSet rsSrv = psSrv.executeQuery();
        while ( rsSrv.next() ) {
            line+= "{\"HOST_NAME\":\"" + Base64Coder.encodeString( rsSrv.getString( 1 ) ) + "\",\"HOST_ID\":\"" + rsSrv.getString( 2 ) + "\",\"IP\":\"" + rsSrv.getString( 3 ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( rsSrv.getString( 4 ) ) + "\",\"SRV_ID\":\"" + rsSrv.getString( 5 ) + "\",\"SRV_NAME\":\"" + Base64Coder.encodeString( rsSrv.getString( 6 ) ) + "\",\"STATE\":\"" + rsSrv.getString( 7 ) + "\",\"ACK\":\"" + rsSrv.getString( 8 ) + "\",\"ACKID\":\"" + rsSrv.getString( 9 ) + "\"},";
        }
        line = line.substring(0, line.length()-1); line+= "],";
        /*
         * Host Taov
         */
        String up; up="0"; String down; down="0"; String unr; unr="0";
        PreparedStatement psSlHst = cn.prepareStatement("select a.current_state,count(a.current_state) from monitoring_status a, monitoring_info_service b where a.srvid=b.srvid and b.srvna like 'SYSTEM_ICMP_REQUEST' group by a.current_state");
        ResultSet rsSlHst = psSlHst.executeQuery();
        while ( rsSlHst.next() ) {
            if(rsSlHst.getString( 1 ).equals("1")) { down = rsSlHst.getString( 2 ); } else if (rsSlHst.getString( 1 ).equals("2")) { unr = rsSlHst.getString( 2 ); } else { up = rsSlHst.getString( 2 ); }
        }
        /*
         * Service Taov
         */
        String ok; ok="0"; String wa; wa="0"; String cr; cr="0"; String un; un="0";
        PreparedStatement psSlSrv = cn.prepareStatement("select a.current_state,count(a.current_state) from monitoring_status a, monitoring_info_service b where a.srvid=b.srvid and b.srvna not like 'SYSTEM_ICMP_REQUEST' group by a.current_state");
        ResultSet rsSlSrv = psSlSrv.executeQuery();
        while ( rsSlSrv.next() ) {
            if(rsSlSrv.getString( 1 ).equals("1")) { wa = rsSlSrv.getString( 2 ); } else if (rsSlSrv.getString( 1 ).equals("2")) { cr = rsSlSrv.getString( 2 ); } else if (rsSlSrv.getString( 1 ).equals("3")) { un = rsSlSrv.getString( 2 ); } else { ok = rsSlSrv.getString( 2 ); }
        }
        /*
         * Database Taov
         */
        Integer open; open=0; Integer stopped; stopped=0;
        PreparedStatement psSlDB = cn.prepareStatement("select b.current_state,count(b.current_state) from monitoring_oracle_database_info a, monitoring_status b where a.srvid=b.srvid group by b.current_state");
        ResultSet rsSlDB = psSlDB.executeQuery();
        while ( rsSlDB.next() ) {
            if (rsSlDB.getInt( 1 ) == 0) { open = rsSlDB.getInt( 2 ); } else { stopped = stopped + rsSlDB.getInt( 2 ); };
        }
        /*
         * Middleware Taov
         */
        Integer online; online=0; Integer offline; offline=0;
        PreparedStatement psSlMW = cn.prepareStatement("select b.current_state,count(b.current_state) from monitoring_oracle_middleware_info a, monitoring_status b where a.srvid=b.srvid group by b.current_state");
        ResultSet rsSlMW = psSlMW.executeQuery();
        while ( rsSlMW.next() ) {
            if (rsSlMW.getInt( 1 ) == 0) { online = rsSlMW.getInt( 2 ); } else { offline = offline + rsSlMW.getInt( 2 ); };
        }
        
        
        line+= "\"SLIMTAOV\":[{\"HOSTS\":[{\"UP\":\"" + up + "\",\"DOWN\":\"" + down + "\",\"UNREACHABLE\":\"" + unr + "\"}]},{\"SERVICES\":[{\"OK\":\"" + ok + "\",\"WARNING\":\"" + wa + "\",\"CRITICAL\":\"" + cr + "\",\"UNKNOWN\":\"" + un + "\"}]},{\"DATABASES\":[{\"OPEN\":\"" + open + "\",\"STOPPED\":\"" + stopped + "\"}]},{\"MIDDLEWARE\":[{\"ONLINE\":\"" + online + "\",\"OFFLINE\":\"" + offline + "\"}]}],\"LIVETICKER\":[";
        
        
        /*
         * Liveticker
         */
        long timestamp = (System.currentTimeMillis()/1000) - 1800;
        PreparedStatement psLt = cn.prepareStatement("select a.hstln,b.hstid,c.srvna,b.srvid,b.state,b.created from monitoring_info_host a,monitoring_state_change b,monitoring_info_service c where a.hstid=b.hstid and b.srvid=c.srvid and b.new_problem=1 and b.state>1 and b.created>? order by b.created desc");
        psLt.setLong(1,timestamp);
        ResultSet rsLt = psLt.executeQuery();
        while ( rsLt.next() ) {
            line+= "{\"HOST_NAME\":\"" + Base64Coder.encodeString( rsLt.getString( 1 ) ) + "\",\"HOST_ID\":\"" + rsLt.getString( 2 ) + "\",\"SERVICE_NAME\":\"" + Base64Coder.encodeString( rsLt.getString( 3 ) ) + "\",\"SERVICE_ID\":\"" + rsLt.getString( 4 ) + "\",\"STATE\":\"" + rsLt.getString( 5 ) + "\",\"CREATED\":\"" + rsLt.getString( 6 ) + "\"},";
        }
        line = line.substring(0, line.length()-1); line+= "]}";
        
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return replace;
    }
    
    static public String LivetickerEntries(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring");
        Connection cn = ds.getConnection(); 
        /*
         * Liveticker
         */
        String line = "{\"LIVETICKER\":[";
        long timestamp = (System.currentTimeMillis()/1000) - 1800;
        PreparedStatement psLt = cn.prepareStatement("select a.hstln,b.hstid,d.htypicon,d.htypln,c.srvna,b.srvid,b.state,b.output,b.created from monitoring_info_host a,monitoring_state_change b,monitoring_info_service c,class_hosttypes d where a.hstid=b.hstid and b.srvid=c.srvid and a.htypid=d.htypid and b.new_problem=1 and b.state>1 and b.created>? order by b.created desc");
        psLt.setLong(1,timestamp);
        ResultSet rsLt = psLt.executeQuery();
        while ( rsLt.next() ) {
            line+= "{\"HOST_NAME\":\"" + Base64Coder.encodeString( rsLt.getString( 1 ) ) + "\",\"HOST_ID\":\"" + rsLt.getString( 2 ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( rsLt.getString( 3 ) ) + "\",\"HOST_TYLN\":\"" + Base64Coder.encodeString( rsLt.getString( 4 ) ) + "\",\"SERVICE_NAME\":\"" + Base64Coder.encodeString( rsLt.getString( 5 ) ) + "\",\"SERVICE_ID\":\"" + rsLt.getString( 6 ) + "\",\"STATE\":\"" + rsLt.getString( 7 ) + "\",\"OUTPUT\":\"" + rsLt.getString( 8 ) + "\",\"CREATED\":\"" + rsLt.getString( 9 ) + "\",\"CREATED_ISO\":\"" + Basics.ConvertUtime( rsLt.getLong( 9 ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1); line+= "]}";
        
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return replace;
    }
    
    static public String MonitoringFull(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring");
        Connection cn = ds.getConnection(); 
        String tmphn="";
        /*
         * Monitoring Info
         */
        String line = "[";
        PreparedStatement psLt = cn.prepareStatement("select a.hstln,a.hstid,a.ipaddr,b.htypln,b.htypicon,c.srvid,c.srvna,d.current_state,d.output,d.created,a.instid,d.ack,d.ackid from monitoring_info_host a, class_hosttypes b, monitoring_info_service c, monitoring_status d where a.htypid=b.htypid and a.hstid=c.hstid and c.srvid=d.srvid order by a.hstln ASC,d.current_state DESC,c.srvid DESC");
        ResultSet rsLt = psLt.executeQuery();
        while ( rsLt.next() ) {
            if (rsLt.getString( 1 ).equals(tmphn)) { } else { line = line.substring(0, line.length()-1); line+= "]},{\"HOST_NAME\":\"" + Base64Coder.encodeString( rsLt.getString( 1 ) ) + "\",\"HOST_ID\":\"" + rsLt.getString( 2 ) + "\",\"HOST_ADDRESS\":\"" + Base64Coder.encodeString( rsLt.getString( 3 ) ) + "\",\"HOST_TYLN\":\"" + Base64Coder.encodeString( rsLt.getString( 4 ) ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( rsLt.getString( 5 ) ) + "\",\"INSTID\":\"" + rsLt.getString( 11 ) + "\",\"SERVICES\":["; }
            line+= "{\"SERVICE_ID\":\"" + rsLt.getString( 6 ) + "\",\"SERVICE_NAME\":\"" + Base64Coder.encodeString( rsLt.getString( 7 ) ) + "\",\"STATE\":\"" + rsLt.getString( 8 ) + "\",\"OUTPUT\":\"" + rsLt.getString( 9 ) + "\",\"CREATED\":\"" + rsLt.getString( 10 ) + "\",\"CREATED_ISO\":\"" + Basics.ConvertUtime( rsLt.getLong( 10 ) ) + "\",\"INSTID\":\"" + rsLt.getString( 11 ) + "\",\"ACK\":\"" + rsLt.getString( 12 ) + "\",\"ACKID\":\"" + rsLt.getString( 13 ) + "\"},";
            tmphn = rsLt.getString( 1 );
        }
        line = line.substring(0, line.length()-1); line+= "]}"; line = line.substring(3);
        
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return "[" + replace + "]";
    }
    
    static public String ServiceAvail(String Uid, Integer Srvid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring");
        Connection cn = ds.getConnection(); 
        /*
         * Service Availability
         */
        String line = "{}";
        PreparedStatement psLt = cn.prepareStatement("select sum(timeok),sum(timewa),sum(timecr),sum(timeun) from monitoring_availability where timeok>=0 and timewa>=0 and timecr>=0 and timeun>=0 and srvid=?");
        psLt.setInt(1,Srvid);
        ResultSet rsLt = psLt.executeQuery();
        while ( rsLt.next() ) {
            line = "{\"OK\":\"" + rsLt.getString( 1 ) + "\",\"WA\":\"" + rsLt.getString( 2 ) + "\",\"CR\":\"" + rsLt.getString( 3 ) + "\",\"UN\":\"" + rsLt.getString( 4 ) + "\"}";
        }
        
        cn.close();
        return line;
    }
    
    static public String ServiceHistory(String Uid, Integer Srvid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring");
        Connection cn = ds.getConnection();
        long tmstmpold = 0;
        long timestamp = (System.currentTimeMillis()/1000) - 2592000; // 30 Tage
        /*
         * Service History
         */
        String line = "[";
        PreparedStatement psLt = cn.prepareStatement("select a.srvna,b.current_state,b.output,b.perf_data,b.created from monitoring_info_service a, monitoring_status_history b where a.srvid=b.srvid and a.srvid=? and b.created>? order by b.created desc");
        psLt.setInt(1,Srvid);
        psLt.setLong(2,timestamp);
        ResultSet rsLt = psLt.executeQuery();
        while ( rsLt.next() ) {
            if (tmstmpold != rsLt.getLong( 5 )) {
                line+= "{\"SERVICE_NAME\":\"" + Base64Coder.encodeString( rsLt.getString( 1 ) ) + "\",\"STATE\":\"" + rsLt.getString( 2 ) + "\",\"OUTPUT\":\"" + rsLt.getString( 3 ) + "\",\"PERF_DATA\":\"" + rsLt.getString( 4 ) + "\",\"CREATED\":\"" + rsLt.getString( 5 ) + "\",\"CREATED_ISO\":\"" + Basics.ConvertUtime( rsLt.getLong( 5 ) ) + "\"},";
            }
            tmstmpold = rsLt.getLong( 5 );
        }
        line = line.substring(0, line.length()-1); line+= "]";
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return replace;
    }
    
    static public String HostInfo(String Uid, Integer Hstid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring");
        Connection cn = ds.getConnection(); 
        /*
         * Host Information
         */
        String line = "{";
        PreparedStatement psLt = cn.prepareStatement("select a.hstln,a.hstid,a.ipaddr,b.htypicon,b.htypln,d.current_state from monitoring_info_host a, class_hosttypes b, monitoring_info_service c, monitoring_status d where a.htypid=b.htypid and a.hstid=c.hstid and c.srvna like 'SYSTEM_ICMP_REQUEST' and c.srvid=d.srvid and a.hstid=?");
        psLt.setInt(1,Hstid);
        ResultSet rsLt = psLt.executeQuery();
        if( rsLt.next() ) {
            line += "\"HOST_NAME\":\"" + Base64Coder.encodeString( rsLt.getString( 1 ) ) + "\",\"HOST_ID\":\"" + rsLt.getString( 2 ) + "\",\"IP\":\"" + Base64Coder.encodeString( rsLt.getString( 3 ) ) + "\",\"HOST_ICON\":\"" + Base64Coder.encodeString( rsLt.getString( 4 ) ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( rsLt.getString( 5 ) ) + "\",\"STATE\":\"" + rsLt.getString( 6 ) + "\",\"CONTRACTS\":[";
            Context ctxR = new InitialContext(); 
            DataSource dsR  = (DataSource) ctxR.lookup("jdbc/repository"); 
            Connection cnR = dsR.getConnection(); 
            PreparedStatement psR = cnR.prepareStatement("select a.cuid,a.cunr,a.cunm,b.ccid,b.ccnr,e.cotrln from managed_service_cinfo a, managed_service_ccontracts b, monitoring_host_customer_mapping c, monitoring_host_contract_mapping d, class_contracttypes e where a.cuid=c.cuid and b.ccid=d.ccid and c.hstid=d.hstid and b.cttyid=e.cttyid and d.hstid=?");
            psR.setInt(1,Hstid);
            ResultSet rsR = psR.executeQuery();
            while(rsR.next()) {
                line+="{\"CUID\":\"" + Base64Coder.encodeString( rsR.getString(1) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( rsR.getString(2) ) + "\",\"CUNM\":\"" + rsR.getString(3) + "\",\"CCID\":\"" + Base64Coder.encodeString( rsR.getString(4) ) + "\",\"CCNR\":\"" + Base64Coder.encodeString( rsR.getString(5) ) + "\",\"CCNM\":\"" + rsR.getString(6) + "\"},";
            }
            line = line.substring(0, line.length()-1);
            line+= "]";
            cnR.close();
        }
        line+= "}";
        String replace = line.replace("\":]", "\":[]");
        cn.close();
        return replace;
    }
    
    /*
     * Insert Service Tasks
     */
    
    static public String ServiceReCheck(String Uid, String Hstid, String Srvid, String Ts, String Instid) throws FileNotFoundException, IOException, NamingException, SQLException, ParseException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement psD = cn.prepareStatement("insert into monitoring_task(type,hstid,srvid,done,usr,tsstart,tsend,comment,instid) values ('0',?,?,false,?,?,'0',?,?)");
        psD.setInt(1,Integer.parseInt( Hstid ));
        psD.setInt(2,Integer.parseInt( Srvid ));
        psD.setString(3, Base64Coder.encodeString(Uid) );
        psD.setInt(4,Integer.parseInt( Basics.ConvertDate( Base64Coder.decodeString(Ts) ) ));
        psD.setString(5, Base64Coder.encodeString("ReSchedule Service Check") );
        psD.setInt(6,Integer.parseInt( Instid ));
        psD.executeUpdate();
        /*
         * Close Connection
         */
        cn.close();
        return "1";
    }
    
    /* 
     * Customer
     */
    
    static public String GetCustomer(String Hstid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        /*
         * Select if host is already assigned to a Customer 
         */
        
        PreparedStatement ps = cn.prepareStatement("SELECT a.cuid,a.cunr,decode(a.cunm,'base64'),decode(a.cuaddr,'base64'),decode(a.cumail,'base64'),decode(a.cueskmail,'base64'),decode(a.cucomm,'base64') FROM managed_service_cinfo a, monitoring_host_customer_mapping b WHERE a.cuid=b.cuid AND b.hstid=? ORDER BY 3");
        ps.setInt(1,Integer.parseInt( Hstid ));
        ResultSet rs = ps.executeQuery();
        
        String out = "{\"CUSTOMER\":[";
        if (rs.next()) {
            out += "{\"CUID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\",\"CUADDR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(4) ) ) + "\",\"CUMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(5) ) ) + "\",\"CUESKMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(6) ) ) + "\",\"CUCOMM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(7) ) ) + "\"},";
            out = out.substring(0, out.length()-1);
        } else {
            PreparedStatement ps2 = cn.prepareStatement("SELECT cuid,cunr,decode(cunm,'base64'),decode(cuaddr,'base64'),decode(cumail,'base64'),decode(cueskmail,'base64'),decode(cucomm,'base64') FROM managed_service_cinfo ORDER BY 3");
            ResultSet rs2 = ps2.executeQuery();
        
            while (rs2.next()) { 
                //Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) )
                out += "{\"CUID\":\"" + Base64Coder.encodeString( rs2.getString(1) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( rs2.getString(2) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(3) ) ) + "\",\"CUADDR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(4) ) ) + "\",\"CUMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(5) ) ) + "\",\"CUESKMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(6) ) ) + "\",\"CUCOMM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(7) ) ) + "\"},";
            }
            out = out.substring(0, out.length()-1);
        }
        out += "]}";
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    static public String GetCustomerContractNumbers(String cuid, String hstid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("select a.ccid,a.ccnr,decode(b.cotrln,'base64') from managed_service_ccontracts a,class_contracttypes b, monitoring_host_contract_mapping c where a.cttyid=b.cttyid AND a.ccid=c.ccid AND a.cuid=? AND c.hstid=?");
        ps.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        ps.setInt(2,Integer.parseInt( hstid ));
        ResultSet rs = ps.executeQuery();

        String out = "{\"CONTRACT\":[";
        
        if(rs.next()) {
            out += "{\"CCID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"CCNR\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"COTRLN\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\"},";
            out = out.substring(0, out.length()-1);
        } else {
            PreparedStatement ps2 = cn.prepareStatement("select a.ccid,a.ccnr,decode(b.cotrln,'base64') from managed_service_ccontracts a,class_contracttypes b where a.cttyid=b.cttyid AND a.cuid=?");
            ps2.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) { 
                out += "{\"CCID\":\"" + Base64Coder.encodeString( rs2.getString(1) ) + "\",\"CCNR\":\"" + Base64Coder.encodeString( rs2.getString(2) ) + "\",\"COTRLN\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(3) ) ) + "\"},";
            }
            out = out.substring(0, out.length()-1);
        }
        out += "]}";
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    /*
     * Acknowledge Service Problem
     */
    
    static public String AcknowledgeService(String hstid, String srvid, String instid, String uuid, String cuid, String ccid, String tm, String co) throws FileNotFoundException, IOException, NamingException, SQLException, ParseException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "1";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        /* Check if host assigned to customer */
        
        PreparedStatement psHid = cn.prepareStatement("SELECT a.cuid,a.cunr,decode(a.cunm,'base64'),decode(a.cuaddr,'base64'),decode(a.cumail,'base64'),decode(a.cueskmail,'base64'),decode(a.cucomm,'base64') FROM managed_service_cinfo a, monitoring_host_customer_mapping b WHERE a.cuid=b.cuid AND b.hstid=? ORDER BY 3");
        psHid.setInt(1,Integer.parseInt( Base64Coder.decodeString( hstid ) ));
        ResultSet rsHid = psHid.executeQuery();
        
        if (!rsHid.next()) {
            PreparedStatement psIac = cn.prepareStatement("INSERT INTO monitoring_host_customer_mapping(HSTID,CUID) VALUES (?,?)");
            psIac.setInt(1, Integer.parseInt( Base64Coder.decodeString( hstid ) ));
            psIac.setInt(2, Integer.parseInt( Base64Coder.decodeString( cuid ) ));
            psIac.executeUpdate();
        } 
        
        /* Check if host assigned to contract */
        
        PreparedStatement psSid = cn.prepareStatement("select a.ccid,a.ccnr,decode(b.cotrln,'base64') from managed_service_ccontracts a,class_contracttypes b, monitoring_host_contract_mapping c where a.cttyid=b.cttyid AND a.ccid=c.ccid AND a.cuid=? AND c.hstid=?");
        psSid.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        psSid.setInt(2,Integer.parseInt( Base64Coder.decodeString( hstid ) ));
        ResultSet rsSid = psSid.executeQuery();
        
        if (!rsSid.next()) {
            PreparedStatement psSac = cn.prepareStatement("INSERT INTO monitoring_host_contract_mapping(HSTID,CCID) VALUES (?,?)");
            psSac.setInt(1, Integer.parseInt( Base64Coder.decodeString( hstid ) ));
            psSac.setInt(2, Integer.parseInt( Base64Coder.decodeString( ccid ) ));
            psSac.executeUpdate();
        }
        
        /*
         * Close Repository Connection
         */
        cn.close();
        
        /*
         * Open Monitoring Connection
         */
        
        Context ctxM = new InitialContext(); 
        DataSource dsM  = (DataSource) ctxM.lookup("jdbc/monitoring"); 
        Connection cnM = dsM.getConnection(); 
        
        /* Insert Acknowledge Command to task Table */
        
        PreparedStatement psD = cnM.prepareStatement("insert into monitoring_task(type,hstid,srvid,done,usr,tsstart,tsend,comment,instid) values ('4',?,?,false,?,?,'0',?,?)");
        psD.setInt(1,Integer.parseInt( Base64Coder.decodeString( hstid ) ));
        psD.setInt(2,Integer.parseInt( Base64Coder.decodeString( srvid ) ));
        psD.setString(3, Base64Coder.encodeString(uuid) );
        psD.setInt(4,Integer.parseInt( Basics.ConvertDate( Base64Coder.decodeString( tm ) ) ));
        psD.setString(5, co.replace("78", "+") );
        psD.setInt(6,Integer.parseInt( Base64Coder.decodeString( instid ) ));
        psD.executeUpdate();
        /*
         * Close Repository Connection
         */
        cnM.close();
        
        return out;
    }
    
    static public String GetAcknowledgement(String ackid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/monitoring"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("select usr,comment,ts from monitoring_acknowledge where ackid=?");
        ps.setInt(1,Integer.parseInt( ackid ));
        ResultSet rs = ps.executeQuery();

        String out = "";
        
        if(rs.next()) {
            Context ctxR = new InitialContext(); 
            DataSource dsR  = (DataSource) ctxR.lookup("jdbc/repository"); 
            Connection cnR = dsR.getConnection(); 
            PreparedStatement psR = cnR.prepareStatement("select usdc from profiles_user where usnm=?");
            psR.setString(1,rs.getString(1));
            ResultSet rsR = psR.executeQuery();
            if(rsR.next()) {
                out += "{\"USER\":\"" + rsR.getString(1) + "\",\"COMMENT\":\"" + rs.getString(2) + "\",\"CREATED\":\"" + rs.getString( 3 ) + "\",\"CREATED_ISO\":\"" + Basics.ConvertUtime( rs.getLong( 3 ) ) + "\"}";
            }
            cnR.close();
        }
        
        cn.close();
        return out;
    }
    
    /*
     * Host Customer Mapping for Configuration
     */
    
    static public String GetHostCustomer() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        /* Require Base64 Value for Parameter Id */
        String line = "";
        Context ctxR = new InitialContext(); 
        DataSource dsR  = (DataSource) ctxR.lookup("jdbc/repository"); 
        Connection cnR = dsR.getConnection();
        
        Context ctxM = new InitialContext(); 
        DataSource dsM  = (DataSource) ctxM.lookup("jdbc/monitoring"); 
        Connection cnM = dsM.getConnection();
        
        
        PreparedStatement pscu = cnR.prepareStatement("SELECT cuid,decode(cunm,'base64'),cunr FROM managed_service_cinfo order by 3");
        ResultSet cu = pscu.executeQuery();
        PreparedStatement psht = cnM.prepareStatement("SELECT hstid,hstln,ipaddr FROM monitoring_info_host order by 2");
        ResultSet ht = psht.executeQuery();
        PreparedStatement psma = cnR.prepareStatement("SELECT hstid,cuid FROM monitoring_host_customer_mapping");
        ResultSet ma = psma.executeQuery();
        
        List<String> mahtid = new ArrayList<String>();
        List<String> macuid = new ArrayList<String>();
        
        while (ma.next()) {
            mahtid.add(ma.getString( 1 ));
            macuid.add(ma.getString( 2 ));
        }
        
        line = "{\"CUSTOMER\":[";
        while (cu.next()) {
            line += "{\"CUID\":\"" + cu.getString( 1 ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cu.getString( 2 ) ) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cu.getString( 3 ) ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1);
        line += "],\"HOST\":[";
        while (ht.next()) {
            String htid = ht.getString( 1 );
            String htnm = ht.getString( 2 );
            String htip = ht.getString( 3 );
            line += "{\"HOST_NM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( htnm ) ) + "\",\"HOST_IP\":\"" + Base64Coder.encodeString( Basics.encodeHtml( htip ) ) + "\",\"HOST_ID\":\"" + htid + "\",\"HOST_GROUP\":[";
            for (int i=0;i<mahtid.size();i++) {
                if (htid.equals(mahtid.get(i))) {
                    line += "\"" + macuid.get(i) + "\",";
                }
            }
            line = line.substring(0, line.length()-1);
            line += "]},";
        }
        line = line.substring(0, line.length()-1);
        line += "]}";
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        /*
         * Close Connection
         */
        cnR.close();
        cnM.close();
        return replace;
    }
    
    /*
     * Host Contract Mapping for Configuration
     */
    
    static public String GetHostContract() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        /* Require Base64 Value for Parameter Id */
        String line = "";
        Context ctxR = new InitialContext(); 
        DataSource dsR  = (DataSource) ctxR.lookup("jdbc/repository"); 
        Connection cnR = dsR.getConnection();
        
        Context ctxM = new InitialContext(); 
        DataSource dsM  = (DataSource) ctxM.lookup("jdbc/monitoring"); 
        Connection cnM = dsM.getConnection();
        
        
        PreparedStatement pscu = cnR.prepareStatement("select a.ccid,decode(b.cotrln,'base64'),a.ccnr,decode(c.cunm,'base64') from managed_service_ccontracts a, class_contracttypes b, managed_service_cinfo c where a.cttyid=b.cttyid and a.cuid=c.cuid order by 3");
        ResultSet cu = pscu.executeQuery();
        PreparedStatement psht = cnM.prepareStatement("SELECT hstid,hstln,ipaddr FROM monitoring_info_host order by 2");
        ResultSet ht = psht.executeQuery();
        PreparedStatement psma = cnR.prepareStatement("SELECT hstid,ccid FROM monitoring_host_contract_mapping");
        ResultSet ma = psma.executeQuery();
        
        List<String> mahtid = new ArrayList<String>();
        List<String> maccid = new ArrayList<String>();
        
        while (ma.next()) {
            mahtid.add(ma.getString( 1 ));
            maccid.add(ma.getString( 2 ));
        }
        
        line = "{\"CONTRACT\":[";
        while (cu.next()) {
            line += "{\"CCID\":\"" + cu.getString( 1 ) + "\",\"CCNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cu.getString( 2 ) ) ) + "\",\"CCNR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cu.getString( 3 ) ) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cu.getString( 4 ) ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1);
        line += "],\"HOST\":[";
        while (ht.next()) {
            String htid = ht.getString( 1 );
            String htnm = ht.getString( 2 );
            String htip = ht.getString( 3 );
            line += "{\"HOST_NM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( htnm ) ) + "\",\"HOST_IP\":\"" + Base64Coder.encodeString( Basics.encodeHtml( htip ) ) + "\",\"HOST_ID\":\"" + htid + "\",\"CONTRACTS\":[";
            for (int i=0;i<mahtid.size();i++) {
                if (htid.equals(mahtid.get(i))) {
                    line += "\"" + maccid.get(i) + "\",";
                }
            }
            line = line.substring(0, line.length()-1);
            line += "]},";
        }
        line = line.substring(0, line.length()-1);
        line += "]}";
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        /*
         * Close Connection
         */
        cnR.close();
        cnM.close();
        return replace;
    }
    
    /*
     * Host Role Mapping for Configuration
     */
    
    static public String GetHostRole() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        /* Require Base64 Value for Parameter Id */
        String line = "";
        Context ctxR = new InitialContext(); 
        DataSource dsR  = (DataSource) ctxR.lookup("jdbc/repository"); 
        Connection cnR = dsR.getConnection();
        
        Context ctxM = new InitialContext(); 
        DataSource dsM  = (DataSource) ctxM.lookup("jdbc/monitoring"); 
        Connection cnM = dsM.getConnection();
        
        
        PreparedStatement pscu = cnR.prepareStatement("SELECT rlid,decode(rlnm,'base64'),decode(rlde,'base64') FROM profiles_role order by 3");
        ResultSet cu = pscu.executeQuery();
        PreparedStatement psht = cnM.prepareStatement("SELECT hstid,hstln,ipaddr FROM monitoring_info_host order by 2");
        ResultSet ht = psht.executeQuery();
        PreparedStatement psma = cnR.prepareStatement("SELECT hstid,rlid FROM monitoring_host_role_mapping");
        ResultSet ma = psma.executeQuery();
        
        List<String> mahtid = new ArrayList<String>();
        List<String> marlid = new ArrayList<String>();
        
        while (ma.next()) {
            mahtid.add(ma.getString( 1 ));
            marlid.add(ma.getString( 2 ));
        }
        
        line = "{\"ROLE\":[";
        while (cu.next()) {
            line += "{\"ROID\":\"" + cu.getString( 1 ) + "\",\"RONM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cu.getString( 2 ) ) ) + "\",\"RODC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cu.getString( 3 ) ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1);
        line += "],\"HOST\":[";
        while (ht.next()) {
            String htid = ht.getString( 1 );
            String htnm = ht.getString( 2 );
            String htip = ht.getString( 3 );
            line += "{\"HOST_NM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( htnm ) ) + "\",\"HOST_IP\":\"" + Base64Coder.encodeString( Basics.encodeHtml( htip ) ) + "\",\"HOST_ID\":\"" + htid + "\",\"ROLE\":[";
            for (int i=0;i<mahtid.size();i++) {
                if (htid.equals(mahtid.get(i))) {
                    line += "\"" + marlid.get(i) + "\",";
                }
            }
            line = line.substring(0, line.length()-1);
            line += "]},";
        }
        line = line.substring(0, line.length()-1);
        line += "]}";
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        /*
         * Close Connection
         */
        cnR.close();
        cnM.close();
        return replace;
    }
    
    /*
     * Update Host / Customer / Contract Mapping
     */
    
    static public String UpdateHostRole(String Hstid, String Rlid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("SELECT hstid,rlid FROM profiles_user_group_mapping WHERE hstid=? AND rlid=?");
        ps.setInt(1,Integer.parseInt(Hstid));
        ps.setInt(2,Integer.parseInt(Rlid));
        ResultSet rs = ps.executeQuery();
        /*
         * Update
         */
        if (rs.next()) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_user_group_mapping WHERE hstid=? AND rlid=?");
            psD.setInt(1,Integer.parseInt(Hstid));
            psD.setInt(2,Integer.parseInt(Rlid));
            psD.executeUpdate();
            out = "1";
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_user_group_mapping (HSTID,RLID) VALUES (?,?)");
            psD.setInt(1,Integer.parseInt(Hstid));
            psD.setInt(2,Integer.parseInt(Rlid));
            psD.executeUpdate();
            out = "1";
        }
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String UpdateHostCustomer(String Hstid, String Cuid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("SELECT hstid,cuid FROM profiles_host_customer_mapping WHERE hstid=? AND cuid=?");
        ps.setInt(1,Integer.parseInt(Hstid));
        ps.setInt(2,Integer.parseInt(Cuid));
        ResultSet rs = ps.executeQuery();
        /*
         * Update
         */
        if (rs.next()) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_host_customer_mapping WHERE hstid=? AND cuid=?");
            psD.setInt(1,Integer.parseInt(Hstid));
            psD.setInt(2,Integer.parseInt(Cuid));
            psD.executeUpdate();
            out = "1";
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_host_customer_mapping (HSTID,CUID) VALUES (?,?)");
            psD.setInt(1,Integer.parseInt(Hstid));
            psD.setInt(2,Integer.parseInt(Cuid));
            psD.executeUpdate();
            out = "1";
        }
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String UpdateHostContract(String Hstid, String Ccid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("SELECT hstid,ccid FROM profiles_host_contract_mapping WHERE hstid=? AND ccid=?");
        ps.setInt(1,Integer.parseInt(Hstid));
        ps.setInt(2,Integer.parseInt(Ccid));
        ResultSet rs = ps.executeQuery();
        /*
         * Update
         */
        if (rs.next()) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_host_contract_mapping WHERE hstid=? AND ccid=?");
            psD.setInt(1,Integer.parseInt(Hstid));
            psD.setInt(2,Integer.parseInt(Ccid));
            psD.executeUpdate();
            out = "1";
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_host_contract_mapping (HSTID,CCID) VALUES (?,?)");
            psD.setInt(1,Integer.parseInt(Hstid));
            psD.setInt(2,Integer.parseInt(Ccid));
            psD.executeUpdate();
            out = "1";
        }
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
}
