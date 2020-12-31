package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Media implements Persistable<Long>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="media_sequence", initialValue=10)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="media_sequence")
	private Long mediaID;
	private String title;
	@Temporal(TemporalType.DATE)
	private Date publishingDate;
	private String language;
	private int totalAvail;
	private int curBorrowed;
	@Enumerated(EnumType.STRING)
	private MediaType mediaType;

	public Media(final String title, final Date publishingDate, final String language,
				 final int totalAvail, final MediaType mediaType) {

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

	public Date getPublishingDate() {
		return this.publishingDate;
	}

	public void setPublishingDate(final Date publishingDate) {
		this.publishingDate = publishingDate;
	}

	public String getLanguage() {
		return this.language;
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
