package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

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
    @Autowired
    AudioBookRepository audioBookRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    MagazineRepository magazineRepository;
    @Autowired
    VideoRepository videoRepository;


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
        return this.mediaRepository.findById(mediaId);
    }

    public Media loadAudioBook(final Long mediaId) {
        return this.audioBookRepository.findById(mediaId);
    }

    public Media loadBook(final Long mediaId) {
        return this.bookRepository.findById(mediaId);
    }

    public Media loadMagazine(final Long mediaId) {
        return this.magazineRepository.findById(mediaId);
    }

    public Media loadVideo(final Long mediaId) {
        return this.videoRepository.findById(mediaId);
    }


    /**
     * Save media in desired repository.
     */

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media saveMedia(final Media media) {

        switch(media.getMediaType()) {
            case AUDIOBOOK: return this.audioBookRepository.save((AudioBook) media);
            case BOOK:      return this.bookRepository.save((Book) media);
            case MAGAZINE:  return this.magazineRepository.save((Magazine) media);
            case VIDEO:     return this.videoRepository.save((Video) media);
            default:        return null;
        }
    }


    /**
     * Create Media of desired type.
     */

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createAudioBook(final String title, final int publishingDate, final String language,
                                 final int totalAvail, final MediaType mediaType, final String speaker,
                                 final int length, final String author, final String ISBN) {

        AudioBook newAudioBook =
                new AudioBook(title, publishingDate, language, totalAvail, mediaType, speaker, length, author, ISBN);
        this.saveMedia(newAudioBook);
        return newAudioBook;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createBook(final String title, final int publishingDate, final String language, final int totalAvail,
                            final MediaType mediaType, final String author, final String ISBN) {

        Book newBook = new Book(title, publishingDate, language, totalAvail, mediaType, author, ISBN);
        this.saveMedia(newBook);
        return newBook;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createMagazine(final String title, final int publishingDate, final String language,
                                final int totalAvail, final MediaType mediaType, final String series) {

        Magazine newMagazine = new Magazine(title, publishingDate, language, totalAvail, mediaType, series);
        this.saveMedia(newMagazine);
        return newMagazine;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createVideo(final String title, final int publishingDate, final String language,
                             final int totalAvail, final MediaType mediaType, final int length) {

        Video newVideo = new Video(title, publishingDate, language, totalAvail, mediaType, length);
        this.saveMedia(newVideo);
        return newVideo;
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


    /**
     * Getting all Languages
     */
    public Collection<String> getAllLanguages() {
        return this.mediaRepository.getAllLanguages();
    }


    /**
     * Delete any Media
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public void deleteMedia(final Media media) {

        switch (media.getMediaType()) {
            case AUDIOBOOK: this.audioBookRepository.delete((AudioBook) media); break;
            case BOOK:      this.bookRepository.delete((Book) media); break;
            case MAGAZINE:  this.magazineRepository.delete((Magazine) media); break;
            case VIDEO:     this.videoRepository.delete((Video) media); break;
            default:        break;
        }
    }

}

