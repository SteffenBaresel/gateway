/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.mailing;

import de.siv.ksc.modules.Base64Coder;
import de.siv.ksc.modules.Basics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author sbaresel
 */
public class MailHtml {
    static Properties props = null;
    
    static public void send(String Recipient, String CCRecipient, String Sender, String Subject, String Text) throws FileNotFoundException, IOException, NoSuchProviderException, MessagingException, NamingException, SQLException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String host = null;
        int port = 0;
        String user = null;
        String pass = null;
                
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository");
        Connection cn  = ds.getConnection(); 
        PreparedStatement ps = cn.prepareStatement("SELECT decode(key,'base64'),decode(val,'base64') FROM config_gateway WHERE MOD like encode('MAILAPI','base64') ORDER BY 1 ASC");
        ResultSet rs = ps.executeQuery();
        while ( rs.next() ) { 
            if ("HOST".equals(rs.getString( 1 ))) {
                host = rs.getString( 2 );
            } else if ("PORT".equals(rs.getString( 1 ))) {
                port = Integer.parseInt(rs.getString( 2 ));
            } else if ("USER".equals(rs.getString( 1 ))) {
                user = rs.getString( 2 );
            } else if ("PASS".equals(rs.getString( 1 ))) {
                pass = rs.getString( 2 );
            }
        } 
                
        Properties mail=new Properties();
        mail.put("mail.smtp.auth", "true");
        mail.put("mail.smtp.starttls.enable", "true");
        mail.put("mail.from", Sender);
        
        Session session=Session.getInstance(mail);
        Transport transport=session.getTransport("smtp");
        transport.connect(host, port, user, pass);
        
        Address[] addresses=InternetAddress.parse(Recipient);
        Address[] ccaddresses=InternetAddress.parse(CCRecipient);
        
        Message message=new MimeMessage(session);
        message.setFrom();
        message.setRecipients(Message.RecipientType.TO, addresses);
        message.addRecipients(Message.RecipientType.CC, ccaddresses);
        message.setSubject(Subject);
        
        message.setText(Text);
        
        transport.sendMessage(message, addresses);
        
        transport.close();
    }
}
