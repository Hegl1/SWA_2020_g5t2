package at.ac.uibk.library.model;

import javax.persistence.Entity;

/**
 * Class that represents a Magazine. Extends the more general
 * {@link Media}
 * 
 * @author Marcel Huber
 *
 */
@Entity
public class Magazine extends Media {

	private static final long serialVersionUID = 1L;

	private String series;

	public Magazine(final String title, final int publishingYear, final String language, final int totalAvail,
					final String series) {

		super(title, publishingYear, language, totalAvail);
		this.setMediaType(MediaType.MAGAZINE);
		this.series = series;
	}

	public Magazine() {

	}

	public String getSeries() {
		return this.series;
	}

	public void setSeries(final String series) {
		this.series = series;
	}

}
