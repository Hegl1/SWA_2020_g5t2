package at.qe.skeleton.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.data.domain.Persistable;

@Entity
public class Bookmarks implements Serializable, Persistable<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long bookmarkID;

	@ManyToOne
	private User user;

	@ManyToOne
	private Media Media;

	public Long getBookmarkID() {
		return bookmarkID;
	}

	public void setBookmarkID(final Long bookmarkID) {
		this.bookmarkID = bookmarkID;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public Media getMedia() {
		return Media;
	}

	public void setMedia(final Media media) {
		Media = media;
	}

	@Override
	public Long getId() {
		return bookmarkID;
	}

	@Override
	public boolean isNew() {
		return bookmarkID == null;
	}

}
