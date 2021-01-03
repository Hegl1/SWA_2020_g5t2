package at.qe.skeleton.model;

import javax.persistence.Entity;

@Entity
public class AudioBook extends Media {

	private static final long serialVersionUID = 1L;

	private String speaker;
	private int length;
	private String author;
	private String ISBN;

	public AudioBook(final String title, final int publishingYear, final String language,
					 final int totalAvail, final MediaType mediaType, final String speaker,
					 final int length, final String author, final String ISBN) {

		super(title, publishingYear, language, totalAvail, mediaType);
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

	public long getLength() {
		return this.length;
	}

	public void setLength(final int length) {
		this.length = length;
	}

}
