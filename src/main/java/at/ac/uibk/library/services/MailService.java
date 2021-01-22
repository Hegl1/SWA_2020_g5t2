package at.ac.uibk.library.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Class that provides the ability to send emails either with our without
 * attachment.
 * 
 * @author Marcel Huber
 * @version 1.0
 *
 */

@Component
@Scope("application")
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	/**
	 * Method that sends an email without an attachment.
	 * 
	 * @param targetMail     the target email address
	 * @param messageSubject the subject of the email
	 * @param messageContent the content of the email
	 * @throws MailException thrown when the email cannot be constructed or send for
	 *                       whatever reason.
	 */
	public void sendMail(final String targetMail, final String messageSubject, final String messageContent)
			throws MailException {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(targetMail);
		msg.setFrom("swa.grp.5.2@gmail.com");
		msg.setSubject(messageSubject);
		msg.setText(messageContent);
		Runnable runner = () -> mailSender.send(msg);
		Thread sender = new Thread(runner);
		sender.start();
	}
}
