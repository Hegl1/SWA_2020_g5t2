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
import org.springframework.security.access.prepost.PreAuthorize;
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
	BorrowedRepository borrowedRepository;

	@Autowired
	ReservedRepository reservedRepository;

	@Autowired
	MediaBorrowTimeRepository mediaBorrowTimeRepository;

	@Autowired
	MailService mailService;

	@Autowired
	MediaRepository mediaRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	private Logger logger = LoggerFactory.getLogger(BorrowService.class);

	/**
	 * Method that borrows Media for a customer.
	 *
	 * @param mediaToBorrow media to borrow
	 * @return true if it was successfully borrowed, else false
	 */
	public boolean borrowMedia(final User borrower, final Media mediaToBorrow) {

		if (mediaToBorrow.getTotalAvail() <= mediaToBorrow.getCurBorrowed()) {
			return false;
		} else {
			mediaToBorrow.setCurBorrowed(mediaToBorrow.getCurBorrowed() + 1);
			mediaRepository.save(mediaToBorrow);
			Borrowed borrow = new Borrowed(borrower, mediaToBorrow, new Date());
			borrowedRepository.save(borrow);
			return true;
		}
	}

	// TODO probably refactor, put link between link parameter and media in
	// controller
	public boolean borrowMedia(final User borrower, final String linkParameter) {
		Long linkParameterLong = Long.parseLong(linkParameter);

		Media mediaToBorrow;
		if (linkParameter != null) {
			mediaToBorrow = mediaRepository.findFirstByMediaID(linkParameterLong);
		} else {
			mediaToBorrow = mediaRepository.findFirstByMediaID(1L);
		}
		return borrowMedia(borrower, mediaToBorrow);
	}

	public boolean borrowMediaForAuthenticatedUser(final String mediaToBorrow_String) {
		User borrower = userService.loadCurrentUser();
		return borrowMedia(borrower, mediaToBorrow_String);
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void unBorrowMedia(final Borrowed borrowed) {
		borrowedRepository.delete(borrowed);
		borrowed.getMedia().setCurBorrowed(borrowed.getMedia().getCurBorrowed() - 1);
		mediaRepository.save(borrowed.getMedia());
		Collection<Reserved> reservations = reservedRepository.findByMedia(borrowed.getMedia());

		for (Reserved current : reservations) {
			unreserveMedia(current);
		}
	}

	public void unBorrowMedia(final User borrower, final Media mediaToUnBorrow) {
		unBorrowMedia(borrowedRepository.findFirstByUserAndMedia(borrower, mediaToUnBorrow));
	}

	public void unBorrowMediaForAuthenticatedUser(final Media mediaToUnBorrow) {
		User user = userService.loadCurrentUser();
		unBorrowMedia(user, mediaToUnBorrow);
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Borrowed> getAllBorrows() {
		return borrowedRepository.findAll();
	}

	public Collection<Borrowed> getAllBorrowsByUser(final User user) {
		return borrowedRepository.findByUser(user);
	}

	public Collection<Borrowed> getAllBorrowsByAuthenticatedUser() {
		return getAllBorrowsByUser(userService.loadCurrentUser());
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Borrowed> getAllBorrowsByMedia(final Media media) {
		return borrowedRepository.findByMedia(media);
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Borrowed loadBorrowed(final User borrower, final Media media) {
		return borrowedRepository.findFirstByUserAndMedia(borrower, media);
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void reserveMedia(final User reserver, final Media mediaToReserve) {
		Reserved res = new Reserved(reserver, mediaToReserve, new Date());
		reservedRepository.save(res);
	}

	public void reserveMediaForAuthenticatedUser(final Media mediaToResMedia) {
		reserveMedia(userService.loadCurrentUser(), mediaToResMedia);
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
		reservedRepository.delete(reserved);
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Reserved> getAllReserved() {
		return reservedRepository.findAll();
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Reserved> getAllReservedByUser(final User user) {
		return reservedRepository.findByUser(user);
	}

	public Collection<Reserved> getAllReservedByAuthenticatedUser() {
		return getAllReservedByUser(userService.loadCurrentUser());
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Reserved> getAllReservedByMedia(final Media media) {
		return reservedRepository.findByMedia(media);
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Reserved loadReserved(final User user, final Media media) {
		return reservedRepository.findByUserAndMedia(user, media);
	}

	@Scheduled(fixedDelay = schedulingDelay, initialDelay = 30000)
	public void checkBorrowTimeout() {
		Map<MediaType, Integer> allowedBorrowTimes = getMediaBorrowTimesAsMap();
		Collection<Borrowed> currentlyBorrowed = borrowedRepository.findAll();
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
		Map<MediaType, Integer> allowedBorrowTimes = new HashMap<>();
		Collection<MediaBorrowTime> borrowTimes = mediaBorrowTimeRepository.findAll();
		for (MediaBorrowTime current : borrowTimes) {
			allowedBorrowTimes.put(current.getMediaType(), current.getAllowedBorrowTime());
		}
		return allowedBorrowTimes;
	}

	@Override
	public void run(final String... args) throws Exception {
		// used for initial loading of the component so scheduled task starts
		logger.info("BorrowService Component loaded at startup");
	}

	/**
	 * the following 2 functions return type specific information about the borrowed
	 * media
	 */
	public String getMediaTypeAsString(final Borrowed borrowed) {
		return borrowed.getMedia().getMediaType().toString();
	}

	public String getMediaTitle(final Borrowed borrowed) {
		return borrowed.getMedia().getTitle();
	}

}
