package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


/**
 * Service for accessing and manipulating media data.
 */
@Component
@Scope("application")
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private AudioBookRepository audioBookRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MagazineRepository magazineRepository;
    @Autowired
    private VideoRepository videoRepository;


    /**
     * Return collection of Media of desired type.
     */

    public Collection<Media> getAllMedia() {
        return this.mediaRepository.findAll();
    }

    public Collection<AudioBook> getAllAudioBooks() {
        return this.audioBookRepository.findAll();
    }

    public Collection<Book> getAllBooks() {
        return this.bookRepository.findAll();
    }

    public Collection<Magazine> getAllMagazines() {
        return this.magazineRepository.findAll();
    }

    public Collection<Video> getAllVideos() {
        return this.videoRepository.findAll();
    }


    /**
     * Loads a single Media of desired type by its ID.
     */

    public Media loadMedia(final Long mediaId) {
        return this.mediaRepository.findFirstByMediaID(mediaId);
    }

    public Media loadAudioBook(final Long mediaId) {
        return this.audioBookRepository.findFirstByMediaID(mediaId);
    }

    public Media loadBook(final Long mediaId) {
        return this.bookRepository.findFirstByMediaID(mediaId);
    }

    public Media loadMagazine(final Long mediaId) {
        return this.magazineRepository.findFirstByMediaID(mediaId);
    }

    public Media loadVideo(final Long mediaId) {
        return this.videoRepository.findFirstByMediaID(mediaId);
    }


    /**
     * Save media in desired repository.
     */

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media saveMedia(final Media media) {
        return this.mediaRepository.save(media);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media saveAudioBook(final AudioBook audiobook) {
        return this.audioBookRepository.save(audiobook);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media saveBook(final Book book) {
        return this.bookRepository.save(book);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media saveMagazine(final Magazine magazine) {
        return this.magazineRepository.save(magazine);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media saveVideo(final Video video) {
        return this.videoRepository.save(video);
    }


    /**
     * Search in Media by title.
     */
    public Media searchMediaByTitle(String title) {

        Collection<Media> medias = this.getAllMedia();

        for(Media current : medias) {
            if(current.getTitle().equals(title)) {
                return current;
            } else {
                // TODO: throw NoMediaFoundException
            }
        }
        return null;
    }


    /**
     * Filter Media by property
     */

    public Collection<Media> filterMediaByAvailability(Collection<Media> filteredMedia, boolean isAvailable) {
        return filteredMedia.stream().filter(x -> isAvailable ? x.getTotalAvail() > 0 : x.getTotalAvail() == 0).collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<Media> filterMediaByLanguage(Collection<Media> filteredMedia, final String language) {
        return filteredMedia.stream().filter(x -> x.getLanguage().equalsIgnoreCase(language)).collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<Media> filterMediaByType(Collection<Media> filteredMedia, final MediaType mediaType) {
        return filteredMedia.stream().filter(x -> x.getMediaType().equals(mediaType)).collect(Collectors.toCollection(ArrayList::new));
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

    public Collection<String> getAllLanguages() {
        return this.mediaRepository.getAllLanguages();
    }

}
