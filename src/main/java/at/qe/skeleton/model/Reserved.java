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
public class Reserved implements Serializable, Persistable<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long reservedID;

	@ManyToOne
	private User user;

	@ManyToOne
	private Media media;

	@Temporal(TemporalType.TIMESTAMP)
	private Date reserveTime;

	public Reserved() {
	}

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
