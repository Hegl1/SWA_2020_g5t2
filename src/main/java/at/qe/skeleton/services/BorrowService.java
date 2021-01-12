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

/**
 * Class that is used for the borrowing process, reservation process and
 * automatic return of expired borrows.
 * 
 * @author Marcel Huber
 * @version 1.0
 *
 */

@Component
@Scope("application")
public class BorrowService implements CommandLineRunner {

	/**
	 * Constant that defines how much times elapses between the scannings for
	 * expired borrow times.
	 */
	private static final long schedulingDelay = (long) 1e7;

	@Autowired
	private BorrowedRepository borrowedRepository;

	@Autowired
	private ReservedRepository reservedRepository;

	@Autowired
	private MediaBorrowTimeRepository mediaBorrowTimeRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private MediaRepository mediaRepository;

	@Autowired
	private UserService userService;

	private Logger logger = LoggerFactory.getLogger(BorrowService.class);

	/**
	 * Method that constructs a Borrowed object and saves it in the database.
	 * 
	 * @param borrower      the user who borrows the media.
	 * @param mediaToBorrow the media which gets borrowed by the user
	 * @return true if borrowing was successfull, else false.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public boolean borrowMedia(final User borrower, final Media mediaToBorrow) {
		if (mediaToBorrow.getTotalAvail() <= mediaToBorrow.getCurBorrowed() || borrower == null) {
			return false;
		} else {
			mediaToBorrow.setCurBorrowed(mediaToBorrow.getCurBorrowed() + 1);
			mediaRepository.save(mediaToBorrow);
			Borrowed borrow = new Borrowed(borrower, mediaToBorrow, new Date());
			borrowedRepository.save(borrow);
			return true;
		}
	}

	/**
	 * Method that constructs a Borrowed object and saves it in the database, uses
	 * the authenticated user for Borrows user.
	 * 
	 * @param media the media to borro
	 * @return true if borrowing was successfull, else false.
	 */
	public boolean borrowMediaForAuthenticatedUser(final Media media) {
		User borrower = userService.loadCurrentUser();
		return borrowMedia(borrower, media);
	}

	/**
	 * Method that unborrows a media (remove the Borrowed object from the database).
	 * 
	 * @param borrowed the Borrowed object that should be removed from the database.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void unBorrowMedia(final Borrowed borrowed) {
		if (borrowed != null) {
			borrowedRepository.delete(borrowed);
			borrowed.getMedia().setCurBorrowed(borrowed.getMedia().getCurBorrowed() - 1);
			mediaRepository.save(borrowed.getMedia());
			Collection<Reserved> reservations = reservedRepository.findByMedia(borrowed.getMedia());

			for (Reserved current : reservations) {
				unreserveMedia(current);
			}
		}

	}

	/**
	 * Method that unborrows a Media (remove the Borrowed object from the database).
	 * 
	 * @param borrower        user to unborrow the media for.
	 * @param mediaToUnBorrow media that should be unborrowed.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void unBorrowMedia(final User borrower, final Media mediaToUnBorrow) {
		unBorrowMedia(borrowedRepository.findFirstByUserAndMedia(borrower, mediaToUnBorrow));
	}

	/**
	 * Method that unborrows a Media (remove the Borrowed object from the database)
	 * for the currently authenticated user.
	 * 
	 * @param mediaToUnBorrow the media which should be unborrowed.
	 */
	public void unBorrowMediaForAuthenticatedUser(final Media mediaToUnBorrow) {
		User user = userService.loadCurrentUser();
		unBorrowMedia(user, mediaToUnBorrow);
	}

	/**
	 * Method that retrieves all Borrowed objects from the database.
	 * 
	 * @return a Collection of all stored Borrowed objects.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Borrowed> getAllBorrows() {
		return borrowedRepository.findAll();
	}

	/**
	 * Method that retrieves all Borrowed objects related to a certain user.
	 * 
	 * @param user the User to get the Borrowed objects for.
	 * @return a Collection of all stored Borrowed objects for the given user.
	 */
	public Collection<Borrowed> getAllBorrowsByUser(final User user) {
		return borrowedRepository.findByUser(user);
	}

	public Collection<Borrowed> getAllBorrowsByUsername(final String username){
		User u = userService.loadUser(username);

		if(u == null) return null;

		return borrowedRepository.findByUser(u);
	}

	/**
	 * Method that retrieves all Borrowed object related to the currently
	 * authenticated user.
	 * 
	 * @return a Collection of all stored Borrowed objects for the given user.
	 */
	public Collection<Borrowed> getAllBorrowsByAuthenticatedUser() {
		return getAllBorrowsByUser(userService.loadCurrentUser());
	}

	/**
	 * Method that retrieves all Borrowed objects related to a certian media.
	 * 
	 * @param media the Media to get the Borrowed objects for.
	 * @return a Collection of all stored Borrowed objects for the given media.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Borrowed> getAllBorrowsByMedia(final Media media) {
		return borrowedRepository.findByMedia(media);
	}

	/**
	 * Method that retrieves one particular Borrowed object.
	 * 
	 * @param borrower the User that borrowed something
	 * @param media    the Media that was borrowed
	 * @return the Borrowed object representing the borrowing.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Borrowed loadBorrowed(final User borrower, final Media media) {
		return borrowedRepository.findFirstByUserAndMedia(borrower, media);
	}

	/**
	 * Method that retrieves one particular borrow for the currently authenticated
	 * user.
	 * 
	 * @param media the Media that was borrowed.
	 * @return the Borrowed object representing the borrowing.
	 */
	public Borrowed loadBorrowedForAuthenticatedUser(final Media media) {
		User borrower = userService.loadCurrentUser();
		return loadBorrowed(borrower, media);
	}

