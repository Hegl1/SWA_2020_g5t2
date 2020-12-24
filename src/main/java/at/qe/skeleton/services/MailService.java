package at.qe.skeleton.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Scope("application")
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendMail(final String targetMail, final String messageSubject, final String messageContent)
			throws MailException {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(targetMail);
		msg.setFrom("swa.grp.5.2@gmail.com");
		msg.setSubject(messageSubject);
		msg.setText(messageContent);
		mailSender.send(msg);
	}

}
