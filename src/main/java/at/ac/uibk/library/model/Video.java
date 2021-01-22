package at.ac.uibk.library.model;

import javax.persistence.Entity;

/**
 * Class that represents a Video. Extends the more general
 * {@link Media}
 * 
 * @author Marcel Huber
 *
 */
@Entity
public class Video extends Media {

	private static final long serialVersionUID = 1L;

	private int length;

	public Video(final String title, final int publishingYear, final String language, final int totalAvail, final int length) {

		super(title, publishingYear, language, totalAvail);
		this.setMediaType(MediaType.VIDEO);
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
