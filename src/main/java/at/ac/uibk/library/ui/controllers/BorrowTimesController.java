package at.ac.uibk.library.ui.controllers;

import at.ac.uibk.library.model.MediaBorrowTime;
import at.ac.uibk.library.model.MediaType;
import at.ac.uibk.library.repositories.MediaBorrowTimeRepository;
import at.ac.uibk.library.services.UndoRedoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

@Component
@Scope("view")
public class BorrowTimesController implements Serializable {
	private Collection<MediaBorrowTime> mediaBorrowTimes;

	@Autowired
	private MediaBorrowTimeRepository mediaBorrowTimeRepository;

	@Autowired
	private UndoRedoService undoRedoService;

	@Autowired
	private FMSpamController fms;

	public void setMediaBorrowTimes(final Collection<MediaBorrowTime> mediaBorrowTimes) {
		if (mediaBorrowTimes == null) {
			this.mediaBorrowTimes = mediaBorrowTimeRepository.findAll();
		} else {
			this.mediaBorrowTimes = mediaBorrowTimes;
		}
	}

	public Collection<MediaBorrowTime> getMediaBorrowTimes() {
		return mediaBorrowTimes;
	}

	/**
	 * Saves the currentBorrowTime object to the database
	 */
	public void save() {
		if (mediaBorrowTimes == null) {
			return;
		}

		Collection<MediaBorrowTime> currentBorrowTimes = mediaBorrowTimeRepository.findAll();
		undoRedoService.addAction(
				undoRedoService.createAction(currentBorrowTimes, UndoRedoService.ActionType.EDIT_MEDIA_BORROW_TIME));

		for (MediaBorrowTime bt : mediaBorrowTimes) {
			mediaBorrowTimeRepository.save(bt);
		}

		fms.info("The borrow times were edited");
	}

	/**
	 * Converts the media type into a formatted, human-readable string (first letter
	 * uppercase, the rest lower case)
	 *
	 * @return the converted media type
	 */
	public String getMediaTypeHuman(final MediaType mediaType) {
		return mediaType.toString().substring(0, 1).toUpperCase() + mediaType.toString().substring(1).toLowerCase();
	}
}
