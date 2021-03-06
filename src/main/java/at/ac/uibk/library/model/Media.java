package at.ac.uibk.library.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Entity representing a general Media. Only exists as in more concrete classes,
 * see {@link AudioBook},
 * {@link Magazine}, {@link Book},
 * {@link Video}
 * 
 * @author Marcel Huber
 * @version 1.0
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Media implements Persistable<Long>, Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "media_sequence", initialValue = 21)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_sequence")
	private Long mediaID;
	private String title;
	private int publishingYear;
	private String language;
	private int totalAvail;
	private int curBorrowed;
	@Enumerated(EnumType.STRING)
	private MediaType mediaType;

	/**
	 * Default constructor.
	 */
	public Media() {

	}

	/**
	 * Construcor that sets all the fileds of Media.
	 * 
	 * @param title          title of the media
	 * @param publishingYear publishing year of the media
	 * @param language       2 digit language code
	 * @param totalAvail     number of totally available copies
	 */
	public Media(final String title, final int publishingYear, final String language, final int totalAvail) {

		this.title = title;
		this.publishingYear = publishingYear;
		this.language = language;
		this.totalAvail = totalAvail;
		this.curBorrowed = 0;
	}

	public long getMediaID() {
		return this.mediaID;
	}

	public void setMediaID(final long mediaID) {
		this.mediaID = mediaID;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public int getPublishingYear() {
		return this.publishingYear;
	}

	public void setPublishingYear(final int publishingYear) {
		this.publishingYear = publishingYear;
	}

	public String getLanguage() {
		return this.language;
	}

	/**
	 * Converts the ISO 3166-1 alpha-2 language code into a human-readable
	 * language
	 *
	 * @return the converted language string
	 */
	public String getLanguageHuman() {
		if(this.language == null) return null;

		Locale l = new Locale(this.language);
		return l.getDisplayLanguage();
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public int getTotalAvail() {
		return this.totalAvail;
	}

	public void setTotalAvail(final int totalAvail) {
		this.totalAvail = totalAvail;
	}

	public MediaType getMediaType() {
		return this.mediaType;
	}

	/**
	 * Converts the media type into a formatted, human-readable
	 * string (first letter uppercase, the rest lower case)
	 *
	 * @return the converted media type
	 */
	public String getMediaTypeHuman() {
		if(this.mediaType == null) return null;

		return this.mediaType.toString().substring(0, 1).toUpperCase()
				+ this.mediaType.toString().substring(1).toLowerCase();
	}

	public void setMediaType(final MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public int getCurBorrowed() {
		return this.curBorrowed;
	}

	public void setCurBorrowed(final int curBorrowed) {
		this.curBorrowed = curBorrowed;
	}

	@Override
	public Long getId() {
		return this.mediaID;
	}

	@Override
	public boolean isNew() {
		return this.mediaID == null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Media media = (Media) o;

		if (this.publishingYear == media.publishingYear && this.totalAvail == media.totalAvail &&
				this.curBorrowed == media.curBorrowed && Objects.equals(this.mediaID, media.mediaID) &&
				Objects.equals(this.title, media.title) && Objects.equals(this.language, media.language) &&
				this.mediaType == media.mediaType) {

			switch (this.mediaType) {

				case AUDIOBOOK:
					return ((AudioBook) this).getAuthor().equals(((AudioBook) media).getAuthor()) &&
							((AudioBook) this).getISBN().equals(((AudioBook) media).getISBN()) &&
							((AudioBook) this).getLength() == ((AudioBook) media).getLength() &&
							((AudioBook) this).getSpeaker().equals(((AudioBook) media).getSpeaker());

				case BOOK:
					return ((Book) this).getAuthor().equals(((Book) media).getAuthor()) &&
							((Book) this).getISBN().equals(((Book) media).getISBN());

				case MAGAZINE:
					return ((Magazine) this).getSeries().equals(((Magazine) media).getSeries());

				case VIDEO:
					return ((Video) this).getLength() == (((Video) media).getLength());
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(mediaID, title, publishingYear, language, totalAvail, curBorrowed, mediaType);
	}

	/**
	 * Checks whether the media is currently available (totalAvail > currently borrowed)
	 *
	 * @return true if there are copies available, false otherwise
	 */
	public boolean getAvailable() {
		return totalAvail > curBorrowed;
	}
}
