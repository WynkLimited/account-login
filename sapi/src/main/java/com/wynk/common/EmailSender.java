package com.wynk.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;

/**
 * @author yatendra
 *
 */
@Service
public class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class.getCanonicalName());

    public boolean sendGmail(String from, List<String> to, List<String> cc, List<String> bcc, String subject, String body, final String userName, final String password) {
    	try {
    		Properties properties = new Properties();
    		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
    		properties.setProperty("mail.smtp.socketFactory.port", "587");
    		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.SocketFactory");
    		properties.setProperty("mail.smtp.auth", "true");
    		properties.setProperty("mail.smtp.port", "587");
    		properties.setProperty("mail.smtp.ssl.enable", "false");
    		properties.setProperty("mail.smtp.starttls.enable", "true");
    		properties.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
    		
    		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}

			});
    		
    		MimeMessage message = new MimeMessage(session);
    		message.setFrom(new InternetAddress(from));
    		if(to != null) {
    			for(String s : to) {
    				message.addRecipient(RecipientType.TO, new InternetAddress(s));
    			}
    		}
    		if(cc != null) {
    			for(String s : cc) {
    				message.addRecipient(RecipientType.CC, new InternetAddress(s));
    			}
    		}
    		if(bcc != null) {
    			for(String s : bcc) {
    				message.addRecipient(RecipientType.BCC, new InternetAddress(s));
    			}
    		}
    		message.setSubject(subject, "utf-8");

    		BodyPart bodyPart = new MimeBodyPart();
    		bodyPart.setContent(body, "text/html");

    		Multipart mp = new MimeMultipart();
    		mp.addBodyPart(bodyPart);

    		message.setContent(mp);

    		Transport.send(message);
    		logger.debug("sent message from:" + from + " to:" + to + " cc:" + cc + " bcc:" + bcc + " subject:" + subject);
    		return true;
    	}
    	catch (Exception e) {
    		logger.error(e.getMessage(), e);
    	}
    	return false;
    }

}
