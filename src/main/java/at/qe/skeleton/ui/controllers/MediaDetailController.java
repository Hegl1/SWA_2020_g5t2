package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.*;
import at.qe.skeleton.services.BookmarkService;
import at.qe.skeleton.services.BorrowService;
import at.qe.skeleton.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Controller for the media detail view.
 */
@Component
@Scope("view")
public class MediaDetailController implements Serializable {

    @Autowired
    MediaService mediaService;

    @Autowired
    BorrowService borrowService;

    @Autowired
    BookmarkService bookmarkService;


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
        FacesContext context = FacesContext.getCurrentInstance();
        Collection<Borrowed> a1 = borrowService.getAllBorrowsByMedia(media);
        if (a1.size() > 0){
            System.out.println("\nThis media is still borrowed by x people: " + a1.size());
            for (Borrowed a : a1) {
                System.out.println("  by: " + a.getUser());
                context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_WARN, "This media is still borrowed by : " + a.getUser().getUsername(), ""));
            }
        } else {
            System.out.println("\nnobody is borrowing it");
        }

        Collection<Bookmark> a2 = bookmarkService.getAllBookmarks();
        List<User> a2_s = new ArrayList<User>();


        for (Bookmark b : a2)
            if(b.getMedia().getMediaID() == media.getMediaID())
                a2_s.add(b.getUser());

        if (a2_s.size() > 0){
            System.out.println("\nThis media is still bookmarked by some people: ");
            for (User u : a2_s){
                System.out.println("  by: "+ u.getUsername());
                context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_WARN, "This media is still bookmarked by: "+ u.getUsername(),  "") );
            }


        } else {
            System.out.println("\nnobody is bookmarking it");
        }

        // only delete if it is not borrowed or bookmarked
        if (!(a1.size() > 0) && !(a2_s.size() > 0)){
            this.mediaService.deleteMedia(media);
            this.media = null;
        }


    }






}
