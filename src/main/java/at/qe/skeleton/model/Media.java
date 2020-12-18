package at.qe.skeleton.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Persistable;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Media implements Persistable<Long>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long mediaID;
	private String title;
	@Temporal(TemporalType.DATE)
	private Date publishingDate;
	private String language;
	private int totalAvail;
	private int curBorrowed;
	@Enumerated(EnumType.STRING)
	private MediaType mediaType;

	public Media(final String title, final Date publishingDate, final String language, final int totalAvail, final MediaType mediaType) {
		this.title = title;
		this.publishingDate = publishingDate;
		this.language = language;
		this.totalAvail = totalAvail;
		this.curBorrowed = 0;
		this.mediaType = mediaType;
	}

	public Media() {

	}

	public long getMediaID() {
		return mediaID;
	}

	public void setMediaID(final long mediaID) {
		this.mediaID = mediaID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public Date getPublishingDate() {
		return publishingDate;
	}

	public void setPublishingDate(final Date publishingDate) {
		this.publishingDate = publishingDate;
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

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(final MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public int getCurBorrowed() {
		return curBorrowed;
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
