package at.qe.skeleton.ui.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.skeleton.model.Media;
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

	private Media media;

	private List<String> selectedMediaTypes;

	private Long mediaID;
	private String title;
	private int publishingDate;
	private String language;
	private int totalAvail;
	private int curBorrowed;
	@Enumerated(EnumType.STRING)
	private MediaType mediaType;

	// For book
	private String author;
	private String ISBN;

	// For Audiobook
	private String speaker;

	// For Magazin
	private String series;

	// For Video
	private int length;

	/*
	 * @PostConstruct public void init() { this.media = new Media(); }
	 */

	@PreAuthorize("hasAuthority('ADMIN')")
	public void saveMedia() {

		//setMediaTypes();
		mediaService.saveMedia(media);

		FacesMessage asGrowl = new FacesMessage(FacesMessage.SEVERITY_INFO, "Changes saved!", "");
		FacesContext.getCurrentInstance().addMessage("asGrowl", asGrowl);
	}

	private void setMediaTypes() {
		Set<MediaType> mediaType = new HashSet<>();

		for (String selected : selectedMediaTypes) {
			switch (selected) {
			case "video":
				mediaType.add(MediaType.VIDEO);
				break;
			case "book":
				mediaType.add(MediaType.BOOK);
				break;
			case "audiobook":
				mediaType.add(MediaType.AUDIOBOOK);
				break;
			case "magazin":
				mediaType.add(MediaType.MAGAZINE);
				break;
			default:
				System.err.println(
						"[Warning] CreateMediaBean - setMediaTypes: Role \"" + selected + "\" not supported yet!"); // TODO:
																													// add
																													// logger
			}
		}
	}
	
    /**
     * Create different Medias.
     */

    public void doCreateAudioBook(final String title, final int publishingDate, final String language,
                                  final int totalAvail, final MediaType mediaType, final String speaker,
                                  final int length, final String author, final String ISBN) {

        this.mediaService.createAudioBook(title, publishingDate, language, totalAvail, mediaType, speaker, length, author, ISBN);
        // this.doReloadMedia();
    }

    public void doCreateBook(final String title, final int publishingDate, final String language, final int totalAvail,
                             final MediaType mediaType, final String author, final String ISBN) {

        this.mediaService.createBook(title, publishingDate, language, totalAvail, mediaType, author, ISBN);
        // this.doReloadMedia();
    }

    public void doCreateMagazine(final String title, final int publishingDate, final String language,
                                 final int totalAvail, final MediaType mediaType, final String series) {

        this.mediaService.createMagazine(title, publishingDate, language, totalAvail, mediaType, series);
        // this.doReloadMedia();
    }

    public void doCreateVideo(final String title, final int publishingDate, final String language,
                              final int totalAvail, final MediaType mediaType, final int length) {

        this.mediaService.createVideo(title, publishingDate, language, totalAvail, mediaType, length);
        // this.doReloadMedia();
    }


	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public List<String> getSelectedMediaTypes() {
		return selectedMediaTypes;
	}

	public void setSelectedMediaTypes(List<String> selectedMediaTypes) {
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public Long getMediaID() {
		return mediaID;
	}

	public void setMediaID(Long mediaID) {
		this.mediaID = mediaID;
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

	public int getCurBorrowed() {
		return curBorrowed;
	}

	public void setCurBorrowed(int curBorrowed) {
		this.curBorrowed = curBorrowed;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
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
