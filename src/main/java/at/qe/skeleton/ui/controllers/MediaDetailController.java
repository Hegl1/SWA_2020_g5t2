package at.qe.skeleton.ui.controllers;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.AudioBook;
import at.qe.skeleton.model.Book;
import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Magazine;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.Video;
import at.qe.skeleton.repositories.BookmarkRepository;
import at.qe.skeleton.services.BookmarkService;
import at.qe.skeleton.services.BorrowService;
import at.qe.skeleton.services.MailService;
import at.qe.skeleton.services.MediaService;
import at.qe.skeleton.services.UndoRedoService;

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
	BookmarkService bookmarkService;

	@Autowired
	MailService mailservice;

	@Autowired
	BookmarkRepository bookmarkRepository;

	@Autowired
	UndoRedoService undoRedoService;

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
		this.media = this.mediaService.saveMedia(this.media);
		undoRedoService
				.addAction(undoRedoService.createAction(beforeEditMedia, media, UndoRedoService.ActionType.EDIT_MEDIA));
	}

	/**
	 * Action to delete the currently displayed media.
	 */
	public void doDeleteMedia() {
		this.mediaService.deleteMedia(media);
		this.media = null;
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("asGrowl",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Media was deleted - in Controller", ""));
	}

	/**
	 * Intermediate function to close the loading window while safe deleting
	 */
	public void orderASafeDelete(final Media media) {

		// a loading message is shown, as the deleting and mail sending process can take
		// up to 20 seconds
		doSafeDeleteMedia(media);
		PrimeFaces.current().executeScript("PF('dataChangeDlg').hide()");
	}

	/**
	 * Safe delete - check if borrowings and bookmarks exist to a given media if
	 * borrowings exist: do not delete if bookmarks exist: delete them and notify
	 * with growl Messages and send email
	 */
	public void doSafeDeleteMedia(final Media media) {

		UndoRedoService.ActionItem deleteAction = undoRedoService.createAction(media,
				UndoRedoService.ActionType.DELETE_MEDIA);

		// Step 1: Check if the media is still borrowed
		FacesContext context = FacesContext.getCurrentInstance();
		Collection<Borrowed> a1 = borrowService.getAllBorrowsByMedia(media);
		if (a1.size() > 0) {
			System.out.println("\nThis media is still borrowed by x " + a1.size() + " people.");
			for (Borrowed a : a1) {
				System.out.println("  by: " + a.getUser());
				context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_WARN,
						"This media is still borrowed by : " + a.getUser().getUsername() + " and cannot be deleted.",
						""));
			}
		} else {
			System.out.println("\nnobody is borrowing it");
		}

		// Step 2: Get a list of users that bookmarked this media eventually
		Collection<Bookmark> a2 = bookmarkService.getAllBookmarks();
		List<User> a2_s = new ArrayList<User>();
		for (Bookmark b : a2) {
			if (b.getMedia().getMediaID() == media.getMediaID()) {
				a2_s.add(b.getUser());
			}
		}

		// Step 3: If the media is not borrowed and only bookmarked: Delete the media
		// and bookmark entries and notify users
		int MediaAlreadyDeleted;
		MediaAlreadyDeleted = 0;
		if (a2_s.size() > 0) {

			System.out.println("\nThis media is still bookmarked by some people: ");

			for (User u : a2_s) {
				System.out.println("  by: " + u.getUsername());
				context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_WARN,
						"This media is still bookmarked by: " + u.getUsername(), ""));
				bookmarkService.deleteBookmark(bookmarkRepository.findFirstByUserAndMedia(u, media));
				mailservice.sendMail(u.getEmail(), "> The Media of your Bookmark was deleted",
						"The following Media is not available anymore: Title: " + media.getTitle() + ", Type: "
								+ media.getMediaType() + ", Language: " + media.getLanguage() + ", Publishing Year: "
								+ media.getPublishingYear());
			}
			undoRedoService.addAction(deleteAction);
			this.mediaService.deleteMedia(media);
			this.media = null;

			System.out.println("\nMedia was deleted finally.");
			MediaAlreadyDeleted = 1;
			context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Please reload the page.", ""));

		} else {
			System.out.println("\nnobody is bookmarking it");
		}

		// Step 4: Delete the media only if it has not been borrowed by somebody and was
		// not deleted in step 3
		if ((!(a1.size() > 0) && (MediaAlreadyDeleted == 0))) {
			undoRedoService.addAction(deleteAction);
			this.mediaService.deleteMedia(media);
			this.media = null;
			context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Please reload the page.", ""));
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
