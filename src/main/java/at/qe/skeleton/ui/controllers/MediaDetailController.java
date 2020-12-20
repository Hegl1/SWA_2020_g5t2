package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Controller for the media detail view.
 */
@Component
@Scope("view")
public class MediaDetailController implements Serializable {

    @Autowired
    private MediaService mediaService;

    /**
     * Attribute to cache the currently displayed media
     */
    private Media media;


    /**
     * Sets the currently displayed media and reloads it from the database.
     */
    public void setMedia(final Media media) {
        this.media = media;
        this.doReloadMedia();
    }


    /**
     * Search in Media by Title
     */
    public Media doSearchMediaByTitle(final String title) {
        return this.mediaService.searchMediaByTitle(title);
    }


    /**
     * Filter a Collection of Media by different properties.
     */

    public Collection<Media> doFilterMediaByAvailability() {
        Collection<Media> media = this.mediaService.filterMediaByAvailability();
        this.doReloadMedia();
        return media;
    }

    public Collection<Media> doFilterMediaByLanguage(final String language) {
        Collection<Media> media = this.mediaService.filterMediaByLanguage(language);
        this.doReloadMedia();
        return media;
    }


    public Collection<Media> doFilterMediaByType(final MediaType mediaType) {
        Collection<Media> media = this.mediaService.filterMediaByType(mediaType);
        this.doReloadMedia();
        return media;
    }


    /**
     * Action to force a reload of the currently displayed media.
     */
    public void doReloadMedia() {
        this.media = this.mediaService.loadMedia(this.media.getMediaID());
    }


    /**
     * Action to save the currently displayed media.
     */
    public void doSaveMedia() {
        this.media = this.mediaService.saveMedia(this.media);
    }

}
