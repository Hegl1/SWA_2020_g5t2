package at.qe.skeleton.model;

import javax.persistence.Entity;

@Entity
public class Book extends Media {

	private static final long serialVersionUID = 1L;

	private String author;
	private String ISBN;

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

}