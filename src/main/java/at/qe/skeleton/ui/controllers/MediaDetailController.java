package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Controller for the media detail view.
 *
 */
@Component
@Scope("view")
public class MediaDetailController implements Serializable {

    @Autowired
    private MediaService mediaService;

    private Media media;

    public void setMedia(final Media media) {
        this.media = media;
        this.doReloadMedia();
    }

    public void doReloadMedia() {
        this.media = this.mediaService.loadMedia(this.media.getMediaID());
    }

    public void doSaveMedia() {
        this.media = this.mediaService.saveMedia(this.media);
    }

    public void doDeleteMedia() {
        this.mediaService.deleteMedia(this.media);
        this.media = null;
    }
}
