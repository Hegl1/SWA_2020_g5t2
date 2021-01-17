package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.services.MediaService;
import at.qe.skeleton.services.UndoRedoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@Component
@Scope("view")

public class UndoRedoController implements Serializable {
	@Autowired
	private UndoRedoService undoRedoService;

	/**
	 * Method that checks whether an undo action is available.
	 *
	 * @return true if an action is available, else false.
	 */
	public boolean getUndoActionAvailable(){
		return undoRedoService.isUndoActionAvailable();
	}

	/**
	 * Method that checks whether a redo action is available.
	 *
	 * @return true if an action is available, else false.
	 */
	public boolean getRedoActionAvailable(){
		return undoRedoService.isRedoActionAvailable();
	}

	/**
	 * Method that undoes the last saved action and
	 * outputs a growl message for it
	 *
	 * @return the undid action-type
	 */
	public void undoLastAction() throws MediaService.TotalAvailabilitySetTooLowException {
		UndoRedoService.ActionType type = undoRedoService.undoLastAction();

		String message;

		switch(type){
			case UNBORROW:
				message = "Undid last un-borrowing action";
				break;
			case BORROW:
				message = "Undid last borrowing action";
				break;
			case SAVE_USER:
				message = "Undid creating user";
				break;
			case DELETE_USER:
				message = "Undid deleting user";
				break;
			case EDIT_USER:
				message = "Undid editing user";
				break;
			case SAVE_MEDIA:
				message = "Undid creating media";
				break;
			case DELETE_MEDIA:
				message = "Undid deleting media";
				break;
			case EDIT_MEDIA:
				message = "Undid editing media";
				break;
			case SAVE_BOOKMARK:
				message = "Undid last bookmarking action";
				break;
			case DELETE_BOOKMARK:
				message = "Undid last un-bookmarking action";
				break;
			case EDIT_MEDIA_BORROW_TIME:
				message = "Undid editing media borrow time";
				break;
			default: return;
		}

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
	}

	/**
	 * Method that redoes the last action and
	 * outputs a growl message for it
	 *
	 * @return the redid action-type
	 */
	public void redoLastAction() throws MediaService.TotalAvailabilitySetTooLowException {
		UndoRedoService.ActionType type = undoRedoService.redoLastAction();

		String message;

		switch(type){
			case UNBORROW:
				message = "Redid last undone un-borrowing action";
				break;
			case BORROW:
				message = "Redid last undone borrowing action";
				break;
			case SAVE_USER:
				message = "Redid creating user";
				break;
			case DELETE_USER:
				message = "Redid deleting user";
				break;
			case EDIT_USER:
				message = "Redid editing user";
				break;
			case SAVE_MEDIA:
				message = "Redid creating media";
				break;
			case DELETE_MEDIA:
				message = "Redid deleting media";
				break;
			case EDIT_MEDIA:
				message = "Redid editing media";
				break;
			case SAVE_BOOKMARK:
				message = "Redid last undone bookmarking action";
				break;
			case DELETE_BOOKMARK:
				message = "Redid last undone un-bookmarking action";
				break;
			case EDIT_MEDIA_BORROW_TIME:
				message = "Redid editing media borrow time";
				break;
			default: return;
		}

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
	}
}