	/**
	 * Method that reserves a Media for a User
	 * 
	 * @param reserver       the User who wants to reserve a media.
	 * @param mediaToReserve the Media which should be reserved.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void reserveMedia(final User reserver, final Media mediaToReserve) {
		Reserved res = new Reserved(reserver, mediaToReserve, new Date());
		reservedRepository.save(res);
	}

	/**
	 * Method that reserves a Media for the currently authenticaed User.
	 * 
	 * @param mediaToReserve the Media which should be reserved.
	 */
	public void reserveMediaForAuthenticatedUser(final Media mediaToReserve) {
		reserveMedia(userService.loadCurrentUser(), mediaToReserve);
	}

	/**
	 * Removes a reservation of the media for the authenticated user
	 *
	 * @param media the media
	 */
	public void removeReservationForAuthenticatedUser(final Media media){
		User user = userService.loadCurrentUser();

		Reserved r = reservedRepository.findFirstByUserAndMedia(user, media);

		if(r != null){
			reservedRepository.delete(r);
		}
	}

	/**
	 * Returns whether the authenticated user has reserved this media or not
	 *
	 * @param media the media to search for
	 * @return true, if he has reserved it, false otherwise
	 */
	public boolean isReservedForAuthenticatedUser(final Media media){
		User user = userService.loadCurrentUser();

		return reservedRepository.findFirstByUserAndMedia(user, media) != null;
	}

	/**
	 * Method that unreserves a Media for a User. Automatically sends an email to
	 * the user that reserved the Media.
	 * 
	 * @param reserved the reserved Object containing the user and the media.
	 */
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

	/**
	 * Method that retrieves all Reserved objects from the database.
	 * 
	 * @return a Collection of the Reserved objects.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Reserved> getAllReserved() {
		return reservedRepository.findAll();
	}

	/**
	 * Method that retrieves all Reserved objects for a certain User.
	 * 
	 * @param user the user to get the Reserved objects for.
	 * @return a Collection of the Reserved objects for the given user.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Reserved> getAllReservedByUser(final User user) {
		return reservedRepository.findByUser(user);
	}

	/**
	 * Method that retrieves all Reserved objects of the currently authenticated
	 * User.
	 * 
	 * @return a Collection of the Reserved objects for the given user.
	 */
	public Collection<Reserved> getAllReservedByAuthenticatedUser() {
		return getAllReservedByUser(userService.loadCurrentUser());
	}

	/**
	 * Method that retrieves all Reserved objects for a certain Media.
	 * 
	 * @param media the Media to get the Reserved obects for.
	 * @return a Collection of the Reserved objects for the given media.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Collection<Reserved> getAllReservedByMedia(final Media media) {
		return reservedRepository.findByMedia(media);
	}

	/**
	 * Returns the amount of reservations made for a certain media
	 *
	 * @param media the media to search for
	 * @return the count of reservations made
	 */
	public int getReservationCountForMedia(final Media media) { return reservedRepository.getReservationCountForMedia(media.getMediaID()); }

	/**
	 * Method that retrieves one particular Reserved object by user and media.
	 * 
	 * @param user  the user to get the Reserved object for.
	 * @param media the media to get the Reserved object for.
	 * @return the Reserved object related to the given user and media.
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Reserved loadReserved(final User user, final Media media) {
		return reservedRepository.findFirstByUserAndMedia(user, media);
	}

	/**
	 * Method that checks for exceeded borrow times. Automatically returns Medias
	 * and unreserves Reservations. Gets called by the spring scheduler
	 * periodically.
	 */
	@Scheduled(fixedDelay = schedulingDelay, initialDelay = 5000)
	public void checkBorrowTimeout() {
		Map<MediaType, Integer> allowedBorrowTimes = getMediaBorrowTimesAsMap();
		Collection<Borrowed> currentlyBorrowed = borrowedRepository.findAll();
		Date currentDate = new Date();
		for (Borrowed current : currentlyBorrowed) {
			long diff = currentDate.getTime() - current.getBorrowDate().getTime();
			long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			if (diffInDays > allowedBorrowTimes.get(current.getMedia().getMediaType())) {
				logger.info("Automatic return of media " + current.getMedia().getMediaID()
						+ " due to time limitations. Sent email to: " + current.getUser().getEmail());

				String head = "Automatic return of " + current.getMedia().getTitle();
				String body = "Dear customer,\n the media " + current.getMedia().getTitle()
						+ " has been returned automatically due the rental period expiring.\n\nYours sincerely,\nThe Library Team";
				mailService.sendMail(current.getUser().getEmail(), head, body);
				unBorrowMedia(current);
			}
		}
	}

	/**
	 * Method that gets the maximum allowed borrow times for all media types.
	 * 
	 * @return a Map with MediaTypes as key and the associated allowed borrow time
	 *         as value.
	 */
	private Map<MediaType, Integer> getMediaBorrowTimesAsMap() {
		Map<MediaType, Integer> allowedBorrowTimes = new HashMap<>();
		Collection<MediaBorrowTime> borrowTimes = mediaBorrowTimeRepository.findAll();
		for (MediaBorrowTime current : borrowTimes) {
			allowedBorrowTimes.put(current.getMediaType(), current.getAllowedBorrowTime());
		}
		return allowedBorrowTimes;
	}

	/**
	 * Method that is only used to initialized the bean after project startup. Makes
	 * the scheduler work from the start of the project.
	 */
	@Override
	public void run(final String... args) throws Exception {
		// used for initial loading of the component so scheduled task starts
		logger.info("BorrowService Component loaded at startup");
	}
}
