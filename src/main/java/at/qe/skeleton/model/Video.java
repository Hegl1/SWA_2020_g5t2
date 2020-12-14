package at.qe.skeleton.model;

import javax.persistence.Entity;

@Entity
public class Video extends Media {

	private static final long serialVersionUID = 1L;

	private int length;

	public int getLength() {
		return length;
	}

	public void setLength(final int length) {
		this.length = length;
	}

}
