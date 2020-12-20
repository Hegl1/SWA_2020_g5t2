package at.qe.skeleton.model;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Video extends Media {

	private static final long serialVersionUID = 1L;

	private int length;

	public Video(final String title, final Date publishingDate, final String language, final int totalAvail,
				 final MediaType mediaType, final int length) {

		super(title, publishingDate, language, totalAvail, mediaType);
		this.length = length;
	}

	public Video() {

	}

	public int getLength() {
		return this.length;
	}

	public void setLength(final int length) {
		this.length = length;
	}

}
