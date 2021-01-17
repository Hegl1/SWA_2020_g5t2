package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.BookmarkRepository;
import at.qe.skeleton.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the media detail view.
 */
@Component
@Scope("view")
public class MediaDetailController implements Serializable {
	// TODO: set private
	@Autowired
	MediaService mediaService;

	@Autowired
	BorrowService borrowService;

	@Autowired
	UndoRedoService undoRedoService;

	@Autowired
	BookmarkService bookmarkService;

	@Autowired
	MailService mailservice;

	@Autowired
	BookmarkRepository bookmarkRepository;

	@Autowired
	ReservedController reservedController;


	@Autowired
	FMSpamController fms;


	/**
	 * Attribute to cache the currently displayed media
	 */
	private Media media;

	/**
	 * Sets the currently displayed media and reloads it from the database.
	 */
	public void setMedia(final Media media) {
		this.media = media;
	}

	/**
	 * Returns the currently displayed media.
	 */
	public Media getMedia() {
		return media;
	}

	public Book getMediaBook() {
		if (media == null || media.getMediaType() != MediaType.BOOK) {
			return null;
		}

		return (Book) media;
	}

	public AudioBook getMediaAudioBook() {
		if (media == null || media.getMediaType() != MediaType.AUDIOBOOK) {
			return null;
		}

		return (AudioBook) media;
	}

	public Video getMediaVideo() {
		if (media == null || media.getMediaType() != MediaType.VIDEO) {
			return null;
		}

		return (Video) media;
	}

	public Magazine getMediaMagazine() {
		if (media == null || media.getMediaType() != MediaType.MAGAZINE) {
			return null;
		}

		return (Magazine) media;
	}

	/**
	 * Action to force a reload of the currently displayed media.
	 */
	public void doReloadMedia() {
		this.media = this.mediaService.loadMedia(this.media.getMediaID());
	}

	/**
	 * Action to save the currently displayed media.
	 */
	public void doSaveMedia() {
		Media beforeEditMedia = mediaService.loadMedia(media.getId());
		try {
			this.media = this.mediaService.saveMedia(this.media);
			undoRedoService.addAction(undoRedoService.createAction(beforeEditMedia, media, UndoRedoService.ActionType.EDIT_MEDIA));
		} catch (MediaService.TotalAvailabilitySetTooLowException e) {
			fms.warn(e.getMessage());
		}
	}










	/**
	 * Action to safely delete the currently displayed media.
	 */
	public void deleteMedia(final Media media) {

		UndoRedoService.ActionItem deleteAction = undoRedoService.createAction(media,
				UndoRedoService.ActionType.DELETE_MEDIA);

		// Step 1: Check if the media is still borrowed
		Collection<Borrowed> a1 = borrowService.getAllBorrowsByMedia(media);
		if (a1.size() > 0) {

			for (Borrowed a : a1) {

				fms.warn("This media is still borrowed by : " + a.getUser().getUsername() + " and cannot be deleted");
			}
		} else {

			doSafeDeleteMedia(media);
		}
		// optionally close the occuring loading message as the deleting and mail sending process can take a while
		// PrimeFaces.current().executeScript("PF('dataChangeDlg').hide()");
	}

	/**
	 * Safe delete - check if borrowings and bookmarks exist to a given media if
	 * borrowings exist: do not delete if bookmarks exist: delete them and notify
	 * with growl Messages and send email
	 * */

	public void doSafeDeleteMedia(final Media media) {

		UndoRedoService.ActionItem deleteAction = undoRedoService.createAction(media,
				UndoRedoService.ActionType.DELETE_MEDIA);

			{

			// Step 2: Get a list of users that bookmarked this media eventually
			Collection<Bookmark> a2 = bookmarkService.getBookmarkByMedia(media);
			List<User> a2_s = new ArrayList<>();
			for (Bookmark b : a2) {
				if (b.getMedia().getMediaID() == media.getMediaID()) {
					a2_s.add(b.getUser());
				}
			}

			// Step 3: If the media is not borrowed and only bookmarked: Delete the media
			// and bookmark entries and notify users
			if (a2_s.size() > 0) {


				for (User u : a2_s) {
					bookmarkService.deleteBookmark(bookmarkRepository.findFirstByUserAndMedia(u, media));
					fms.info("The Bookmark for this Media was deleted from user: "+u.getUsername());
				}
				for (User u : a2_s) {
					mailservice.sendMail(u.getEmail(), "> The Media of your Bookmark was deleted",
							"The following Media is not available anymore: Title: " + media.getTitle() + ", Type: "
									+ media.getMediaType() + ", Language: " + media.getLanguage() + ", Publishing Year: "
									+ media.getPublishingYear());
				}
				// last but not least: remove reservations
				if(reservedController.getReservationCountForMedia(media) > 0);
				for (User u : a2_s) {
					if(reservedController.isReservedForSpecialUser(media, u)){
						reservedController.doRemoveReservationForSpecificUser(media, u);
						fms.info("The Reservation for this Media was deleted from user: "+u.getUsername());
					}
				}

				undoRedoService.addAction(deleteAction);
				this.mediaService.deleteMedia(media);
				this.media = null;

				fms.info("Media was deleted.");
				fms.info("Please reload the page.");

			} else {

				// Step 4: Delete the media only if it has not been borrowed by somebody
				undoRedoService.addAction(deleteAction);
				this.mediaService.deleteMedia(media);
				this.media = null;
				fms.info("Please reload the page.");
			}
		}
	}

	/**
	 * Converts a length in seconds to a nicely readable string in the format:
	 * "[hours]h [minutes]m [seconds]s"
	 *
	 * @param length The length in seconds to convert
	 * @return the formatted string
	 */
	public String convertLength(final int length) {
		int seconds = length % 60;
		int minutes = (length / 60) % 60;
		int hours = (length / 60) / 60;

		return hours + "h " + minutes + "m " + seconds + "s";
	}

	/**
	 * Returns a list of all 2-letter language codes defined in ISO 639
	 *
	 * @return the list of 2-letter language codes
	 */
	public Collection<String> getAllLanguages() {
		return Arrays.stream(Locale.getISOLanguages()).map(String::toUpperCase).sorted((l1, l2) -> {
			Locale l = new Locale(l1);
			String l1_display = l.getDisplayLanguage();

			l = new Locale(l2);
			String l2_display = l.getDisplayLanguage();

			return l1_display.compareTo(l2_display);
		}).collect(Collectors.toList());
	}
}
