package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.MediaBorrowTime;
import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.repositories.MediaBorrowTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Collection;

@Component
@Scope("view")
public class BorrowTimesController implements Serializable {
	private Collection<MediaBorrowTime> mediaBorrowTimes;

	@Autowired
	private MediaBorrowTimeRepository mediaBorrowTimeRepository;

	@PostConstruct
	public void init() {
		this.mediaBorrowTimes = mediaBorrowTimeRepository.findAll();
	}

	public Collection<MediaBorrowTime> getMediaBorrowTimes() {
		return mediaBorrowTimes;
	}

	/**
	 * Saves the currentBorrowTime object to the database
	 */
	public void save(){
		if(mediaBorrowTimes == null) return;

		for(MediaBorrowTime bt : mediaBorrowTimes){
			mediaBorrowTimeRepository.save(bt);
		}

		// TODO: add undo/redo action

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "The borrow times were edited", ""));
	}

	/**
	 * Converts the media type into a formatted, human-readable
	 * string (first letter uppercase, the rest lower case)
	 *
	 * @return the converted media type
	 */
	public String getMediaTypeHuman(MediaType mediaType) {
		return mediaType.toString().substring(0, 1).toUpperCase()
				+ mediaType.toString().substring(1).toLowerCase();
	}
}
