package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.*;
import at.qe.skeleton.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;


/**
 * Controller for the media detail view.
 */
@Component
@Scope("view")
public class MediaDetailController implements Serializable {

    @Autowired
    MediaService mediaService;


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
     * Returns the currently displayed media.
     */
    public Media getMedia() { return media; }

    public Book getMediaBook() {
        if(media == null || media.getMediaType() != MediaType.BOOK) return null;

        return (Book) media;
    }

    public AudioBook getMediaAudioBook() {
        if(media == null || media.getMediaType() != MediaType.AUDIOBOOK) return null;

        return (AudioBook) media;
    }

    public Video getMediaVideo() {
        if(media == null || media.getMediaType() != MediaType.VIDEO) return null;

        return (Video) media;
    }

    public Magazine getMediaMagazine() {
        if(media == null || media.getMediaType() != MediaType.MAGAZINE) return null;

        return (Magazine) media;
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


    /**
     * Create different Medias.
     */

    public void doCreateAudioBook(final String title, final int publishingDate, final String language,
                                  final int totalAvail, final MediaType mediaType, final String speaker,
                                  final int length, final String author, final String ISBN) {

        this.mediaService.createAudioBook(title, publishingDate, language, totalAvail, mediaType, speaker, length, author, ISBN);
        // this.doReloadMedia();
    }

    public void doCreateBook(final String title, final int publishingDate, final String language, final int totalAvail,
                             final MediaType mediaType, final String author, final String ISBN) {

        this.mediaService.createBook(title, publishingDate, language, totalAvail, mediaType, author, ISBN);
        // this.doReloadMedia();
    }

    public void doCreateMagazine(final String title, final int publishingDate, final String language,
                                 final int totalAvail, final MediaType mediaType, final String series) {

        this.mediaService.createMagazine(title, publishingDate, language, totalAvail, mediaType, series);
        // this.doReloadMedia();
    }

    public void doCreateVideo(final String title, final int publishingDate, final String language,
                              final int totalAvail, final MediaType mediaType, final int length) {

        this.mediaService.createVideo(title, publishingDate, language, totalAvail, mediaType, length);
        // this.doReloadMedia();
    }

    /**
     * Action to delete the currently displayed media.
     */
    public void doDeleteMedia(final Media media) {
        this.mediaService.deleteMedia(media);
        this.media = null;
    }
}
