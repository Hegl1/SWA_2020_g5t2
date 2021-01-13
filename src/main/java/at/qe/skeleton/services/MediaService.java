package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.BookmarkRepository;
import at.qe.skeleton.repositories.MediaRepository;
import at.qe.skeleton.ui.controllers.FMSpamController;
import at.qe.skeleton.ui.controllers.ReservedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for accessing and manipulating media data.
 */
@Component
@Scope("application")
public class MediaService {

	@Autowired
	MediaRepository mediaRepository;

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

	@Autowired
	ReservedController reservedController;

	@Autowired
	FMSpamController fms;

	/**
	 * Return collection of Media of desired type.
	 */
	public Collection<Media> getAllMedia() {
		return this.mediaRepository.findAll();
	}

	/**
	 * Loads a single Media of desired type by its ID.
	 */
	public Media loadMedia(final Long mediaId) {
		return this.mediaRepository.findFirstByMediaID(mediaId);
	}

	/**
	 * Save Media in repository
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Media saveMedia(final Media media) {
		return this.mediaRepository.save(media);
	}

	/**
	 * Create Media of desired type.
	 */

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Media createAudioBook(final String title, final int publishingDate, final String language,
			final int totalAvail, final MediaType mediaType, final String speaker, final int length,
			final String author, final String ISBN) {

		Media newAudioBook = new AudioBook(title, publishingDate, language, totalAvail, mediaType, speaker, length,
				author, ISBN);
		this.saveMedia(newAudioBook);
		return newAudioBook;
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Media createBook(final String title, final int publishingDate, final String language, final int totalAvail,
			final MediaType mediaType, final String author, final String ISBN) {

		Media newBook = new Book(title, publishingDate, language, totalAvail, mediaType, author, ISBN);
		this.saveMedia(newBook);
		return newBook;
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Media createMagazine(final String title, final int publishingDate, final String language,
			final int totalAvail, final MediaType mediaType, final String series) {

		Media newMagazine = new Magazine(title, publishingDate, language, totalAvail, mediaType, series);
		this.saveMedia(newMagazine);
		return newMagazine;
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Media createVideo(final String title, final int publishingDate, final String language, final int totalAvail,
			final MediaType mediaType, final int length) {

		Media newVideo = new Video(title, publishingDate, language, totalAvail, mediaType, length);
		this.saveMedia(newVideo);
		return newVideo;
	}

	/**
	 * Filter Media by property
	 */

	public Collection<Media> filterMediaByTitle(final Collection<Media> filteredMedia, final String title) {
		return filteredMedia.stream().filter(x -> x.getTitle().toLowerCase().contains(title))
				.collect(Collectors.toCollection(ArrayList::new));
	}

    public Collection<Media> filterMediaByAvailability(Collection<Media> filteredMedia, boolean isAvailable) {
        return filteredMedia.stream().filter(x ->
                isAvailable ?
                        x.getAvailable() :
                        !x.getAvailable()).collect(Collectors.toCollection(ArrayList::new));
    }

	public Collection<Media> filterMediaByLanguage(final Collection<Media> filteredMedia, final String language) {
		return filteredMedia.stream().filter(x -> x.getLanguage().equalsIgnoreCase(language))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public Collection<Media> filterMediaByType(final Collection<Media> filteredMedia, final MediaType mediaType) {
		return filteredMedia.stream().filter(x -> x.getMediaType().equals(mediaType))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public Collection<Media> filterMediaByTitle(final String title) {
		return filterMediaByTitle(this.getAllMedia(), title);
	}

	public Collection<Media> filterMediaByAvailability(final boolean isAvailable) {
		return filterMediaByAvailability(this.getAllMedia(), isAvailable);
	}

	public Collection<Media> filterMediaByLanguage(final String language) {
		return filterMediaByLanguage(this.getAllMedia(), language);
	}

	public Collection<Media> filterMediaByType(final MediaType mediaType) {
		return filterMediaByType(this.getAllMedia(), mediaType);
	}

	public Media loadMediaByLanguageTypeYearTitle(final Media media) {
		return mediaRepository.findFirstByTitleAndPublishingYearAndLanguageAndMediaType(media.getTitle(),
				media.getPublishingYear(), media.getLanguage(), media.getMediaType());
	}

	/**
	 * Getting all Languages
	 */
	public Collection<String> getAllLanguages() {
		return this.mediaRepository.getAllLanguages();
	}

//	/**
//	 * Delete Media from repository
//	 */
//	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
//	public void deleteMedia(final Media media) {
//		this.mediaRepository.delete(media);
//		FacesContext context = FacesContext.getCurrentInstance();
//		context.addMessage("asGrowl",
//				new FacesMessage(FacesMessage.SEVERITY_INFO, "Media was deleted - in Service", ""));
//	}

	/**
	 * Delete Media from repository
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void deleteMedia(final Media media) {

		UndoRedoService.ActionItem deleteAction = undoRedoService.createAction(media,
				UndoRedoService.ActionType.DELETE_MEDIA);




		{
			System.out.println("\nnobody is borrowing it");

			// Step 2: Get a list of users that bookmarked this media eventually
			Collection<Bookmark> a2 = bookmarkService.getBookmarkByMedia(media);
			List<User> a2_s = new ArrayList<User>();
			for (Bookmark b : a2) {
				if (b.getMedia().getMediaID() == media.getMediaID()) {
					a2_s.add(b.getUser());
				}
			}

			// Step 3: If the media is not borrowed and only bookmarked: Delete the media
			// and bookmark entries and notify users
			if (a2_s.size() > 0) {

				System.out.println("\nThis media is still bookmarked by some people: ");
				for (User u : a2_s) {
					bookmarkService.deleteBookmark(bookmarkRepository.findFirstByUserAndMedia(u, media));
				}
				for (User u : a2_s) {
					System.out.println("  by: " + u.getUsername());
//					context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_WARN,
//							"This media is still bookmarked by: " + u.getUsername(), ""));
					fms.warn("This media is still bookmarked by: " + u.getUsername());
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
					}
				}

				undoRedoService.addAction(deleteAction);
				deleteMedia(media);
//				this.media = null;

				System.out.println("\nMedia was deleted finally.");
				fms.info("Please reload the page.");
//				context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Please reload the page.", ""));

			} else {
				System.out.println("\nnobody is bookmarking it");

				// Step 4: Delete the media only if it has not been borrowed by somebody
				undoRedoService.addAction(deleteAction);
				deleteMedia(media);
//				this.media = null;
				fms.info("Please reload the page.");
//				context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Please reload the page.", ""));
			}
		}





//		this.mediaRepository.delete(media);
//		FacesContext context = FacesContext.getCurrentInstance();
//		context.addMessage("asGrowl",
//				new FacesMessage(FacesMessage.SEVERITY_INFO, "Media was deleted - in Service", ""));
	}

}

