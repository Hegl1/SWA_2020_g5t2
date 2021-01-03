package at.qe.skeleton.model;

import javax.persistence.Entity;

@Entity
public class Magazine extends Media {

	private static final long serialVersionUID = 1L;

	private String series;

	public Magazine(final String title, final int publishingYear, final String language,
					final int totalAvail, final MediaType mediaType, final String series) {
		
		super(title, publishingYear, language, totalAvail, mediaType);
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
