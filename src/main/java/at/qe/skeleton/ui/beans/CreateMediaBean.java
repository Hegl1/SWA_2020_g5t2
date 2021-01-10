package at.qe.skeleton.ui.beans;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.services.MediaService;

@Component
@Scope("view")
public class CreateMediaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private MediaService mediaService;

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

	public void doCreateAudioBook() {

		this.mediaService.createAudioBook(title, publishingDate, language, totalAvail, MediaType.AUDIOBOOK, speaker,
				length, author, ISBN);
		// this.doReloadMedia();
	}

	public void doCreateBook() {

		this.mediaService.createBook(title, publishingDate, language, totalAvail, MediaType.BOOK, author, ISBN);
		// this.doReloadMedia();
	}

	public void doCreateMagazine() {

		this.mediaService.createMagazine(title, publishingDate, language, totalAvail, MediaType.MAGAZINE, series);
		// this.doReloadMedia();
	}

	public void doCreateVideo() {

		this.mediaService.createVideo(title, publishingDate, language, totalAvail, MediaType.VIDEO, length);
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
	}

	public void setSelectedMediaTypes(List<String> selectedMediaTypes) {
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPublishingDate() {
		return publishingDate;
	}

	public void setPublishingDate(int publishingYear) {
		this.publishingDate = publishingYear;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getTotalAvail() {
		return totalAvail;
	}

	public void setTotalAvail(int totalAvail) {
		this.totalAvail = totalAvail;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getSpeaker() {
		return speaker;
	}

	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
