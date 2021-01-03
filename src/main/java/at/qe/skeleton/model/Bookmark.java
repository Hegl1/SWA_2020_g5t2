package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;

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
