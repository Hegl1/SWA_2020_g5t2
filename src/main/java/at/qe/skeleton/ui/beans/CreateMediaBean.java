package at.qe.skeleton.ui.beans;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.services.MediaService;
import at.qe.skeleton.services.UndoRedoService;

@Component
@Scope("view")
public class CreateMediaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private UndoRedoService undoRedoService;

	private String title;
	private int publishingDate;
	private String language;
	private int totalAvail;
	private String mediaType;

	// For book
	private String author;
	private String ISBN;

	// For Audiobook
	private String speaker;

	// For Magazin
	private String series;

	// For Video
	private int length;

	/**
	 * Create different Medias.
	 */

	private void doCreateAudioBook() {
		Media media = this.mediaService.createAudioBook(title, publishingDate, language, totalAvail,
				 speaker, length, author, ISBN);
		undoRedoService.addAction(undoRedoService.createAction(media, UndoRedoService.ActionType.SAVE_MEDIA));
		// this.doReloadMedia();
	}

	private void doCreateBook() {
		Media media = this.mediaService.createBook(title, publishingDate, language, totalAvail, author, ISBN);
		undoRedoService.addAction(undoRedoService.createAction(media, UndoRedoService.ActionType.SAVE_MEDIA));
		// this.doReloadMedia();
	}

	private void doCreateMagazine() {
		Media media = this.mediaService.createMagazine(title, publishingDate, language, totalAvail, series);
		undoRedoService.addAction(undoRedoService.createAction(media, UndoRedoService.ActionType.SAVE_MEDIA));
		// this.doReloadMedia();
	}

	private void doCreateVideo() {
		Media media = this.mediaService.createVideo(title, publishingDate, language, totalAvail, length);
		undoRedoService.addAction(undoRedoService.createAction(media, UndoRedoService.ActionType.SAVE_MEDIA));
		// this.doReloadMedia();
	}

	public void doCreateMedia() {
		switch (mediaType) {
		case "VIDEO":
			doCreateVideo();
			break;
		case "BOOK":
			doCreateBook();
			break;
		case "AUDIOBOOK":
			doCreateAudioBook();
			break;
		case "MAGAZINE":
			doCreateMagazine();
			break;
		}

		this.reset();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public int getPublishingDate() {
		return publishingDate;
	}

	public void setPublishingDate(final int publishingYear) {
		this.publishingDate = publishingYear;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public int getTotalAvail() {
		return totalAvail;
	}

	public void setTotalAvail(final int totalAvail) {
		this.totalAvail = totalAvail;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(final String mediaType) {
		this.mediaType = mediaType;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(final String iSBN) {
		ISBN = iSBN;
	}

	public String getSpeaker() {
		return speaker;
	}

	public void setSpeaker(final String speaker) {
		this.speaker = speaker;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(final String series) {
		this.series = series;
	}

	public int getLength() {
		return length;
	}

	public void setLength(final int length) {
		this.length = length;
	}

	public void reset() {
		this.title = null;
		this.publishingDate = 0;
		this.language = null;
		this.totalAvail = 0;
		this.mediaType = null;
		this.author = null;
		this.ISBN = null;
		this.speaker = null;
		this.series = null;
		this.length = 0;
	}
}
