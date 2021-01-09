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

/**
 * Entity that represents a reservation.
 * 
 * @author Marcel Huber
 * @version 1.0
 */
@Entity
public class Reserved implements Serializable, Persistable<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "borrow_sequence", initialValue = 10)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "borrow_sequence")
	private Long reservedID;

	@ManyToOne
	private User user;

	@ManyToOne
	private Media media;

	@Temporal(TemporalType.TIMESTAMP)
	private Date reserveTime;

	/**
	 * Default constructor
	 */
	public Reserved() {
		this.reserveTime = new Date();
	}

	/**
	 * Constructor that sets all the fields.
	 * 
	 * @param user      the user that reserves the media
	 * @param media     the media to be reserved.
	 * @param timestamp time when the media was reserved.
	 */
	public Reserved(final User user, final Media media, final Date timestamp) {
		this();
		this.user = user;
		this.media = media;
		this.reserveTime = timestamp;
	}

	public Long getReservedID() {
		return this.reservedID;
	}

	public void setReservedID(final Long reservedID) {
		this.reservedID = reservedID;
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

	public Date getReserveTime() {
		return this.reserveTime;
	}

	public void setReserveTime(final Date reserveTime) {
		this.reserveTime = reserveTime;
	}

	@Override
	public Long getId() {
		return this.reservedID;
	}

	@Override
	public boolean isNew() {
		return this.reservedID == null;
	}

}
