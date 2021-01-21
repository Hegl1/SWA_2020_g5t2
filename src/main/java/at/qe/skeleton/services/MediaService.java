package at.qe.skeleton.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.AudioBook;
import at.qe.skeleton.model.Book;
import at.qe.skeleton.model.Magazine;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.model.Video;
import at.qe.skeleton.repositories.MediaRepository;
import at.qe.skeleton.utils.UnallowedInputException;

/**
 * Service for accessing and manipulating media data.
 */
@Component
@Scope("application")
public class MediaService {

	@Autowired
	private MediaRepository mediaRepository;

	/**
	 * Returns collection of all media
	 *
	 * @return collection of all media
	 */
	public Collection<Media> getAllMedia() {
		return this.mediaRepository.findAll();
	}

	/**
	 * Loads a single media of desired type by its ID.
	 */
	public Media loadMedia(final Long mediaId) {
		return this.mediaRepository.findFirstByMediaID(mediaId);
	}

	/**
	 * Saves media in media repository
	 *
	 * @return saved media
	 * @throws UnallowedInputException
	 */
	public Media saveMedia(final Media media) throws TotalAvailabilitySetTooLowException, UnallowedInputException {

		if (media.getTotalAvail() < media.getCurBorrowed()) {
			throw new TotalAvailabilitySetTooLowException(
					"You cannot set less available items than are currently borrowed by persons");
		}

		if (media.getLanguage().length() < 1 || media.getTitle().length() < 1 || media.getTotalAvail() < 1) {
			throw new UnallowedInputException("No empty fields allowed");
		}

		switch (media.getMediaType()) {
		case MAGAZINE: {
			Magazine tmpMedia = (Magazine) media;
			if (tmpMedia.getSeries().length() < 1) {
				throw new UnallowedInputException("No empty fields allowed");
			}

		}
			break;

		case AUDIOBOOK: {
			AudioBook tmpMedia = (AudioBook) media;
			if (tmpMedia.getISBN().length() < 1 || tmpMedia.getAuthor().length() < 1 || tmpMedia.getLength() < 1
					|| tmpMedia.getSpeaker().length() < 1) {
				throw new UnallowedInputException("No empty fields allowed");
			}
		}
			break;

		case BOOK: {
			Book tmpMedia = (Book) media;
			if (tmpMedia.getAuthor().length() < 1 || tmpMedia.getISBN().length() < 1) {
				throw new UnallowedInputException("No empty fields allowed");
			}
		}
			break;

		case VIDEO: {
			Video tmpMedia = (Video) media;
			if (tmpMedia.getLength() < 1) {
				throw new UnallowedInputException("No empty fields allowed");
			}
		}
			break;
		}

		return this.mediaRepository.save(media);

	}

	public static class TotalAvailabilitySetTooLowException extends Exception {
		private static final long serialVersionUID = 1L;

		public TotalAvailabilitySetTooLowException(final String message) {
			super(message);
		}
	}

