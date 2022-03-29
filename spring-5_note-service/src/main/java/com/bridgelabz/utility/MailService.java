package com.bridgelabz.utility;



import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.bridgelabz.configuration.RabbitConfiguration;
import com.bridgelabz.models.Note;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * This Class is used for sending the mail to the user
 *
 * @author Shubham Verma
 */
@Component
public class MailService {

	final String host = "smtp.gmail.com";
	final String sender = System.getenv("MY_USERNAME");
	final String pass = System.getenv("MY_PASSWORD");
	final String SUBJECT_VERIFY = "Fundoo Email verification link";
	final String SUBJECT_RESET = "Fundoo Password Reset Link";


	Properties props = new Properties();

	@Autowired
	JwtToken jwtToken;

	@Autowired
	Environment environment;

	@RabbitListener(queues = RabbitConfiguration.MAIL_REMINDER_QUEUE)
	public void commingEmailVerifyReq(String email) {
		System.out.println(email);
		String token = jwtToken.generateToken(email);
		String content = "Click on the below link to verify your email id http://localhost:8080/api/verifyUser/";
		sendMail(email,token, SUBJECT_VERIFY, content);
	}

	public String sendMail(String email, String token, String subject , String content) {

		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender, pass);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject(subject);
			message.setText(
					content + token);
			Transport.send(message);
			return environment.getProperty("emailSent");
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Some Error occurred";
		}
	}

	
	public String sendNoteReminderMail(String email , Note note) {

		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		String token = jwtToken.generateToken(email);

		Session session = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender, pass);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject("Fundoo Note Reminder");
			message.setText(
					"Note title:- " + note.getTitle() +"\n Note Content:- " + note.getNoteBody());
			Transport.send(message);
			return environment.getProperty("emailSent");
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Some Error occurred";
		}
	}
	
}
