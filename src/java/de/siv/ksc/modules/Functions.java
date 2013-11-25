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
    
    static public String GetUserConfig(String Uid) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String line = null;
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository");
        Connection cn = ds.getConnection(); 
        line = "[{\"LOCAL_BACKEND\":\"" + props.getProperty("BACKEND.IP") + ":" + props.getProperty("BACKEND.PORT") + "\",\"DASHBOARD\":[";
        PreparedStatement ps = cn.prepareStatement("SELECT decode(val1,'base64'),decode(val2,'base64'),decode(val3,'base64') FROM config_portal WHERE usr=? AND mod = encode('DASHBOARD','base64') ORDER BY cpid ASC");
        ps.setString(1,Base64Coder.encodeString( Uid ));
        ResultSet rs = ps.executeQuery();
        while ( rs.next() ) { 
            String tv1; if (rs.getString( 1 ) == null) { tv1 = Base64Coder.encodeString( "-" ); } else { tv1 = Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 1 ) ) ); }
            String tv2; if (rs.getString( 2 ) == null) { tv2 = Base64Coder.encodeString( "-" ); } else { tv2 = Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 2 ) ) ); }
            String tv3; if (rs.getString( 3 ) == null) { tv3 = Base64Coder.encodeString( "-" ); } else { tv3 = Base64Coder.encodeString( Basics.encodeHtml( rs.getString( 3 ) ) ); }
            line += "{\"TITLE\":\"" + tv1 + "\",\"DESC\":\"" + tv2 + "\",\"TARGET\":\"" + tv3 + "\"},"; 
        } 
        line = line.substring(0, line.length()-1); line += "],\"USER_CONFIG\":["; 
        /* Zweite Query */
        PreparedStatement ps2 = cn.prepareStatement("SELECT decode(key,'base64'),decode(val1,'base64'),decode(val2,'base64'),decode(val3,'base64') FROM config_portal WHERE usr=? AND mod = encode('Config','base64') ORDER BY cpid ASC");
        ps2.setString(1,Base64Coder.encodeString( Uid ));
        ResultSet rs2 = ps2.executeQuery();
        while ( rs2.next() ) {
            String tky; if (rs2.getString( 1 ) == null) { tky = Base64Coder.encodeString( "-" ); } else { tky = Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 1 ) ) ); }
            String tv1; if (rs2.getString( 2 ) == null) { tv1 = Base64Coder.encodeString( "-" ); } else { tv1 = Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 2 ) ) ); }
            String tv2; if (rs2.getString( 3 ) == null) { tv2 = Base64Coder.encodeString( "-" ); } else { tv2 = Base64Coder.encodeString( Basics.encodeHtml( rs2.getString( 3 ) ) ); }
            line += "{\"KEY\":\"" + tky + "\",\"ACTION\":\"" + tv1 + "\",\"DESC\":\"" + tv2 + "\"},";
        }
        line = line.substring(0, line.length()-1); line += "],";
        /* Dritte Query */
        PreparedStatement ps3 = cn.prepareStatement("SELECT decode(usdc,'base64'),decode(umai,'base64'),bit_length(upic) FROM profiles_user WHERE usnm=?");
        ps3.setString(1,Base64Coder.encodeString( Uid ));
        ResultSet rs3 = ps3.executeQuery();
        while ( rs3.next() ) { 
            String fnam; if (rs3.getString( 1 ) == null) { fnam = Base64Coder.encodeString( "-" ); } else { fnam = Base64Coder.encodeString( Basics.encodeHtml( rs3.getString( 1 ) ) ); }
            String mail; if (rs3.getString( 2 ) == null) { mail = Base64Coder.encodeString( "-" ); } else { mail = Base64Coder.encodeString( Basics.encodeHtml( rs3.getString( 2 ) ) ); }
            line += "\"UID\":\"" + Base64Coder.encodeString( Uid ) + "\",\"NAME\":\"" + fnam + "\",\"MAIL\":\"" + mail + "\",\"PCTRL\":\"" + Base64Coder.encodeString( rs3.getString( 3 ) ) + "\",\"PCTR\":\"" + Base64Coder.encodeString("/gateway/exec/UserPicture") + "\",";
        }
        line = line.substring(0, line.length()-1);
        // User Group
        line += ",\"USER_GROUPS\":[" + Functions.UsersGroups(Uid) + "]";
        // USer Permission
        line += ",\"USER_PERM\":[" + Functions.UsersPermissions(Uid) + "]";
        //
        line += "}]";
        String replace = line.replace("\n", "").replace("\r", "").replace("\":]", "\":[]");
        cn.close();
        return replace;
    }
    
    static public void AddDashboardLink(String Uid, String Title, String Desc, String Target) throws FileNotFoundException, IOException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        PreparedStatement psD = cn.prepareStatement("INSERT INTO config_portal(USR,MOD,KEY,VAL1,VAL2,VAL3) VALUES (?,encode('DASHBOARD','base64'),encode('LINK','base64'),?,?,?)");
        psD.setString(1,Base64Coder.encodeString( Uid ));
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
                PreparedStatement psD = cn.prepareStatement("INSERT INTO profiles_user(USNM,USDC,UMAI,UCRT,ULAL,UACT,UPIC) values (?,?,?,?,?,?,'')");
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
}
