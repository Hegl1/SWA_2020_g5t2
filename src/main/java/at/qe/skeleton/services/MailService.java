package at.qe.skeleton.services;

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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
@Scope("application")
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	private Logger logger = LoggerFactory.getLogger(MailService.class);

	public void sendMail(final String targetMail, final String messageSubject, final String messageContent)
			throws MailException {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(targetMail);
		msg.setFrom("swa.grp.5.2@gmail.com");
		msg.setSubject(messageSubject);
		msg.setText(messageContent);
		mailSender.send(msg);
	}

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

			mailSender.send(msg);
		} catch (MessagingException e) {
			logger.error("Error while constructing the msgHelper in MailService, no mail sent.");
		}

	}

}
