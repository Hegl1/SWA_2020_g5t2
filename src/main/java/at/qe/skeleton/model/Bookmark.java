package at.qe.skeleton.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.springframework.data.domain.Persistable;

/**
 * Entity that represents a bookmark.
 * 
 * @author Marcel Huber
 * @version 1.0
 */
@Entity
public class Bookmark implements Serializable, Persistable<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "bookmark_sequence", initialValue = 10)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookmark_sequence")
	private Long bookmarkID;

	@ManyToOne
	private User user;

	@ManyToOne
	private Media media;

	public Long getBookmarkID() {
		return this.bookmarkID;
	}

	public void setBookmarkID(final Long bookmarkID) {
		this.bookmarkID = bookmarkID;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public Media getMedia() {
		return this.media;
	}

	public void setMedia(final Media media) {
		this.media = media;
	}

	@Override
	public Long getId() {
		return this.bookmarkID;
	}

	@Override
	public boolean isNew() {
		return this.bookmarkID == null;
	}

}
