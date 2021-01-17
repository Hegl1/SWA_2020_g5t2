package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Entity that relates media types with the corresponding maximum allowed
 * borrowment times.
 * 
 * @author Marcel Huber
 * @version 1.0
 *
 */
@Entity
public class MediaBorrowTime implements Serializable, Persistable<MediaType> {

	private static final long serialVersionUID = 1L;

	@Id
	@Enumerated(EnumType.STRING)
	private MediaType mediaType;
	private Integer allowedBorrowTime;

	public MediaType getMediaType() {
		return this.mediaType;
	}

	public void setMediaType(final MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public int getAllowedBorrowTime() {
		return this.allowedBorrowTime;
	}

	public void setAllowedBorrowTime(final int allowedBorrowTime) {
		if(allowedBorrowTime <= 0) return;

		this.allowedBorrowTime = allowedBorrowTime;
	}

	@Override
	public MediaType getId() {
		return this.mediaType;
	}

	@Override
	public boolean isNew() {
		return this.mediaType == null;
	}

}
