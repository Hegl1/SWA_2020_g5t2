package at.qe.skeleton.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Persistable;

@MappedSuperclass
public abstract class Media implements Persistable<Long>, Serializable {

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
