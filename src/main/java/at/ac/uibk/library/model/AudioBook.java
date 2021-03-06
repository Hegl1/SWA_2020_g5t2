package at.ac.uibk.library.model;

import javax.persistence.Entity;

/**
 * Class that represents an AudioBook. Extends the more general
 * {@link Media}
 * 
 * @author Marcel Huber
 *
 */
@Entity
public class AudioBook extends Media {

	private static final long serialVersionUID = 1L;

	private String speaker;
	private int length;
	private String author;
	private String ISBN;

	public AudioBook(final String title, final int publishingYear, final String language, final int totalAvail, final String speaker, final int length, final String author, final String ISBN) {

		super(title, publishingYear, language, totalAvail);
		this.setMediaType(MediaType.AUDIOBOOK);
		this.speaker = speaker;
		this.length = length;
		this.author = author;
		this.ISBN = ISBN;
	}

	public AudioBook() {

	}

	public String getSpeaker() {
		return this.speaker;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public String getISBN() {
		return this.ISBN;
	}

	public void setISBN(final String iSBN) {
		this.ISBN = iSBN;
	}

	public void setSpeaker(final String speaker) {
		this.speaker = speaker;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(final int length) {
		this.length = length;
	}

}
