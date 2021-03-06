package at.ac.uibk.library.model;

import javax.persistence.Entity;

/**
 * Class that represents a Book. Extends the more general
 * {@link Media}
 * 
 * @author Marcel Huber
 *
 */
@Entity
public class Book extends Media {

	private static final long serialVersionUID = 1L;

	private String author;
	private String ISBN;

	public Book(final String title, final int publishingYear, final String language, final int totalAvail, final String author, final String ISBN) {

		super(title, publishingYear, language, totalAvail);
		this.setMediaType(MediaType.BOOK);
		this.author = author;
		this.ISBN = ISBN;
	}

	public Book() {

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
}