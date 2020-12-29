package at.qe.skeleton.services;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaBorrowTime;
import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.model.Reserved;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.BorrowedRepository;
import at.qe.skeleton.repositories.MediaBorrowTimeRepository;
import at.qe.skeleton.repositories.MediaRepository;
import at.qe.skeleton.repositories.ReservedRepository;
import at.qe.skeleton.repositories.UserRepository;

@Component
@Scope("application")
public class BorrowService implements CommandLineRunner {

	private static final long schedulingDelay = (long) 1e7;

	@Autowired
	BorrowedRepository borrowedRepostiroy;

	@Autowired
	ReservedRepository reserverdreRepository;

	@Autowired
	MediaBorrowTimeRepository mediaBorrowTimeRepository;

	@Autowired
	MailService mailService;

	@Autowired
	MediaRepository mediaRepository;

	@Autowired
	UserRepository userRepository;

	Logger logger = LoggerFactory.getLogger(BorrowService.class);

	// TEMPORAL
	public void testMethod() {
		Media media = mediaRepository.findFirstByMediaID(3L);
		User user = userRepository.findFirstByUsername("csauer");
		unBorrowMedia(user, media);
	}

	/**
	 * Method that borrows Media for a customer.
	 * 
	 * @param borrower      customer that borrows the media
	 * @param mediaToBorrow media to borrow
	 * @return true if it was successfully borrowed, else false
	 */
	public boolean borrowMedia(final User borrower, final Media mediaToBorrow) {
		if (mediaToBorrow.getTotalAvail() <= mediaToBorrow.getCurBorrowed()) {
			// TODO subject to change, maybe add auto reserve mechanism
			return false;
		} else {
			mediaToBorrow.setCurBorrowed(mediaToBorrow.getCurBorrowed() + 1);
			mediaRepository.save(mediaToBorrow);
			Borrowed borrow = new Borrowed(borrower, mediaToBorrow, new Date());
			borrowedRepostiroy.save(borrow);
			return true;
		}
	}

	public boolean borrowMediaForAuthenticatedUser(final Media mediaToBorrow) {
		User borrower = getAuthenticatedUser();
		return borrowMedia(borrower, mediaToBorrow);
	}

	public void unBorrowMedia(final Borrowed borrowed) {
		borrowedRepostiroy.delete(borrowed);
		borrowed.getMedia().setCurBorrowed(borrowed.getMedia().getCurBorrowed() - 1);
		mediaRepository.save(borrowed.getMedia());
		Collection<Reserved> reservations = reserverdreRepository.findByMedia(borrowed.getMedia());
		for (Reserved current : reservations) {
			unreserveMedia(current);
		}
	}

	public void unBorrowMedia(final User borrower, final Media mediaToUnBorrow) {
		unBorrowMedia(borrowedRepostiroy.findFirstByUserAndMedia(borrower, mediaToUnBorrow));
	}

	public void unBorrowMediaForAuthenticatedUser(final Media mediaToUnBorrow) {
		User user = getAuthenticatedUser();
		unBorrowMedia(user, mediaToUnBorrow);
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
		return borrowedRepostiroy.findFirstByUserAndMedia(borrower, media);
	}

	public void reserveMedia(final User reserver, final Media mediaToReserve) {
		Reserved res = new Reserved(reserver, mediaToReserve, new Date());
		reserverdreRepository.save(res);
	}

	private void unreserveMedia(final Reserved reserved) {
		StringBuilder bodyBuild = new StringBuilder();
		Media targetMedia = reserved.getMedia();
		bodyBuild.append("Dear customer, the media \"");
		bodyBuild.append(targetMedia.getTitle());
		bodyBuild.append("\" is available again for borrowing.\n\n");
		bodyBuild.append("Yours sincerely,\nThe Library Team");
		String body = bodyBuild.toString();
		StringBuilder headBuild = new StringBuilder("Borrowing of ");
		headBuild.append(targetMedia.getTitle());
		String head = headBuild.toString();
		mailService.sendMail(reserved.getUser().getEmail(), head, body);
		logger.info("Email about freed media send to " + reserved.getUser().getEmail());
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

	@Scheduled(fixedDelay = schedulingDelay, initialDelay = 30000)
	public void checkBorrowTimeout() {
		Map<MediaType, Integer> allowedBorrowTimes = getMediaBorrowTimesAsMap();
		Collection<Borrowed> currentlyBorrowed = borrowedRepostiroy.findAll();
		Date currentDate = new Date();
		for (Borrowed current : currentlyBorrowed) {
			long diff = currentDate.getTime() - current.getBorrowDate().getTime();
			long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			if (diffInDays > allowedBorrowTimes.get(current.getMedia().getMediaType())) {
				logger.info(
						"Automatic return of media " + current.getMedia().getMediaID() + " due to time limitations");
				unBorrowMedia(current);
			}
		}
	}

	private Map<MediaType, Integer> getMediaBorrowTimesAsMap() {
		Map<MediaType, Integer> allowedBorrowTimes = new HashMap<MediaType, Integer>();
		Collection<MediaBorrowTime> borrowTimes = mediaBorrowTimeRepository.findAll();
		for (MediaBorrowTime current : borrowTimes) {
			allowedBorrowTimes.put(current.getMediaType(), current.getAllowedBorrowTime());
		}
		return allowedBorrowTimes;
	}

	private User getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return this.userRepository.findFirstByUsername(auth.getName());
	}

	@Override
	public void run(final String... args) throws Exception {
		// used for initial loading of the component so scheduled task starts
		logger.info("BorrowService Component loaded at startup");
	}

}
