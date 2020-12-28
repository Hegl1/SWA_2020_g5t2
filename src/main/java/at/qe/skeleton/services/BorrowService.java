package at.qe.skeleton.services;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.Reserved;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.BorrowedRepository;
import at.qe.skeleton.repositories.ReservedRepository;
import at.qe.skeleton.repositories.UserRepository;

@Component
@Scope("application")
public class BorrowService {

	@Autowired
	BorrowedRepository borrowedRepostiroy;

	@Autowired
	ReservedRepository reserverdreRepository;

	@Autowired
	MailService mailService;

	// TEMPORAL
	@Autowired
	UserRepository urep;

	// Temporal
	@Autowired
	MediaService mediaService;

	// TEMPORAL
	public Borrowed testMethod() {
		System.out.println("Test method call");
		Media u = mediaService.getAllMedia().iterator().next();
		User us = urep.findFirstByUsername("csauer");
		Borrowed r = loadBorrowed(us, u);
		System.out.println("id " + r.getId());
		return r;
	}

	public boolean borrowMedia(final User borrower, final Media mediaToBorrow) {
		// TODO implement functionality
		// Maybe one extra metho for authenticated user?
		return false;
	}

	public void unBorrowMedia(final User borrower, final Media mediaToUnBorrow) {
		// TODO implement functionality
	}

	public Collection<Borrowed> getAllBorrows() {
		return borrowedRepostiroy.findAll();
	}

	public Collection<Borrowed> getAllBorrowsByUser(final User user) {
		return borrowedRepostiroy.findByUser(user);
	}

	public Collection<Borrowed> getAllBorrowsByMedia(final Media media) {
		return borrowedRepostiroy.findByMedia(media);
	}

	public Borrowed loadBorrowed(final User borrower, final Media media) {
		return borrowedRepostiroy.findByUserAndMedia(borrower, media);
	}

	private void reserveMedia(final User reserver, final Media mediaToReserve) {
		StringBuilder bodyBuild = new StringBuilder();
		bodyBuild.append("Dear customer, the media ");
		bodyBuild.append(mediaToReserve.getTitle());
		bodyBuild.append(
				" is currently not available.\n You will be notified as soon as the media is available again!\n\n");
		bodyBuild.append("Yours sincerely,\nThe Library Team");
		String body = bodyBuild.toString();
		StringBuilder headBuild = new StringBuilder("Borrowing of ");
		headBuild.append(mediaToReserve.getTitle());
		String head = headBuild.toString();
		mailService.sendMail(reserver.getEmail(), head, body);
		Reserved res = new Reserved(reserver, mediaToReserve, new Date());
		reserverdreRepository.save(res);
	}

	private void unreserveMedia(final Reserved reserved) {
		StringBuilder bodyBuild = new StringBuilder();
		Media targetMedia = reserved.getMedia();
		bodyBuild.append("Dear customer, the media ");
		bodyBuild.append(targetMedia.getTitle());
		bodyBuild.append(" is available again for borrowing.\n\n");
		bodyBuild.append("Yours sincerely,\nThe Library Team");
		String body = bodyBuild.toString();
		StringBuilder headBuild = new StringBuilder("Borrowing of ");
		headBuild.append(targetMedia.getTitle());
		String head = headBuild.toString();
		mailService.sendMail(reserved.getUser().getEmail(), head, body);
		reserverdreRepository.delete(reserved);
	}

	public Collection<Reserved> getAllReserved() {
		return reserverdreRepository.findAll();
	}

	public Collection<Reserved> getAllReservedByUser(final User user) {
		return reserverdreRepository.findByUser(user);
	}

	public Collection<Reserved> getAllReservedByMedia(final Media media) {
		return reserverdreRepository.findByMedia(media);
	}

	public Reserved loadReserved(final User user, final Media media) {
		return reserverdreRepository.findByUserAndMedia(user, media);
	}

	public void checkBorrowTimeout() {
		// TODO implement functionality
		// find annotation for proper timing
	}

}
