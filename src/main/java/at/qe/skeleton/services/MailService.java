package at.qe.skeleton.services;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Class that provides the ability to send emails either with our withouth
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

	private Logger logger = LoggerFactory.getLogger(MailService.class);

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
		Runnable runner = () -> {
			mailSender.send(msg);
		};
		Thread sender = new Thread(runner);
		sender.start();
	}

	/**
	 * Method that sends an email with an attachment.
	 * 
	 * @param targetMail     the target email address
	 * @param messageSubject the subject of the email
	 * @param messagecontent the content of the email
	 * @param attachmentName the name of the attachment
	 * @param path           the path of the attachment
	 * @throws MailException thrown when the email cannot be constructed or send for
	 *                       whatever reason.
	 */
	public void sendMailWithAttachment(final String targetMail, final String messageSubject,
			final String messagecontent, final String attachmentName, final String path) throws MailException {
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper msgHelper = new MimeMessageHelper(msg, true);
			msgHelper.setTo(targetMail);
			msgHelper.setFrom("swa.grp.5.2@gmail.com");
			msgHelper.setSubject(messageSubject);
			msgHelper.setText(messagecontent);

			FileSystemResource attachment = new FileSystemResource(new File(path));
			msgHelper.addAttachment(attachmentName, attachment);

			Runnable runner = () -> {
				mailSender.send(msg);
			};
			Thread sender = new Thread(runner);
			sender.start();
		} catch (MessagingException e) {
			logger.error("Error while constructing the msgHelper in MailService, no mail sent.");
		}

	}

}
