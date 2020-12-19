package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;


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
     *
     */

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Collection<Media> getAllMedia() {
        return this.mediaRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Collection<AudioBook> getAllAudioBooks() {
        return this.audioBookRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Collection<Book> getAllBooks() {
        return this.bookRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Collection<Magazine> getAllMagazines() {
        return this.magazineRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Collection<Video> getAllVideos() {
        return this.videoRepository.findAll();
    }





    /**
     * Loads a single Media of desired type by its ID.
     *
     */
    // TODO: correct usage of @PreAuthorize annotation

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
     *
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
     * Create Media of desired type.
     *
     */

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createAudioBook(final String title, final Date publishingDate, final String language, final int totalAvail, final MediaType mediaType, final String speaker, final int length, final String author, final String ISBN) {

        AudioBook newAudioBook = new AudioBook(title, publishingDate, language, totalAvail, mediaType, speaker, length, author, ISBN);
        this.saveAudioBook(newAudioBook);
        this.saveMedia(newAudioBook);
        return newAudioBook;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createBook(final String title, final Date publishingDate, final String language, final int totalAvail, final MediaType mediaType, final String author, final String ISBN) {

        Book newBook = new Book(title, publishingDate, language, totalAvail, mediaType, author, ISBN);
        this.saveBook(newBook);
        this.saveMedia(newBook);
        return newBook;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createMagazine(final String title, final Date publishingDate, final String language, final int totalAvail, final MediaType mediaType, final String series) {

        Magazine newMagazine = new Magazine(title, publishingDate, language, totalAvail, mediaType, series);
        this.saveMagazine(newMagazine);
        this.saveMedia(newMagazine);
        return newMagazine;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public Media createVideo(final String title, final Date publishingDate, final String language, final int totalAvail, final MediaType mediaType, final int length) {

        Video newVideo = new Video(title, publishingDate, language, totalAvail, mediaType, length);
        this.saveVideo(newVideo);
        this.saveMedia(newVideo);
        return newVideo;
    }





    /**
     * Delete Media of desired repository
     *
     */
    // TODO: ensure that media gets deleted in every repository it occurs, maybe throw an Exception in default case

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public void deleteMedia(final Media media) {
        this.mediaRepository.delete(media);

        switch (media.getMediaType()) {
            case AUDIOBOOK: this.audioBookRepository.delete((AudioBook) media); break;
            case BOOK:      this.bookRepository.delete((Book) media); break;
            case MAGAZINE:  this.magazineRepository.delete((Magazine) media); break;
            case VIDEO:     this.videoRepository.delete((Video) media); break;
            default:        break;
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public void deleteAudioBook(final AudioBook audioBook) {
        this.audioBookRepository.delete(audioBook);
        this.mediaRepository.delete(audioBook);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public void deleteBook(final Book book) {
        this.bookRepository.delete(book);
        this.mediaRepository.delete(book);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public void deleteMagazine(final Magazine magazine) {
        this.magazineRepository.delete(magazine);
        this.mediaRepository.delete(magazine);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('LIBRARIAN')")
    public void deleteVideo(final Video video) {
        this.videoRepository.delete(video);
        this.mediaRepository.delete(video);
    }





    // potentially some custom Exceptions





}