	/**
	 * Creates a new audiobook and saves it in the media repository.
	 *
	 * @param title          the title of the new audiobook
	 * @param publishingYear the year in which the audiobook was published
	 * @param language       the language in which the audiobook was published
	 * @param totalAvail     the number of copies, that can be borrowed
	 * @param speaker        the speaker of the audiobook
	 * @param length         the length of the audiobook in seconds
	 * @param author         the author of the corresponding book of the audiobook
	 * @param ISBN           the ISBN of the audiobook
	 *
	 * @return the newly created audiobook
	 * @throws UnallowedInputException
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Media createAudioBook(final String title, final int publishingYear, final String language,
			final int totalAvail, final String speaker, final int length, final String author, final String ISBN)
			throws TotalAvailabilitySetTooLowException, UnallowedInputException {

		if (speaker.length() < 1 || length < 1 || author.length() < 1 || ISBN.length() < 1) {
			throw new UnallowedInputException("No empty fields allowed");
		}
		Media newAudioBook = new AudioBook(title, publishingYear, language, totalAvail, speaker, length, author, ISBN);
		this.saveMedia(newAudioBook);
		return newAudioBook;
	}

	/**
	 * Creates a new book and saves it in the media repository.
	 *
	 * @param title          the title of the new book
	 * @param publishingYear the year in which the book was published
	 * @param language       the language in which the audiobook was published
	 * @param totalAvail     the number of copies, that can be borrowed
	 * @param author         the author of the book
	 * @param ISBN           the ISBN of the book
	 *
	 * @return the newly created book
	 * @throws UnallowedInputException
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Media createBook(final String title, final int publishingYear, final String language, final int totalAvail,
			final String author, final String ISBN)
			throws TotalAvailabilitySetTooLowException, UnallowedInputException {

		if (author.length() < 1 || ISBN.length() < 1) {
			throw new UnallowedInputException("No empty fields allowed");
		}

		Media newBook = new Book(title, publishingYear, language, totalAvail, author, ISBN);
		this.saveMedia(newBook);
		return newBook;
	}

	/**
	 * Creates a new magazine and saves it in the media repository.
	 *
	 * @param title          the title of the new magazine
	 * @param publishingYear the year in which the magazine was published
	 * @param language       the language in which the magazine was published
	 * @param totalAvail     the number of copies, that can be borrowed
	 * @param series         the series of which the magazine is part of
	 *
	 * @return the newly created magazine
	 * @throws UnallowedInputException
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Media createMagazine(final String title, final int publishingYear, final String language,
			final int totalAvail, final String series)
			throws TotalAvailabilitySetTooLowException, UnallowedInputException {

		if (series.length() < 1) {
			throw new UnallowedInputException("No empty fields allowed");
		}
		Media newMagazine = new Magazine(title, publishingYear, language, totalAvail, series);
		this.saveMedia(newMagazine);
		return newMagazine;
	}

	/**
	 * Creates a new video and saves it in the media repository.
	 *
	 * @param title          the title of the new video
	 * @param publishingYear the year in which the video was published
	 * @param language       the language in which the video was published
	 * @param totalAvail     the number of copies, that can be borrowed
	 * @param length         the length of the video in seconds
	 *
	 * @return the newly created video
	 * @throws UnallowedInputException
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public Media createVideo(final String title, final int publishingYear, final String language, final int totalAvail,
			final int length) throws TotalAvailabilitySetTooLowException, UnallowedInputException {

		if (length < 1) {
			throw new TotalAvailabilitySetTooLowException("No empty fields allowed");
		}
		Media newVideo = new Video(title, publishingYear, language, totalAvail, length);
		this.saveMedia(newVideo);
		return newVideo;
	}

	/**
	 * Filters a selected collection of media by title
	 *
	 * @param filteredMedia the collection which gets filtered
	 * @param title         the title the collection is filtered by
	 * @return collection of filtered media
	 */
	public Collection<Media> filterMediaByTitle(final Collection<Media> filteredMedia, final String title) {
		return filteredMedia.stream().filter(x -> x.getTitle().toLowerCase().contains(title.toLowerCase()))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Filters a selected collection of media by availability
	 *
	 * @param filteredMedia the collection which gets filtered
	 * @param isAvailable   whether the media must be available or not available
	 * @return collection of filtered media
	 */
	public Collection<Media> filterMediaByAvailability(final Collection<Media> filteredMedia,
			final boolean isAvailable) {
		return filteredMedia.stream().filter(x -> isAvailable == x.getAvailable())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Filters a selected collection of media by language
	 *
	 * @param filteredMedia the collection which gets filtered
	 * @param language      the language the collection is filtered by
	 * @return filtered collection of media
	 */
	public Collection<Media> filterMediaByLanguage(final Collection<Media> filteredMedia, final String language) {
		return filteredMedia.stream().filter(x -> x.getLanguage().equalsIgnoreCase(language))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Filters a selected collection of media by media type
	 *
	 * @param filteredMedia the collection which gets filtered
	 * @param mediaType     the media type the collection is filtered by
	 * @return filtered collection of media
	 */
	public Collection<Media> filterMediaByType(final Collection<Media> filteredMedia, final MediaType mediaType) {
		return filteredMedia.stream().filter(x -> x.getMediaType().equals(mediaType))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Loads media by a super key if the primary key has changed due to a resave
	 * action
	 *
	 * @param media the media that should get resaved after it was deleted
	 * @return the new resaved media with new mediaID
	 */
	public Media loadMediaByLanguageTypeYearTitle(final Media media) {
		return mediaRepository.findFirstByTitleAndPublishingYearAndLanguageAndMediaType(media.getTitle(),
				media.getPublishingYear(), media.getLanguage(), media.getMediaType());
	}

	/**
	 * Gets all languages
	 *
	 * @return collection of all occurring languages in the collection of medias
	 */
	public Collection<String> getAllLanguages() {
		return this.mediaRepository.getAllLanguages();
	}

	/**
	 * Deletes media from the media repository
	 *
	 * @param media the media to delete
	 */
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
	public void deleteMedia(final Media media) {
		this.mediaRepository.delete(media);
	}

	/**
	 * Refreshes the media with data from the database
	 *
	 * @param media the media to refresh
	 */
	public void refreshMedia(final Media media) {
		if (media.isNew()) {
			return;
		}

		Media refresh = this.mediaRepository.findFirstByMediaID(media.getMediaID());

		media.setTitle(refresh.getTitle());
		media.setPublishingYear(refresh.getPublishingYear());
		media.setLanguage(refresh.getLanguage());
		media.setTotalAvail(refresh.getTotalAvail());
		media.setCurBorrowed(refresh.getCurBorrowed());

		switch (media.getMediaType()) {
		case BOOK:
			Book book = (Book) media;
			Book refreshBook = (Book) refresh;

			book.setAuthor(refreshBook.getAuthor());
			book.setISBN(refreshBook.getISBN());
			break;
		case AUDIOBOOK:
			AudioBook audiobook = (AudioBook) media;
			AudioBook refreshAudioBook = (AudioBook) refresh;

			audiobook.setSpeaker(refreshAudioBook.getSpeaker());
			audiobook.setLength(refreshAudioBook.getLength());
			audiobook.setAuthor(refreshAudioBook.getAuthor());
			audiobook.setISBN(refreshAudioBook.getISBN());
			break;
		case MAGAZINE:
			Magazine magazine = (Magazine) media;
			Magazine refreshMagazine = (Magazine) refresh;

			magazine.setSeries(refreshMagazine.getSeries());
			break;
		case VIDEO:
			Video video = (Video) media;
			Video refreshVideo = (Video) refresh;

			video.setLength(refreshVideo.getLength());
			break;
		}
	}
}
