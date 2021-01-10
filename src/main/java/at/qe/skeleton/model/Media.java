package at.qe.skeleton.model;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a general Media. Only exists as in more concrete classes,
 * see {@link at.qe.skeleton.model.AudioBook},
 * {@link at.qe.skeleton.model.Magazine}, {@link at.qe.skeleton.model.Book},
 * {@link at.qe.skeleton.model.Video}
 * 
 * @author Marcel Huber
 * @version 1.0
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Media implements Persistable<Long>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "media_sequence", initialValue = 10)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_sequence")
	private Long mediaID;
	private String title;
	private int publishingYear;
	private String language;
	private int totalAvail;
	private int curBorrowed;
	@Enumerated(EnumType.STRING)
	private MediaType mediaType;

	/**
	 * Default constructor.
	 */
	public Media() {

	}

	/**
	 * Construcor that sets all the fileds of Media.
	 * 
	 * @param title          title of the media
	 * @param publishingYear publishing year of the media
	 * @param language       2 digit language code
	 * @param totalAvail     number of totally available copies
	 * @param mediaType      type of the media, see {link MediaType}
	 */
	public Media(final String title, final int publishingYear, final String language, final int totalAvail,
			final MediaType mediaType) {

		this.title = title;
		this.publishingYear = publishingYear;
		this.language = language;
		this.totalAvail = totalAvail;
		this.curBorrowed = 0;
		this.mediaType = mediaType;
	}

	public long getMediaID() {
		return this.mediaID;
	}

	public void setMediaID(final long mediaID) {
		this.mediaID = mediaID;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public int getPublishingYear() {
		return this.publishingYear;
	}

	public void setPublishingYear(final int publishingYear) {
		this.publishingYear = publishingYear;
	}

	public String getLanguage() {
		return this.language;
	}

	public String getLanguageHuman() {
		Locale l = new Locale(this.language);
		return l.getDisplayLanguage();
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public int getTotalAvail() {
		return this.totalAvail;
	}

	public void setTotalAvail(final int totalAvail) {
		this.totalAvail = totalAvail;
	}

	public MediaType getMediaType() {
		return this.mediaType;
	}

	public String getMediaTypeHuman() {
		return this.mediaType.toString().substring(0, 1).toUpperCase()
				+ this.mediaType.toString().substring(1).toLowerCase();
	}

	public void setMediaType(final MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public int getCurBorrowed() {
		return this.curBorrowed;
	}

	public void setCurBorrowed(final int curBorrowed) {
		this.curBorrowed = curBorrowed;
	}

	@Override
	public Long getId() {
		return this.mediaID;
	}

	@Override
	public boolean isNew() {
		return this.mediaID == null;
	}

}
