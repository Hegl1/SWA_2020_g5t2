package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * Controller for the Media list view.
 */
@Component
@Scope("view")
public class MediaListController implements Serializable {

    @Autowired
    MediaService mediaService;


    /**
     * Returns a Collection of all Media.
     */
    public Collection<Media> getMedia() {
        return this.mediaService.getAllMedia();
    }

    public Collection<String> getAllLanguages() { return this.mediaService.getAllLanguages(); }


    /**
     * Search in Media by Title
     */
    public Media doSearchMediaByTitle(final String title) {
        return this.mediaService.searchMediaByTitle(title);
    }


    /**
     * Filter a Collection of Media by different properties.
     */

    public Collection<Media> doFilterMediaByAvailability(final Collection<Media> mediaList, final boolean isAvailable) {
        return this.mediaService.filterMediaByAvailability(mediaList, isAvailable);
    }

    public Collection<Media> doFilterMediaByLanguage(final Collection<Media> mediaList, final String language) {
        return this.mediaService.filterMediaByLanguage(mediaList, language);
    }

    public Collection<Media> doFilterMediaByType(final Collection<Media> mediaList, final MediaType mediaType) {
        return this.mediaService.filterMediaByType(mediaList, mediaType);
    }

    public Collection<Media> doFilterMediaByAvailability(final boolean isAvailable) {
        return this.mediaService.filterMediaByAvailability(isAvailable);
    }

    public Collection<Media> doFilterMediaByLanguage(final String language) {
        return this.mediaService.filterMediaByLanguage(language);
    }

    public Collection<Media> doFilterMediaByType(final MediaType mediaType) {
        return this.mediaService.filterMediaByType(mediaType);
    }

}