/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.mailing;

import de.siv.ksc.modules.Basics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author sbaresel
 */
public class MailHtml {
    static Properties props = null;
    
    static public void send(String Recipient, String CCRecipient, String Sender, String Subject, String Text) throws FileNotFoundException, IOException, NoSuchProviderException, MessagingException {
        if (props == null) {
            props = Basics.getConfiguration();
        }
        String host=props.getProperty("MAIL.HOST");
        int port=Integer.parseInt(props.getProperty("MAIL.PORT"));
        String user=props.getProperty("MAIL.USER");
        String pass=props.getProperty("MAIL.PASS");
        
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
