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

	public void doCreateAudioBook() throws MediaService.TotalAvailabilitySetTooLowException {

		Media media = this.mediaService.createAudioBook(title, publishingDate, language, totalAvail,
				MediaType.AUDIOBOOK, speaker, length, author, ISBN);
		undoRedoService.addAction(undoRedoService.createAction(media, UndoRedoService.ActionType.SAVE_MEDIA));
		// this.doReloadMedia();
	}

	public void doCreateBook() throws MediaService.TotalAvailabilitySetTooLowException {

		Media media = this.mediaService.createBook(title, publishingDate, language, totalAvail, MediaType.BOOK, author,
				ISBN);
		undoRedoService.addAction(undoRedoService.createAction(media, UndoRedoService.ActionType.SAVE_MEDIA));
		// this.doReloadMedia();
	}

	public void doCreateMagazine() throws MediaService.TotalAvailabilitySetTooLowException {

		Media media = this.mediaService.createMagazine(title, publishingDate, language, totalAvail, MediaType.MAGAZINE,
				series);
		undoRedoService.addAction(undoRedoService.createAction(media, UndoRedoService.ActionType.SAVE_MEDIA));
		// this.doReloadMedia();
	}

	public void doCreateVideo() throws MediaService.TotalAvailabilitySetTooLowException {

		Media media = this.mediaService.createVideo(title, publishingDate, language, totalAvail, MediaType.VIDEO,
				length);
		undoRedoService.addAction(undoRedoService.createAction(media, UndoRedoService.ActionType.SAVE_MEDIA));
		// this.doReloadMedia();
	}

	public void doCreateMedia() throws MediaService.TotalAvailabilitySetTooLowException {
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
	}

	public void setSelectedMediaTypes(final List<String> selectedMediaTypes) {
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(final MediaService mediaService) {
		this.mediaService = mediaService;
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

}
