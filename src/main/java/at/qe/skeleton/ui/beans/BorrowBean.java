package at.qe.skeleton.ui.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.skeleton.services.BorrowService;

@Component
@Scope("request")
public class BorrowBean {

	@Autowired
	private BorrowService borrowService;

	public String targetMedia;

	public String getTargetMedia() {
		return targetMedia;
	}

	public void setTargetMedia(final String action) {
		this.targetMedia = action;
	}

	public void editTargetMedia() {
		borrowService.borrowMediaForAuthenticatedUser(targetMedia);
	}

}