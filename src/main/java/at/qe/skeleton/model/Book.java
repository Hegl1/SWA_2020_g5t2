package at.qe.skeleton.model;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Book extends Media {

	private static final long serialVersionUID = 1L;

	private String author;
	private String ISBN;

	public Book(final String title, final Date publishingDate, final String language, final int totalAvail, final MediaType mediaType, final String author, final String ISBN) {
		super(title, publishingDate, language, totalAvail, mediaType);
		this.author = author;
		this.ISBN = ISBN;
	}

	public Book() {

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

}