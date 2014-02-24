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
import java.sql.Statement;
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
public class Functions {
    
    static Properties props = null;
    
    static public void UpdateLastLogin(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement psD = cn.prepareStatement("UPDATE profiles_user SET ULAL=? WHERE usnm=?");
        psD.setLong(1,System.currentTimeMillis()/1000);
        psD.setString(2,Base64Coder.encodeString( Uid ));
        psD.executeUpdate(); 
        /*
         * Close Connection
         */
        cn.close();
    }
    
    static public void UpdateUserIsLoggedIn(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement psD = cn.prepareStatement("UPDATE profiles_user SET UILI=? WHERE usnm=?");
        psD.setBoolean(1,true);
        psD.setString(2,Base64Coder.encodeString( Uid ));
        psD.executeUpdate(); 
        /*
         * Close Connection
         */
        cn.close();
    }
    
    static public String GetUserConfig(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Integer UUID = null;
        String line = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository");
        Connection cn = ds.getConnection(); 
        line = "[{\"LOCAL_BACKEND\":\"" + props.getProperty("BACKEND.IP") + ":" + props.getProperty("BACKEND.PORT") + "\",";
        /*
         * Erste Query
         */
        PreparedStatement ps = cn.prepareStatement("SELECT uuid,decode(usdc,'base64'),decode(umai,'base64'),bit_length(upic) FROM profiles_user WHERE usnm=?");
        ps.setString(1,Base64Coder.encodeString( Uid ));
        ResultSet rs = ps.executeQuery();
        while ( rs.next() ) { 
            UUID = rs.getInt( 1 );
            String fnam; if (rs.getString( 2 ) == null) { fnam = Base64Coder.encodeString( "-" ); } else { fnam = Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 2 ) ) ); }
            String mail; if (rs.getString( 3 ) == null) { mail = Base64Coder.encodeString( "-" ); } else { mail = Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 3 ) ) ); }
            line += "\"UUID\":\"" + Base64Coder.encodeString( rs.getString( 1 ) ) + "\",\"UID\":\"" + Base64Coder.encodeString( Uid ) + "\",\"NAME\":\"" + fnam + "\",\"MAIL\":\"" + mail + "\",\"PCTRL\":\"" + Base64Coder.encodeString( rs.getString( 4 ) ) + "\",\"PCTR\":\"" + Base64Coder.encodeString("/gateway/exec/UserPicture") + "\",";
        }
        line = line.substring(0, line.length()-1); line += ",\"DASHBOARD\":[";
        /*
         * Zweite Query
         */
        PreparedStatement ps1 = cn.prepareStatement("SELECT decode(val1,'base64'),decode(val2,'base64'),decode(val3,'base64') FROM config_portal WHERE uuid=? AND mod = encode('DASHBOARD','base64') ORDER BY cpid ASC");
        ps1.setInt(1,UUID);
        ResultSet rs1 = ps1.executeQuery();
        while ( rs1.next() ) { 
            String tv1; if (rs1.getString( 1 ) == null) { tv1 = Base64Coder.encodeString( "-" ); } else { tv1 = Base64Coder.encodeString( Basics.encodeHtml( rs1.getString( 1 ) ) ); }
            String tv2; if (rs1.getString( 2 ) == null) { tv2 = Base64Coder.encodeString( "-" ); } else { tv2 = Base64Coder.encodeString( Basics.encodeHtml( rs1.getString( 2 ) ) ); }
            String tv3; if (rs1.getString( 3 ) == null) { tv3 = Base64Coder.encodeString( "-" ); } else { tv3 = Base64Coder.encodeString( Basics.encodeHtml( rs1.getString( 3 ) ) ); }
            line += "{\"TITLE\":\"" + tv1 + "\",\"DESC\":\"" + tv2 + "\",\"TARGET\":\"" + tv3 + "\"},"; 
        } 
        line = line.substring(0, line.length()-1); line += "],\"USER_CONFIG\":["; 
        /* 
         * Dritte Query
         */
        PreparedStatement ps2 = cn.prepareStatement("SELECT decode(key,'base64'),decode(val1,'base64'),decode(val2,'base64'),decode(val3,'base64') FROM config_portal WHERE uuid=? AND mod = encode('CONFIG','base64') ORDER BY cpid ASC");
        ps2.setInt(1,UUID);
        ResultSet rs2 = ps2.executeQuery();
        while ( rs2.next() ) {
            String tky; if (rs2.getString( 1 ) == null) { tky = Base64Coder.encodeString( "-" ); } else { tky = Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 1 ) ) ); }
            String tv1; if (rs2.getString( 2 ) == null) { tv1 = Base64Coder.encodeString( "-" ); } else { tv1 = Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 2 ) ) ); }
            String tv2; if (rs2.getString( 3 ) == null) { tv2 = Base64Coder.encodeString( "-" ); } else { tv2 = Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 3 ) ) ); }
            line += "{\"KEY\":\"" + tky + "\",\"ACTION\":\"" + tv1 + "\",\"DESC\":\"" + tv2 + "\"},";
        }
        line = line.substring(0, line.length()-1); line += "],\"MAILING\":[";
        /*
         * Vierte Query
         */
        PreparedStatement ps3 = cn.prepareStatement("SELECT decode(key,'base64'),decode(val1,'base64') FROM config_portal WHERE uuid=? AND mod = encode('MAILING','base64')");
        ps3.setInt(1,UUID);
        ResultSet rs3 = ps3.executeQuery();
        while ( rs3.next() ) { 
            line += "{\"" + rs3.getString( 1 ) + "\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs3.getString( 2 ) ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1); line += "],";
        line += "\"USER_GROUPS\":[" + Functions.UsersGroups(Uid) + "],";
        line += "\"USER_PERM\":[" + Functions.UsersPermissions(Uid) + "]}]";
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return replace;
    }
    
    static public String WhoIsLoggedIn(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Long ctime = System.currentTimeMillis()/1000;
        Long stime = ctime - 3600; /* 1h zurück */
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("SELECT decode(usnm,'base64'),decode(usdc,'base64'),decode(umai,'base64'),bit_length(upic) FROM profiles_user WHERE uili=? AND ulal>? ORDER BY 3");
        ps.setBoolean(1,true);
        ps.setLong(2,stime);
        ResultSet rs = ps.executeQuery();
        
        String out = "{\"USER\":[";
        while (rs.next()) { 
            out += "{\"USNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(1) ) ) + "\",\"USDC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(2) ) ) + "\",\"UMAI\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\",\"PCTRL\":\"" + rs.getString(4) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    /*
     * Dashboard
     */
    
    static public void AddDashboardLink(String Uuid, String Title, String Desc, String Target) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement psD = cn.prepareStatement("INSERT INTO config_portal(UUID,MOD,KEY,VAL1,VAL2,VAL3) VALUES (?,encode('DASHBOARD','base64'),encode('LINK','base64'),?,?,?)");
        psD.setInt(1,Integer.parseInt( Base64Coder.decodeString( Uuid ) ));
        psD.setString(2,Title);
        psD.setString(3,Desc);
        psD.setString(4,Target);
        psD.executeUpdate();
        /*
         * Close Connection
         */
        cn.close();
    }
    
    static public String UsersPermissions(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("select decode(e.prnm, 'base64'),decode(e.prdc, 'base64') from profiles_user a, profiles_user_group_mapping b, profiles_group_role_mapping c, profiles_role_priv_mapping d, profiles_privilege e where a.uuid = b.uuid and b.grid = c.grid and c.rlid = d.rlid and d.prid = e.prid and a.usnm=?");
        ps.setString(1,Base64Coder.encodeString( Uid ));
        ResultSet rs = ps.executeQuery();
        while ( rs.next() ) {
            out += "\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(1) + " (" + rs.getString(2) + ")" ) ) + "\",";
        }
        out = out.substring(0, out.length()-1);
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String UsersGroups(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("select decode(b.grdc,'base64') from profiles_user a, profiles_group b, profiles_user_group_mapping c where a.uuid = c.uuid AND b.grid = c.grid AND a.usnm=?");
        ps.setString(1,Base64Coder.encodeString( Uid ));
        ResultSet rs = ps.executeQuery();
        while ( rs.next() ) {
            out += "\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(1) ) ) + "\",";
        }
        out = out.substring(0, out.length()-1);
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public void UpdateUserMail(String Uid, String Mail) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement psD = cn.prepareStatement("UPDATE profiles_user SET umai=? where usnm=?");
        psD.setString(1,Mail);
        psD.setString(2,Base64Coder.encodeString( Uid ));
        psD.executeUpdate();
        /*
         * Close Connection
         */
        cn.close();
    }
    
    static public String UserManagementOverview() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = null;
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        // Alle Nutzer
        
        out = "{\"USERS\":[";
        PreparedStatement ps = cn.prepareStatement("select uuid,decode(usnm,'base64'),decode(usdc,'base64'),decode(umai,'base64'),ucrt,ulal,uact from profiles_user order by 2");
        ResultSet rs = ps.executeQuery();
        while ( rs.next() ) {
            out += "{\"UUID\":\"" + rs.getString(1)  + "\",\"USNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(2) ) ) + "\",\"USDC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\",\"UMAI\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(4) ) ) + "\",\"UCRT\":\"" + Basics.ConvertUtime(rs.getLong(5)) + "\",\"ULAL\":\"" + Basics.ConvertUtime(rs.getLong(6)) + "\",\"UACT\":\"" + rs.getString(7) + "\",\"UPIC\":\"" + Base64Coder.encodeString("/gateway/exec/UserPicture?user=" + rs.getString(2)) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        
        // Alle Gruppen
        
        out += "],\"GROUPS\":[";
        PreparedStatement ps2 = cn.prepareStatement("select grid,decode(grnm,'base64'),decode(grdc,'base64') from profiles_group order by 2");
        ResultSet rs2 = ps2.executeQuery();
        while ( rs2.next() ) {
            out += "{\"GRID\":\"" + rs2.getString(1)  + "\",\"GRNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(2) ) ) + "\",\"GRDC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(3) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        
        // Alle Rollen
        
        out += "],\"ROLES\":[";
        PreparedStatement ps3 = cn.prepareStatement("select rlid,decode(rlnm,'base64'),decode(rlde,'base64') from profiles_role order by 2");
        ResultSet rs3 = ps3.executeQuery();
        while ( rs3.next() ) {
            out += "{\"RLID\":\"" + rs3.getString(1)  + "\",\"RLNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs3.getString(2) ) ) + "\",\"RLDC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs3.getString(3) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        
        // Alle Privilegien
        
        out += "],\"PRIVS\":[";
        PreparedStatement ps4 = cn.prepareStatement("select prid,decode(prnm,'base64'),decode(prdc,'base64') from profiles_privilege order by 2");
        ResultSet rs4 = ps4.executeQuery();
        while ( rs4.next() ) {
            out += "{\"PRID\":\"" + rs4.getString(1) + "\",\"PRNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs4.getString(2) ) ) + "\",\"PRDC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs4.getString(3) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        /*
         * Close Connection
         */
        String replace = out.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return replace;
    }
    
    static public Boolean AlreadyExist(String Mod, String Nm) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        /* Require Base64 Value for Parameter Id */
        Boolean line = false;
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        ResultSet rs = null;
        if ("USER".equals(Mod)) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM profiles_user where usnm=?");
            ps.setString(1,Nm);
            rs = ps.executeQuery();
        } else if ("GROUP".equals(Mod)) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM profiles_group where grnm=?");
            ps.setString(1,Nm);
            rs = ps.executeQuery();
        } else if ("ROLE".equals(Mod)) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM profiles_role where rlnm=?");
            ps.setString(1,Nm);
            rs = ps.executeQuery();
        } else if ("PRIV".equals(Mod)) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM profiles_privilege where prnm=?");
            ps.setString(1,Nm);
            rs = ps.executeQuery();
        } else {
            // nothing
        }
                
        while ( rs.next() ) { 
           line = true;
        }
        
        /*
         * Close Connection
         */
        cn.close();
        return line;
    }
    
    static public String AddEntry(String Mod, String Id, String Desc) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        /*
         * Insert
         */
        if (Base64Coder.encodeString("USER").equals(Mod)) {
            if(AlreadyExist("USER",Id)) { 
                out = "0"; 
            } else {
                PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_user(USNM,USDC,UMAI,UCRT,ULAL,UACT,UPIC,UILI) values (?,?,?,?,?,?,'',FALSE)");
                psD.setString(1,Id);
                psD.setString(2,Desc);
                psD.setString(3,Base64Coder.encodeString("-"));
                psD.setLong(4,System.currentTimeMillis()/1000);
                psD.setInt(5,0);
                psD.setInt(6,0);
                psD.executeUpdate();
                out = "1";
            }
        } else if (Base64Coder.encodeString("GROUP").equals(Mod)) {
            if(AlreadyExist("GROUP",Id)) { 
                out = "0"; 
            } else { 
                PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_group(GRNM,GRDC) values (?,?)");
                psD.setString(1,Id);
                psD.setString(2,Desc);
                psD.executeUpdate();
                out = "1"; 
            }
        } else if (Base64Coder.encodeString("ROLE").equals(Mod)) {
            if(AlreadyExist("ROLE",Id)) { 
                out = "0"; 
            } else { 
                PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_role(RLNM,RLDE) values (?,?)");
                psD.setString(1,Id);
                psD.setString(2,Desc);
                psD.executeUpdate();
                out = "1"; 
            }
        } else if (Base64Coder.encodeString("PRIV").equals(Mod)) {
            if(AlreadyExist("PRIV",Id)) { 
                out = "0"; 
            } else { 
                PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_privilege(PRNM,PRDC) values (?,?)");
                psD.setString(1,Id);
                psD.setString(2,Desc);
                psD.executeUpdate();        
                out = "1"; 
            }
        } else {
            out = "0";
        }
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String DeleteEntry(String Mod, String Id) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        /*
         * Insert
         */
        if (Base64Coder.encodeString("USER").equals(Mod)) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_user WHERE uuid=?");
            psD.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD.executeUpdate();
            PreparedStatement psD2 = cn.prepareStatement("DELETE FROM profiles_user_group_mapping WHERE uuid=?");
            psD2.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD2.executeUpdate();
            out = "1";
        } else if (Base64Coder.encodeString("GROUP").equals(Mod)) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_group WHERE grid=?");
            psD.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD.executeUpdate();
            PreparedStatement psD2 = cn.prepareStatement("DELETE FROM profiles_group_role_mapping WHERE grid=?");
            psD2.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD2.executeUpdate();
            PreparedStatement psD3 = cn.prepareStatement("DELETE FROM profiles_user_group_mapping WHERE grid=?");
            psD3.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD3.executeUpdate();
            out = "1";
        } else if (Base64Coder.encodeString("ROLE").equals(Mod)) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_role WHERE rlid=?");
            psD.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD.executeUpdate();
            PreparedStatement psD2 = cn.prepareStatement("DELETE FROM profiles_role_priv_mapping WHERE rlid=?");
            psD2.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD2.executeUpdate();
            PreparedStatement psD3 = cn.prepareStatement("DELETE FROM profiles_group_role_mapping WHERE rlid=?");
            psD3.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD3.executeUpdate();
            out = "1";
        } else if (Base64Coder.encodeString("PRIV").equals(Mod)) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_privilege WHERE prid=?");
            psD.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD.executeUpdate();
            PreparedStatement psD2 = cn.prepareStatement("DELETE FROM profiles_role_priv_mapping WHERE prid=?");
            psD2.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD2.executeUpdate();
            out = "1";
        } else {
            out = "0";
        }
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String ActivateUser(String Mod, String Id) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection();  
        /*
         * Insert
         */
        if (Base64Coder.encodeString("1").equals(Mod)) {
            PreparedStatement psD = cn.prepareStatement("UPDATE profiles_user set uact = 1 WHERE uuid=?");
            psD.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD.executeUpdate();
            out = "1";
        } else if (Base64Coder.encodeString("0").equals(Mod)) {
            PreparedStatement psD = cn.prepareStatement("UPDATE profiles_user set uact = 0 WHERE uuid=?");
            psD.setInt(1,Integer.parseInt(Base64Coder.decodeString( Id )));
            psD.executeUpdate();
            out = "1";
        } else {
            out = "0";
        }
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String PermissionRolePrivilege() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        /* Require Base64 Value for Parameter Id */
        String line = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection();
        PreparedStatement pspr = cn.prepareStatement("SELECT prid,decode(prnm,'base64'),decode(prdc,'base64') FROM profiles_privilege order by 3");
        ResultSet pr = pspr.executeQuery();
        PreparedStatement psro = cn.prepareStatement("SELECT rlid,decode(rlnm,'base64'),decode(rlde,'base64') FROM profiles_role order by 3");
        ResultSet ro = psro.executeQuery();
        PreparedStatement psma = cn.prepareStatement("SELECT rlid,prid FROM profiles_role_priv_mapping");
        ResultSet ma = psma.executeQuery();
        
        List<String> marlid = new ArrayList<String>();
        List<String> maprid = new ArrayList<String>();
        
        while (ma.next()) {
            marlid.add(ma.getString( 1 ));
            maprid.add(ma.getString( 2 ));
        }
        
        line = "{\"PRIVILEGE\":[";
        while (pr.next()) {
            line += "{\"PRID\":\"" + pr.getString( 1 ) + "\",\"PRNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( pr.getString( 2 ) ) ) + "\",\"PRDC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( pr.getString( 3 ) ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1);
        line += "],\"ROLE\":[";
        while (ro.next()) {
            String roid = ro.getString( 1 );
            String ronm = ro.getString( 2 );
            String rode = ro.getString( 3 );
            line += "{\"ROLE_NM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( ronm ) ) + "\",\"ROLE_DC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rode ) ) + "\",\"ROLE_ID\":\"" + roid + "\",\"ROLE_PRIV\":[";
            for (int i=0;i<marlid.size();i++) {
                if (roid.equals(marlid.get(i))) {
                    line += "\"" + maprid.get(i) + "\",";
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
        cn.close();
        return replace;
    }
    
    static public String UpdateRolePriv(String Rlid, String Prid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("SELECT rlid,prid FROM profiles_role_priv_mapping WHERE rlid=? AND prid=?");
        ps.setInt(1,Integer.parseInt(Rlid));
        ps.setInt(2,Integer.parseInt(Prid));
        ResultSet rs = ps.executeQuery();
        /*
         * Update
         */
        if (rs.next()) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_role_priv_mapping WHERE rlid? AND prid=?");
            psD.setInt(1,Integer.parseInt(Rlid));
            psD.setInt(2,Integer.parseInt(Prid));
            psD.executeUpdate();
            out = "1";
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_role_priv_mapping (RLID,PRID) values (?,?)");
            psD.setInt(1,Integer.parseInt(Rlid));
            psD.setInt(2,Integer.parseInt(Prid));
            psD.executeUpdate();
            out = "1";
        }
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String PermissionGroupRole() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        /* Require Base64 Value for Parameter Id */
        String line = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement psro = cn.prepareStatement("SELECT rlid,decode(rlnm,'base64'),decode(rlde,'base64') FROM profiles_role order by 3");
        ResultSet ro = psro.executeQuery();
        PreparedStatement psgr = cn.prepareStatement("SELECT grid,decode(grnm,'base64'),decode(grdc,'base64') FROM profiles_group order by 3");
        ResultSet gr = psgr.executeQuery();
        PreparedStatement psma = cn.prepareStatement("SELECT grid,rlid FROM profiles_group_role_mapping");
        ResultSet ma = psma.executeQuery();
        
        List<String> magrid = new ArrayList<String>();
        List<String> marlid = new ArrayList<String>();
        
        while (ma.next()) {
            magrid.add(ma.getString( 1 ));
            marlid.add(ma.getString( 2 ));
        }
        
        line = "{\"ROLE\":[";
        while (ro.next()) {
            line += "{\"ROID\":\"" + ro.getString( 1 ) + "\",\"RONM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( ro.getString( 2 ) ) ) + "\",\"RODC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( ro.getString( 3 ) ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1);
        line += "],\"GROUP\":[";
        while (gr.next()) {
            String grid = gr.getString( 1 );
            String grnm = gr.getString( 2 );
            String grdc = gr.getString( 3 );
            line += "{\"GROUP_NM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( grnm ) ) + "\",\"GROUP_DC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( grdc ) ) + "\",\"GROUP_ID\":\"" + grid + "\",\"GROUP_ROLE\":[";
            for (int i=0;i<magrid.size();i++) {
                if (grid.equals(magrid.get(i))) {
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
        cn.close();
        return replace;
    }
    
    static public String UpdateGroupRole(String Grid, String Rlid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection();
        PreparedStatement ps = cn.prepareStatement("SELECT grid,rlid FROM profiles_group_role_mapping WHERE grid=? AND rlid=?");
        ps.setInt(1,Integer.parseInt(Grid));
        ps.setInt(2,Integer.parseInt(Rlid));
        ResultSet rs = ps.executeQuery();
        /*
         * Update
         */
        if (rs.next()) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_group_role_mapping WHERE grid=? AND rlid=?");
            psD.setInt(1,Integer.parseInt(Grid));
            psD.setInt(2,Integer.parseInt(Rlid));
            psD.executeUpdate();
            out = "1";
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_group_role_mapping (GRID,RLID) values (?,?)");
            psD.setInt(1,Integer.parseInt(Grid));
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
    
    static public String PermissionUserGroup() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        /* Require Base64 Value for Parameter Id */
        String line = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection();
        PreparedStatement psgr = cn.prepareStatement("SELECT grid,decode(grnm,'base64'),decode(grdc,'base64') FROM profiles_group order by 3");
        ResultSet gr = psgr.executeQuery();
        PreparedStatement psus = cn.prepareStatement("SELECT uuid,decode(usnm,'base64'),decode(usdc,'base64') FROM profiles_user order by 3");
        ResultSet us = psus.executeQuery();
        PreparedStatement psma = cn.prepareStatement("SELECT uuid,grid FROM profiles_user_group_mapping");
        ResultSet ma = psma.executeQuery();
        
        List<String> mauuid = new ArrayList<String>();
        List<String> magrid = new ArrayList<String>();
        
        while (ma.next()) {
            mauuid.add(ma.getString( 1 ));
            magrid.add(ma.getString( 2 ));
        }
        
        line = "{\"GROUP\":[";
        while (gr.next()) {
            line += "{\"GRID\":\"" + gr.getString( 1 ) + "\",\"GRNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( gr.getString( 2 ) ) ) + "\",\"GRDC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( gr.getString( 3 ) ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1);
        line += "],\"USER\":[";
        while (us.next()) {
            String usid = us.getString( 1 );
            String usnm = us.getString( 2 );
            String usdc = us.getString( 3 );
            line += "{\"USER_NM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( usnm ) ) + "\",\"USER_DC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( usdc ) ) + "\",\"USER_ID\":\"" + usid + "\",\"USER_GROUP\":[";
            for (int i=0;i<mauuid.size();i++) {
                if (usid.equals(mauuid.get(i))) {
                    line += "\"" + magrid.get(i) + "\",";
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
        cn.close();
        return replace;
    }
    
    static public String UpdateUserGroup(String Uuid, String Grid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("SELECT uuid,grid FROM profiles_user_group_mapping WHERE uuid=? AND grid=?");
        ps.setInt(1,Integer.parseInt(Uuid));
        ps.setInt(2,Integer.parseInt(Grid));
        ResultSet rs = ps.executeQuery();
        /*
         * Update
         */
        if (rs.next()) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_user_group_mapping WHERE uuid=? AND grid=?");
            psD.setInt(1,Integer.parseInt(Uuid));
            psD.setInt(2,Integer.parseInt(Grid));
            psD.executeUpdate();
            out = "1";
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_user_group_mapping (UUID,GRID) VALUES (?,?)");
            psD.setInt(1,Integer.parseInt(Uuid));
            psD.setInt(2,Integer.parseInt(Grid));
            psD.executeUpdate();
            out = "1";
        }
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    /*
     * Customer
     */
    
    static public String CreateCustomer(String cname, String cnumber, String cmail, String cesmail, String caddress, String ccomm, String ct1, String ct1an, String ct1pv, String ct1pi, String ct2, String ct2an, String ct2pv, String ct2pi, String ct3, String ct3an, String ct3pv, String ct3pi, String ct4, String ct4an, String ct4pv, String ct4pi, String ct5, String ct5an, String ct5pv, String ct5pi, String ct6, String ct6an, String ct6pv, String ct6pi) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "1";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        /*
         * Check if Customer exist
         */
        
        PreparedStatement p = cn.prepareStatement("SELECT cuid FROM managed_service_cinfo WHERE CUNR=?");
        p.setInt(1,Integer.parseInt( Base64Coder.decodeString( cnumber ) ));
        ResultSet r = p.executeQuery();
        
        if (r.next()) { out = "0"; } else {
        
        /*
         * Create Customer
         */
        
        PreparedStatement ps = cn.prepareStatement("INSERT INTO managed_service_cinfo(CUNR,CUNM,CUADDR,CUMAIL,CUESKMAIL,CUCOMM) VALUES (?,?,?,?,?,?)");
        ps.setInt(1,Integer.parseInt( Base64Coder.decodeString( cnumber ) ));
        ps.setString(2,cname);
        ps.setString(3,caddress.replace("78", "+"));
        ps.setString(4,cmail);
        ps.setString(5,cesmail);
        ps.setString(6,ccomm.replace("78", "+"));
        ps.executeUpdate();
        
        /*
         * Get Customer ID
         */
        
        Integer CUID = null;
        PreparedStatement ps2 = cn.prepareStatement("SELECT cuid FROM managed_service_cinfo WHERE CUNR=?");
        ps2.setInt(1,Integer.parseInt( Base64Coder.decodeString( cnumber ) ));
        ResultSet rs2 = ps2.executeQuery();
        
        if ( rs2.next() ) { CUID = rs2.getInt( 1 ); }
        
        /*
         * Update Contract
         */
        
        if (!"0000".equals(Base64Coder.decodeString(ct1))) {
            PreparedStatement ps3 = cn.prepareStatement("INSERT INTO managed_service_ccontracts(CUID,CCNR,CCPRVE,CCPRDC,CTTYID) VALUES (?,?,?,?,?)");
            ps3.setInt(1,CUID);
            ps3.setInt(2,Integer.parseInt( Base64Coder.decodeString( ct1an ) ));
            ps3.setString(3,ct1pv);
            ps3.setString(4,ct1pi);
            ps3.setInt(5,Integer.parseInt( Base64Coder.decodeString( ct1 ) ));
            ps3.executeUpdate();
        } 
        
        if (!"0000".equals(Base64Coder.decodeString(ct2))) {
            PreparedStatement ps4 = cn.prepareStatement("INSERT INTO managed_service_ccontracts(CUID,CCNR,CCPRVE,CCPRDC,CTTYID) VALUES (?,?,?,?,?)");
            ps4.setInt(1,CUID);
            ps4.setInt(2,Integer.parseInt( Base64Coder.decodeString( ct2an ) ));
            ps4.setString(3,ct2pv);
            ps4.setString(4,ct2pi);
            ps4.setInt(5,Integer.parseInt( Base64Coder.decodeString( ct2 ) ));
            ps4.executeUpdate();
        } 
        
        if (!"0000".equals(Base64Coder.decodeString(ct3))) {
            PreparedStatement ps5 = cn.prepareStatement("INSERT INTO managed_service_ccontracts(CUID,CCNR,CCPRVE,CCPRDC,CTTYID) VALUES (?,?,?,?,?)");
            ps5.setInt(1,CUID);
            ps5.setInt(2,Integer.parseInt( Base64Coder.decodeString( ct3an ) ));
            ps5.setString(3,ct3pv);
            ps5.setString(4,ct3pi);
            ps5.setInt(5,Integer.parseInt( Base64Coder.decodeString( ct3 ) ));
            ps5.executeUpdate();
        } 
        
        if (!"0000".equals(Base64Coder.decodeString(ct4))) {
            PreparedStatement ps6 = cn.prepareStatement("INSERT INTO managed_service_ccontracts(CUID,CCNR,CCPRVE,CCPRDC,CTTYID) VALUES (?,?,?,?,?)");
            ps6.setInt(1,CUID);
            ps6.setInt(2,Integer.parseInt( Base64Coder.decodeString( ct4an ) ));
            ps6.setString(3,ct4pv);
            ps6.setString(4,ct4pi);
            ps6.setInt(5,Integer.parseInt( Base64Coder.decodeString( ct4 ) ));
            ps6.executeUpdate();
        } 
        
        if (!"0000".equals(Base64Coder.decodeString(ct5))) {
            PreparedStatement ps7 = cn.prepareStatement("INSERT INTO managed_service_ccontracts(CUID,CCNR,CCPRVE,CCPRDC,CTTYID) VALUES (?,?,?,?,?)");
            ps7.setInt(1,CUID);
            ps7.setInt(2,Integer.parseInt( Base64Coder.decodeString( ct5an ) ));
            ps7.setString(3,ct5pv);
            ps7.setString(4,ct5pi);
            ps7.setInt(5,Integer.parseInt( Base64Coder.decodeString( ct5 ) ));
            ps7.executeUpdate();
        } 
        
        if (!"0000".equals(Base64Coder.decodeString(ct6))) {
            PreparedStatement ps8 = cn.prepareStatement("INSERT INTO managed_service_ccontracts(CUID,CCNR,CCPRVE,CCPRDC,CTTYID) VALUES (?,?,?,?,?)");
            ps8.setInt(1,CUID);
            ps8.setInt(2,Integer.parseInt( Base64Coder.decodeString( ct6an ) ));
            ps8.setString(3,ct6pv);
            ps8.setString(4,ct6pi);
            ps8.setInt(5,Integer.parseInt( Base64Coder.decodeString( ct6 ) ));
            ps8.executeUpdate();
        } 
        
        }
        
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String GetCustomer() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("SELECT cuid,cunr,decode(cunm,'base64'),decode(cuaddr,'base64'),decode(cumail,'base64'),decode(cueskmail,'base64'),decode(cucomm,'base64') FROM managed_service_cinfo ORDER BY 3");
        ResultSet rs = ps.executeQuery();
        
        String out = "{\"CUSTOMER\":[";
        while (rs.next()) { 
            //Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) )
            out += "{\"CUID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\",\"CUADDR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(4) ) ) + "\",\"CUMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(5) ) ) + "\",\"CUESKMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(6) ) ) + "\",\"CUCOMM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(7) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    static public String AutoCompleteCustomer(String cunm) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        Statement st = cn.createStatement(); 
        ResultSet rs  = st.executeQuery("SELECT cuid,cunr,cunm FROM autocompletecustomer WHERE cunm LIKE '%" + Base64Coder.decodeString( cunm ) + "%'");
        
        String out = "{\"CUSTOMER\":[";
        while (rs.next()) { 
            out += "{\"CUID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    static public String GetSingleCustomer(String cuid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        /*
         * Get Customer Info
         */
        
        PreparedStatement ps = cn.prepareStatement("SELECT cuid,cunr,decode(cunm,'base64'),decode(cuaddr,'base64'),decode(cumail,'base64'),decode(cueskmail,'base64'),decode(cucomm,'base64') FROM managed_service_cinfo WHERE cuid=?");
        ps.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        ResultSet rs = ps.executeQuery();
        
        String out = "{\"CUSTOMER\":[";
        while (rs.next()) { 
            out += "{\"CUID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\",\"CUADDR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(4) ) ) + "\",\"CUMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(5) ) ) + "\",\"CUESKMAIL\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(6) ) ) + "\",\"CUCOMM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(7) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "],\"CONTRACTS\":[";
        
        /*
         * Get Customer Contracts
         */
        
        PreparedStatement ps2 = cn.prepareStatement("select a.ccid,a.ccnr,decode(a.ccprve,'base64'),decode(a.ccprdc,'base64'),decode(b.cotrsn,'base64'),decode(b.cotrln,'base64') from managed_service_ccontracts a,class_contracttypes b where a.cttyid=b.cttyid and a.cuid=?");
        ps2.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        ResultSet rs2 = ps2.executeQuery();
        
        while (rs2.next()) { 
            out += "{\"CCID\":\"" + Base64Coder.encodeString( rs2.getString(1) ) + "\",\"CCNR\":\"" + Base64Coder.encodeString( rs2.getString(2) ) + "\",\"CCPRVE\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(3) ) ) + "\",\"CCPRDC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(4) ) ) + "\",\"COTRSN\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(5) ) ) + "\",\"COTRLN\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString(6) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    static public String UpdateCustomer(String cuid, String cname, String cnumber, String cmail, String cesmail, String caddress, String ccomm, String ct1, String ct1an, String ct1pv, String ct1pi) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "1";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        /*
         * Update Customer
         */
        
        PreparedStatement ps = cn.prepareStatement("UPDATE managed_service_cinfo set CUNR=?, CUNM=?, CUADDR=?, CUMAIL=?, CUESKMAIL=?, CUCOMM=? WHERE CUID=?");
        ps.setInt(1,Integer.parseInt( Base64Coder.decodeString( cnumber ) ));
        ps.setString(2,cname);
        ps.setString(3,caddress.replace("78", "+"));
        ps.setString(4,cmail);
        ps.setString(5,cesmail);
        ps.setString(6,ccomm.replace("78", "+"));
        ps.setInt(7,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        ps.executeUpdate();
        
        if (!"0000".equals(Base64Coder.decodeString(ct1))) {
            PreparedStatement ps3 = cn.prepareStatement("INSERT INTO managed_service_ccontracts(CUID,CCNR,CCPRVE,CCPRDC,CTTYID) VALUES (?,?,?,?,?)");
            ps3.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
            ps3.setInt(2,Integer.parseInt( Base64Coder.decodeString( ct1an ) ));
            ps3.setString(3,ct1pv);
            ps3.setString(4,ct1pi);
            ps3.setInt(5,Integer.parseInt( Base64Coder.decodeString( ct1 ) ));
            ps3.executeUpdate();
        } 
                
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String DeleteContract(String cuid, String ccid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement psD = cn.prepareStatement("DELETE FROM managed_service_ccontracts WHERE cuid=? AND ccid=?");
        psD.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        psD.setInt(2,Integer.parseInt( Base64Coder.decodeString( ccid ) ));
        psD.executeUpdate();
        out = "1";
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String DeleteCustomer(String cuid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("DELETE FROM managed_service_cinfo WHERE cuid=?");
        ps.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        ps.executeUpdate();
                
        PreparedStatement psD = cn.prepareStatement("DELETE FROM managed_service_ccontracts WHERE cuid=?");
        psD.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        psD.executeUpdate();
        out = "1";
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    /*
     * Contract Types
     */
    
    static public String GetContractTypes() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("select cttyid,decode(cotrsn,'base64'),decode(cotrln,'base64') from class_contracttypes order by 3");
        ResultSet rs = ps.executeQuery();
        
        String out = "{\"CONTRACT_TYPES\":[";
        while ( rs.next() ) {
            out += "{\"ID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"SN\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"LN\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    static public String CreateContractType(String cotrsn, String cotrln, String mactions) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "1";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        /*
         * Check if Customer exist
         */
        
        PreparedStatement p = cn.prepareStatement("SELECT cttyid FROM class_contracttypes WHERE cotrsn=?");
        p.setString(1,Base64Coder.decodeString( cotrsn ));
        ResultSet r = p.executeQuery();
        
        if (r.next()) { out = "0"; } else {
        
        /*
         * Create Customer
         */
        
        PreparedStatement ps = cn.prepareStatement("INSERT INTO class_contracttypes(COTRSN,COTRLN,MACTIONS) VALUES (?,?,?)");
        ps.setString(1,cotrsn);
        ps.setString(2,cotrln);
        ps.setString(3,mactions.replace("78", "+"));
        ps.executeUpdate();
        
        }
        
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String GetSingleContractType(String cttyid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("SELECT cttyid,decode(cotrsn,'base64'),decode(cotrln,'base64'),decode(mactions,'base64') FROM class_contracttypes WHERE cttyid=?");
        ps.setInt(1,Integer.parseInt( Base64Coder.decodeString( cttyid ) ));
        ResultSet rs = ps.executeQuery();
        
        String out = "{\"CONTRACT\":[";
        while (rs.next()) { 
            out += "{\"CTTYID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"COTRSN\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"COTRLN\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\",\"MACTIONS\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(4) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    static public String GetCustomerContractNumbers(String cuid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("select a.ccid,a.ccnr,decode(b.cotrln,'base64') from managed_service_ccontracts a,class_contracttypes b where a.cttyid=b.cttyid AND a.cuid=?");
        ps.setInt(1,Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        ResultSet rs = ps.executeQuery();
        
        String out = "{\"CONTRACT\":[";
        while (rs.next()) { 
            out += "{\"CCID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"CCNR\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"COTRLN\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    static public String UpdateContractType(String cttyid, String cotrsn, String cotrln, String mactions) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "1";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("UPDATE class_contracttypes set COTRLN=?, MACTIONS=? WHERE CTTYID=?");
        ps.setString(1,cotrln.replace("78", "+"));
        ps.setString(2,mactions.replace("78", "+"));
        ps.setInt(3,Integer.parseInt( Base64Coder.decodeString( cttyid ) ));
        ps.executeUpdate();
        
        cn.close();
        return out;
    }
    
    static public String DeleteContractType(String cttyid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "1";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("DELETE FROM class_contracttypes WHERE CTTYID=?");
        ps.setInt(1,Integer.parseInt( Base64Coder.decodeString( cttyid ) ));
        ps.executeUpdate();
        
        PreparedStatement psD = cn.prepareStatement("DELETE FROM managed_service_ccontracts WHERE cttyid=?");
        psD.setInt(1,Integer.parseInt( Base64Coder.decodeString( cttyid ) ));
        psD.executeUpdate();
        
        cn.close();
        return out;
    }
    
    /*
     * Comment Types
     */
    
    static public String GetCommentTypes() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("select comtid,decode(comtsn,'base64'),decode(comtln,'base64') from class_commenttypes order by 3");
        ResultSet rs = ps.executeQuery();
        
        String out = "{\"COMMENT_TYPES\":[";
        while ( rs.next() ) {
            out += "{\"ID\":\"" + Base64Coder.encodeString( rs.getString(1) ) + "\",\"SN\":\"" + Base64Coder.encodeString( rs.getString(2) ) + "\",\"LN\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(3) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    /*
     * Mail Format
     */
    
    static public String GetConfigMailFormat() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("select decode(key,'base64'),decode(val,'base64') from config_gateway where mod = encode('MAILFORMAT','base64')");
        ResultSet rs = ps.executeQuery();
        
        String out = "{\"MAIL_FORMAT\":[";
        while ( rs.next() ) {
            out += "{\"" + rs.getString(1) + "\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString(2) ) ) + "\"},";
        }
        out = out.substring(0, out.length()-1);
        out += "]}";
        
        String replace = out.replace("\":]", "\":[]");
        
        cn.close();
        return replace;
    }
    
    /*
     * Mailing User
     */
    
    static public void AddMailingConfig(String Uuid, String Key, String Val) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("SELECT cpid FROM config_portal WHERE uuid=? AND mod = encode('MAILING','base64') AND key=?");
        ps.setInt(1,Integer.parseInt( Base64Coder.decodeString( Uuid ) ));
        ps.setString(2,Key);
        ResultSet rs = ps.executeQuery();
        
        if ( rs.next() ) { 
            PreparedStatement psU = cn.prepareStatement("UPDATE config_portal SET VAL1=? WHERE uuid=? AND mod = encode('MAILING','base64') AND key=?");
            psU.setString(1,Val.replace("78", "+"));
            psU.setInt(2,Integer.parseInt( Base64Coder.decodeString( Uuid ) ));
            psU.setString(3,Key);
            psU.executeUpdate();
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO config_portal(UUID,MOD,KEY,VAL1) VALUES (?,encode('MAILING','base64'),?,?)");
            psD.setInt(1,Integer.parseInt( Base64Coder.decodeString( Uuid ) ));
            psD.setString(2,Key);
            psD.setString(3,Val.replace("78", "+"));
            psD.executeUpdate();
        }
        /*
         * Close Connection
         */
        cn.close();
    }
    
    /*
     * Mail Konfiguration
     */
    
    static public String GetMailConfig() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("SELECT decode(key,'base64'),decode(val,'base64') FROM config_gateway WHERE mod = encode('MAILAPI','base64')");
        ResultSet rs = ps.executeQuery();
        
        out = "{";
        while ( rs.next() ) { 
            out+="\"" + rs.getString( 1 ) + "\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 2 ) ) ) + "\",";
        }
        out = out.substring(0, out.length()-1);
        out += "}";
        
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public void AddMailConfig(String Key, String Val) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("SELECT cgid FROM config_gateway WHERE mod = encode('MAILAPI','base64') AND key=?");
        ps.setString(1,Key);
        ResultSet rs = ps.executeQuery();
        
        if ( rs.next() ) { 
            PreparedStatement psU = cn.prepareStatement("UPDATE config_gateway SET VAL=? WHERE mod = encode('MAILAPI','base64') AND key=?");
            psU.setString(1,Val.replace("78", "+"));
            psU.setString(2,Key);
            psU.executeUpdate();
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO config_gateway(MOD,KEY,VAL) VALUES (encode('MAILAPI','base64'),?,?)");
            psD.setString(1,Key);
            psD.setString(2,Val.replace("78", "+"));
            psD.executeUpdate();
        }
        /*
         * Close Connection
         */
        cn.close();
    }
    
    static public String GetUserMailFormat(String Uuid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = null;
        String sign = null;
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository");
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("SELECT decode(key,'base64'),decode(val1,'base64') FROM config_portal WHERE uuid=? AND mod = encode('MAILING','base64')");
        ps.setInt(1, Integer.parseInt( Base64Coder.decodeString( Uuid ) ));
        ResultSet rs = ps.executeQuery();
        
        out = "{";
        while ( rs.next() ) { 
            if ("SIGN".equals(rs.getString( 1 ))) {
                sign = rs.getString( 2 );
            } else {
                out+="\"" + rs.getString( 1 ) + "\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 2 ) ) ) + "\",";
            }
        }
        out = out.substring(0, out.length()-1);
        out += ",";
        
        PreparedStatement ps2 = cn.prepareStatement("SELECT decode(key,'base64'),decode(val,'base64') FROM config_gateway WHERE mod = encode('MAILAPI','base64')");
        ResultSet rs2 = ps2.executeQuery();
        
        while ( rs2.next() ) { 
            if ("FOOTER".equals(rs2.getString( 1 ))) {
                sign += "<br></br>" + rs2.getString( 2 );
            } else {
                out+="\"" + rs2.getString( 1 ) + "\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 2 ) ) ) + "\",";
            }
        }
        out = out.substring(0, out.length()-1);
        out += ",\"FOOTER\":\"" + Base64Coder.encodeString( Basics.encodeHtml( sign ) ) + "\"}";
        
        String replace = out.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return replace;
    }
    
    static public String GetCustomerMailing(String Cuid,String Uuid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String esk1 = null;
        String esk2 = null;
        String esk3 = null;
        String to = null;
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository");
        Connection cn = ds.getConnection();
        
        PreparedStatement ps2 = cn.prepareStatement("SELECT decode(cumail,'base64'),decode(cueskmail,'base64') FROM managed_service_cinfo WHERE cuid=?");
        ps2.setInt(1, Integer.parseInt( Base64Coder.decodeString( Cuid ) ));
        ResultSet rs2 = ps2.executeQuery();
        
        while ( rs2.next() ) { 
            esk1 = rs2.getString( 2 );
            esk2 = rs2.getString( 2 );
            esk3 = rs2.getString( 2 );
            to = rs2.getString( 1 );
        }
        
        PreparedStatement ps = cn.prepareStatement("SELECT decode(key,'base64'),decode(val1,'base64') FROM config_portal WHERE uuid=? AND mod = encode('MAILING','base64') AND key = encode('ESK1','base64') OR key = encode('ESK2','base64') OR key = encode('ESK3','base64')");
        ps.setInt(1, Integer.parseInt( Base64Coder.decodeString( Uuid ) ));
        ResultSet rs = ps.executeQuery();
        
        while ( rs.next() ) {
            if ("ESK1".equals(rs.getString(1))) {
                esk1 += "," + rs.getString( 2 );
            } else if ("ESK2".equals(rs.getString(1))) {
                esk2 += "," + rs.getString( 2 );
            } else if ("ESK3".equals(rs.getString(1))) {
                esk3 += "," + rs.getString( 2 );
            }
        }
        
        String out = "{\"TO\":\"" + Base64Coder.encodeString( Basics.encodeHtml( to ) ) + "\",\"ESK1\":\"" + Base64Coder.encodeString( Basics.encodeHtml( esk1 ) ) + "\",\"ESK2\":\"" + Base64Coder.encodeString( Basics.encodeHtml( esk2 ) ) + "\",\"ESK3\":\"" + Base64Coder.encodeString( Basics.encodeHtml( esk3 ) ) + "\"}";
        
        cn.close();
        return out;
    }
    
    /*
     * Service Entry
     */
    
    static public String CreateServiceEntry(String uuid, String cuid, String ccid, String comtid, String tm, String dl, String co, String esk) throws FileNotFoundException, IOException, NamingException, SQLException, ParseException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "1";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        PreparedStatement ps = cn.prepareStatement("INSERT INTO managed_service_cservices(CUID,CCID,ENID,COMTID,UUID,COMT,DELAY,UTIM,ESK) VALUES (?,?,?,?,?,?,?,?,?)");
        ps.setInt(1, Integer.parseInt( Base64Coder.decodeString( cuid ) ));
        ps.setInt(2, Integer.parseInt( Base64Coder.decodeString( ccid ) ));
        ps.setInt(3, 1);
        ps.setInt(4, Integer.parseInt( Base64Coder.decodeString( comtid ) ));
        ps.setInt(5, Integer.parseInt( Base64Coder.decodeString( uuid ) ));
        ps.setString(6,co.replace("78", "+"));
        ps.setInt(7, Integer.parseInt( Base64Coder.decodeString( dl ) ));
        ps.setInt(8, Integer.parseInt( Basics.ConvertDate( Base64Coder.decodeString( tm ) ) ));
        ps.setInt(9, Integer.parseInt( Base64Coder.decodeString( esk ) ));
        ps.executeUpdate();
        
        /*
         * Close Connection
         */
        cn.close();
        return out;
    }
    
    static public String GetServiceEntry(String Uuid, String Offset, String Limit) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = null;
        String count = null;
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository");
        Connection cn = ds.getConnection();
        
        PreparedStatement psC = cn.prepareStatement("select count(*) from managed_service_cservices a,managed_service_cinfo b,profiles_user c,managed_service_ccontracts d,class_contracttypes e where a.cuid=b.cuid and a.uuid=c.uuid and a.ccid=d.ccid and d.cttyid=e.cttyid");
        ResultSet rsC = psC.executeQuery();
        
        if ( rsC.next() ) { count = rsC.getString( 1 ); }
        
        
        PreparedStatement ps = cn.prepareStatement("select decode(c.usdc,'base64'),decode(c.usnm,'base64'),d.ccnr,decode(e.cotrln,'base64'),decode(b.cunm,'base64'),decode(a.comt,'base64'),a.delay,a.utim,a.esk,bit_length(c.upic) from managed_service_cservices a,managed_service_cinfo b,profiles_user c,managed_service_ccontracts d,class_contracttypes e where a.cuid=b.cuid and a.uuid=c.uuid and a.ccid=d.ccid and d.cttyid=e.cttyid order by a.msid DESC offset ? limit ?");
        ps.setInt(1, Integer.parseInt( Base64Coder.decodeString( Offset ) ));
        ps.setInt(2, Integer.parseInt( Base64Coder.decodeString( Limit ) ));
        ResultSet rs = ps.executeQuery();
        
        out = "{\"COUNT\":\"" + Base64Coder.encodeString( count ) + "\",\"ROWS\":[";
        while ( rs.next() ) {
            out += "{\"NAME\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 1 ) ) ) + "\",\"UID\":\"" + Base64Coder.encodeString( rs.getString( 2 ) ) + "\",\"AN\":\"" + Base64Coder.encodeString( rs.getString( 3 ) ) + "\",\"CONM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 4 ) ) ) + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 5 ) ) ) + "\",\"TEXT\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 6 ) ) ) + "\",\"TS\":\"" + Base64Coder.encodeString( rs.getString( 8 ) ) + "\",\"ESK\":\"" + Base64Coder.encodeString( rs.getString( 9 ) ) + "\",\"PCTRL\":\"" + rs.getString( 10 ) + "\"},";
        }
        out = out.substring(0, out.length()-1); out += "]}";
        String replace = out.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return replace;
    }
    
    /*
     * Customer Role Mapping for Configuration
     */
    
    static public String GetCustomerRole() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        /* Require Base64 Value for Parameter Id */
        String line = "";
        Context ctxR = new InitialContext(); 
        DataSource dsR  = (DataSource) ctxR.lookup("jdbc/repository"); 
        Connection cnR = dsR.getConnection();
        
        PreparedStatement psrl = cnR.prepareStatement("SELECT rlid,decode(rlnm,'base64'),decode(rlde,'base64') FROM profiles_role order by 3");
        ResultSet rl = psrl.executeQuery();
        PreparedStatement pscu = cnR.prepareStatement("SELECT cuid,decode(cunm,'base64'),cunr FROM managed_service_cinfo order by 3");
        ResultSet cu = pscu.executeQuery();
        PreparedStatement psma = cnR.prepareStatement("SELECT cuid,rlid FROM profiles_customer_role_mapping");
        ResultSet ma = psma.executeQuery();
        
        List<String> macuid = new ArrayList<String>();
        List<String> marlid = new ArrayList<String>();
        
        while (ma.next()) {
            macuid.add(ma.getString( 1 ));
            marlid.add(ma.getString( 2 ));
        }
        
        line = "{\"ROLE\":[";
        while (rl.next()) {
            line += "{\"ROID\":\"" + rl.getString( 1 ) + "\",\"RONM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rl.getString( 2 ) ) ) + "\",\"RODC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rl.getString( 3 ) ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1);
        line += "],\"CUSTOMER\":[";
        while (cu.next()) {
            String cuid = cu.getString( 1 );
            String cunm = cu.getString( 2 );
            String cunr = cu.getString( 3 );
            line += "{\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cunm ) ) + "\",\"CUNR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cunr ) ) + "\",\"CUID\":\"" + cuid + "\",\"ROLES\":[";
            for (int i=0;i<macuid.size();i++) {
                if (cuid.equals(macuid.get(i))) {
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
        return replace;
    }
    
    /*
     * Contract Role Mapping for Configuration
     */
    
    static public String GetContractRole() throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        /* Require Base64 Value for Parameter Id */
        String line = "";
        Context ctxR = new InitialContext(); 
        DataSource dsR  = (DataSource) ctxR.lookup("jdbc/repository"); 
        Connection cnR = dsR.getConnection();
        
        PreparedStatement psrl = cnR.prepareStatement("SELECT rlid,decode(rlnm,'base64'),decode(rlde,'base64') FROM profiles_role order by 3");
        ResultSet rl = psrl.executeQuery();
        PreparedStatement pscc = cnR.prepareStatement("select a.ccid,decode(b.cotrln,'base64'),a.ccnr,decode(c.cunm,'base64') from managed_service_ccontracts a, class_contracttypes b, managed_service_cinfo c where a.cttyid=b.cttyid and a.cuid=c.cuid order by 3");
        ResultSet cc = pscc.executeQuery();
        PreparedStatement psma = cnR.prepareStatement("SELECT ccid,rlid FROM profiles_contract_role_mapping");
        ResultSet ma = psma.executeQuery();
        
        List<String> maccid = new ArrayList<String>();
        List<String> marlid = new ArrayList<String>();
        
        while (ma.next()) {
            maccid.add(ma.getString( 1 ));
            marlid.add(ma.getString( 2 ));
        }
        
        line = "{\"ROLE\":[";
        while (rl.next()) {
            line += "{\"ROID\":\"" + rl.getString( 1 ) + "\",\"RONM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rl.getString( 2 ) ) ) + "\",\"RODC\":\"" + Base64Coder.encodeString( Basics.encodeHtml( rl.getString( 3 ) ) ) + "\"},";
        }
        line = line.substring(0, line.length()-1);
        line += "],\"CONTRACT\":[";
        while (cc.next()) {
            String ccid = cc.getString( 1 );
            String ccnm = cc.getString( 2 );
            String ccnr = cc.getString( 3 );
            String cunm = cc.getString( 4 );
            line += "{\"CCNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( ccnm ) ) + "\",\"CCNR\":\"" + Base64Coder.encodeString( Basics.encodeHtml( ccnr ) ) + "\",\"CCID\":\"" + ccid + "\",\"CUNM\":\"" + Base64Coder.encodeString( Basics.encodeHtml( cunm ) ) + "\",\"ROLES\":[";
            for (int i=0;i<maccid.size();i++) {
                if (ccid.equals(maccid.get(i))) {
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
        return replace;
    }
    
    /*
     * Update Contract / Customer Role Mapping
     */
    
    static public String UpdateContractRole(String Ccid, String Rlid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("SELECT ccid,rlid FROM profiles_contract_role_mapping WHERE ccid=? AND rlid=?");
        ps.setInt(1,Integer.parseInt(Ccid));
        ps.setInt(2,Integer.parseInt(Rlid));
        ResultSet rs = ps.executeQuery();
        /*
         * Update
         */
        if (rs.next()) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_contract_role_mapping WHERE ccid=? AND rlid=?");
            psD.setInt(1,Integer.parseInt(Ccid));
            psD.setInt(2,Integer.parseInt(Rlid));
            psD.executeUpdate();
            out = "1";
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_contract_role_mapping (CCID,RLID) VALUES (?,?)");
            psD.setInt(1,Integer.parseInt(Ccid));
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
    
    static public String UpdateCustomerRole(String Cuid, String Rlid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String out = "0";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("SELECT cuid,grid FROM profiles_customer_role_mapping WHERE cuid=? AND rlid=?");
        ps.setInt(1,Integer.parseInt(Cuid));
        ps.setInt(2,Integer.parseInt(Rlid));
        ResultSet rs = ps.executeQuery();
        /*
         * Update
         */
        if (rs.next()) {
            PreparedStatement psD = cn.prepareStatement("DELETE FROM profiles_customer_role_mapping WHERE cuid=? AND rlid=?");
            psD.setInt(1,Integer.parseInt(Cuid));
            psD.setInt(2,Integer.parseInt(Rlid));
            psD.executeUpdate();
            out = "1";
        } else {
            PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_customer_role_mapping (CUID,RLID) VALUES (?,?)");
            psD.setInt(1,Integer.parseInt(Cuid));
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
}
