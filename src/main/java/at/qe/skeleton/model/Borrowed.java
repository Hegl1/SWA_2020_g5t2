package at.qe.skeleton.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Persistable;

@Entity
public class Borrowed implements Serializable, Persistable<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long borrowID;

	@ManyToOne
	private User user;

	@ManyToOne
	private Media media;

	@Temporal(TemporalType.DATE)
	private Date borrowDate;

	public Long getBorrowID() {
		return borrowID;
	}

	public void setBorrowID(final Long borrowID) {
		this.borrowID = borrowID;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(final Media media) {
		this.media = media;
	}

	public Date getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(final Date borrowDate) {
		this.borrowDate = borrowDate;
	}

	@Override
	public Long getId() {
		return borrowID;
	}

	@Override
	public boolean isNew() {
		return borrowID == null;
	}
}
