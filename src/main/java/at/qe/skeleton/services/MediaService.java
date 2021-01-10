package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Service for accessing and manipulating media data.
 */
@Component
@Scope("application")
public class MediaService {

    @Autowired
    MediaRepository mediaRepository;


    /**
     * Return collection of Media of desired type.
     */
    public Collection<Media> getAllMedia() {
        return this.mediaRepository.findAll();
    }


    /**
     * Loads a single Media of desired type by its ID.
     */
    public Media loadMedia(final Long mediaId) {
        return this.mediaRepository.findFirstByMediaID(mediaId);
    }


    /**
     * Save Media in repository
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media saveMedia(final Media media) {
        return this.mediaRepository.save(media);
    }


    /**
     * Create Media of desired type.
     */

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createAudioBook(final String title, final int publishingDate, final String language,
                                 final int totalAvail, final MediaType mediaType, final String speaker,
                                 final int length, final String author, final String ISBN) {

        Media newAudioBook =
                new AudioBook(title, publishingDate, language, totalAvail, mediaType, speaker, length, author, ISBN);
        this.saveMedia(newAudioBook);
        return newAudioBook;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createBook(final String title, final int publishingDate, final String language, final int totalAvail,
                            final MediaType mediaType, final String author, final String ISBN) {

        Media newBook = new Book(title, publishingDate, language, totalAvail, mediaType, author, ISBN);
        this.saveMedia(newBook);
        return newBook;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createMagazine(final String title, final int publishingDate, final String language,
                                final int totalAvail, final MediaType mediaType, final String series) {

        Media newMagazine = new Magazine(title, publishingDate, language, totalAvail, mediaType, series);
        this.saveMedia(newMagazine);
        return newMagazine;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createVideo(final String title, final int publishingDate, final String language,
                             final int totalAvail, final MediaType mediaType, final int length) {

        Media newVideo = new Video(title, publishingDate, language, totalAvail, mediaType, length);
        this.saveMedia(newVideo);
        return newVideo;
    }


    /**
     * Filter Media by property
     */

    public Collection<Media> filterMediaByTitle(Collection<Media> filteredMedia, String title) {
        return filteredMedia.stream().filter(x -> x.getTitle().toLowerCase().indexOf(title) != -1).collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<Media> filterMediaByAvailability(Collection<Media> filteredMedia, boolean isAvailable) {
        return filteredMedia.stream().filter(x ->
                isAvailable ?
                        (x.getTotalAvail() - x.getCurBorrowed()) > 0 :
                        (x.getTotalAvail() - x.getCurBorrowed()) == 0).collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<Media> filterMediaByLanguage(Collection<Media> filteredMedia, final String language) {
        return filteredMedia.stream().filter(x -> x.getLanguage().equalsIgnoreCase(language)).collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<Media> filterMediaByType(Collection<Media> filteredMedia, final MediaType mediaType) {
        return filteredMedia.stream().filter(x -> x.getMediaType().equals(mediaType)).collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<Media> filterMediaByTitle(String title) {
        return filterMediaByTitle(this.getAllMedia(), title);
    }

    public Collection<Media> filterMediaByAvailability(boolean isAvailable) {
        return filterMediaByAvailability(this.getAllMedia(), isAvailable);
    }

    public Collection<Media> filterMediaByLanguage(final String language) {
        return filterMediaByLanguage(this.getAllMedia(), language);
    }

    public Collection<Media> filterMediaByType(final MediaType mediaType) {
        return filterMediaByType(this.getAllMedia(), mediaType);
    }


    /**
     * Getting all Languages
     */
    public Collection<String> getAllLanguages() {
        return this.mediaRepository.getAllLanguages();
    }


    /**
     * Delete Media from repository
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public void deleteMedia(final Media media) {
        this.mediaRepository.delete(media);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Media was deleted.",  "") );
    }

}

