package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;

/**
 * Controller for the Media list view.
 */
@Component
@Scope("view")
public class MediaListController implements Serializable {
    @Autowired
    MediaService mediaService;

    /**
     * Returns a collection of all media
     *
     * @return the media collection
     */
    public Collection<Media> getMedia() {
        return this.mediaService.getAllMedia();
    }

    /**
     * Returns a collection of all languages used in the database
     *
     * @return the language collection
     */
    public Collection<String> getAllLanguages() { return this.mediaService.getAllLanguages(); }


    /**
     * Converts the ISO 3166-1 alpha-2 language code into a human-readable
     * language
     *
     * @return the converted language string
     */
    public String convertLanguageHuman(final String language){
        Locale l = new Locale(language);
        return l.getDisplayLanguage();
    }
}