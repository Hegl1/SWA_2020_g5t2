package at.qe.skeleton.model;

import javax.persistence.Entity;

@Entity
public class Magazine extends Media {

	private static final long serialVersionUID = 1L;

	private String series;

	public String getSeries() {
		return series;
	}

	public void setSeries(final String series) {
		this.series = series;
	}

}
