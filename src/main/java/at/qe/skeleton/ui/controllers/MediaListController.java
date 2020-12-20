package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Controller for the Media list view.
 */
@Component
@Scope("view")
public class MediaListController implements Serializable {

    @Autowired
    private MediaService mediaService;

    /**
     * Returns a Collection of all Media.
     */
    public Collection<Media> getMedia() {
        return this.mediaService.getAllMedia();
    }

}