/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.modules;

import de.siv.ksc.modules.*;
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
        PreparedStatement psHst = cn.prepareStatement("select a.hstln,a.hstid,a.ipaddr,b.htypln,c.srvid,c.srvna,d.current_state from monitoring_info_host a, class_hosttypes b, monitoring_info_service c, monitoring_status d where a.htypid=b.htypid and a.hstid=c.hstid and c.srvna like 'SYSTEM_ICMP_REQUEST' and c.srvid=d.srvid order by a.hstid asc");
        ResultSet rsHst = psHst.executeQuery();
        while ( rsHst.next() ) {
            line+= "{\"HOST_NAME\":\"" + Base64Coder.encodeString( rsHst.getString( 1 ) ) + "\",\"HOST_ID\":\"" + rsHst.getString( 2 ) + "\",\"IP\":\"" + rsHst.getString( 3 ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( rsHst.getString( 4 ) ) + "\",\"STATE\":\"" + rsHst.getString( 7 ) + "\"},";
        }
        line = line.substring(0, line.length()-1); line+= "],\"SERVICES\":[";
        /*
         * Service Status
         */
        PreparedStatement psSrv = cn.prepareStatement("select a.hstln,a.hstid,a.ipaddr,b.htypln,c.srvid,c.srvna,d.current_state from monitoring_info_host a, class_hosttypes b, monitoring_info_service c, monitoring_status d where a.htypid=b.htypid and a.hstid=c.hstid and c.srvna not like 'SYSTEM_ICMP_REQUEST' and c.srvid=d.srvid order by a.hstid,c.srvid asc");
        ResultSet rsSrv = psSrv.executeQuery();
        while ( rsSrv.next() ) {
            line+= "{\"HOST_NAME\":\"" + Base64Coder.encodeString( rsSrv.getString( 1 ) ) + "\",\"HOST_ID\":\"" + rsSrv.getString( 2 ) + "\",\"IP\":\"" + rsSrv.getString( 3 ) + "\",\"HOST_TYPE\":\"" + Base64Coder.encodeString( rsSrv.getString( 4 ) ) + "\",\"SRV_ID\":\"" + rsSrv.getString( 5 ) + "\",\"SRV_NAME\":\"" + Base64Coder.encodeString( rsSrv.getString( 6 ) ) + "\",\"STATE\":\"" + rsSrv.getString( 7 ) + "\"},";
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
}
