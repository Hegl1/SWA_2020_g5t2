package at.qe.skeleton.model;

import javax.persistence.Entity;

@Entity
public class AudioBook extends Media {

	private static final long serialVersionUID = 1L;

	private String speaker;
	private int length;
	private String author;
	private String ISBN;

	public String getSpeaker() {
		return speaker;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(final String iSBN) {
		ISBN = iSBN;
	}

	public void setSpeaker(final String speaker) {
		this.speaker = speaker;
	}

	public long getLength() {
		return length;
	}

	public void setLength(final int length) {
		this.length = length;
	}

}
