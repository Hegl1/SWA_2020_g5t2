package at.qe.skeleton.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Persistable;

@Entity
public class Borrowed implements Serializable, Persistable<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "borrow_sequence", initialValue = 10)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "borrow_sequence")
	private Long borrowID;

	@ManyToOne
	private User user;

	@ManyToOne
	private Media media;

	@Temporal(TemporalType.DATE)
	private Date borrowDate;

	public Borrowed() {

	}

	public Borrowed(final User user, final Media media, final Date date) {
		this();
		this.user = user;
		this.media = media;
		this.borrowDate = date;
	}

	public Long getBorrowID() {
		return this.borrowID;
	}

	public void setBorrowID(final Long borrowID) {
		this.borrowID = borrowID;
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

	public Date getBorrowDate() {
		return this.borrowDate;
	}

	public void setBorrowDate(final Date borrowDate) {
		this.borrowDate = borrowDate;
	}

	@Override
	public Long getId() {
		return this.borrowID;
	}

	@Override
	public boolean isNew() {
		return this.borrowID == null;
	}
}
