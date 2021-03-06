package at.ac.uibk.library.tests;

import at.ac.uibk.library.model.*;
import at.ac.uibk.library.services.MediaService;
import at.ac.uibk.library.ui.beans.ContextMocker;
import at.ac.uibk.library.utils.UnallowedInputException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.faces.context.FacesContext;
import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class MediaServiceTest {

	@Autowired
	private MediaService mediaService;

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testGetAllMedia() {
		Collection<Media> allMedia = this.mediaService.getAllMedia();
		Assertions.assertEquals(20, allMedia.size());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testLoadMedia() {
		Media media = this.mediaService.loadMedia(1L);

		Assertions.assertNotNull(media);
		Assertions.assertEquals("20.000 Meilen unter dem Meer", media.getTitle());
		Assertions.assertEquals(MediaType.BOOK, media.getMediaType());
		Assertions.assertEquals("DE", media.getLanguage());
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testSaveMedia() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {
		AudioBook audioBook = new AudioBook("Some French AudioBook", 2021, "FR", 12, "Frank Elstner", 42069,
				"Thomas Gottschalk", "1234-asdf");
		this.mediaService.saveMedia(audioBook);
		Media loadedNewAudioBook = this.mediaService.loadMedia(21L);

		Assertions.assertNotNull(loadedNewAudioBook);
		Assertions.assertEquals("Some French AudioBook", loadedNewAudioBook.getTitle(), "--wrong title--");
		Assertions.assertEquals(2021, loadedNewAudioBook.getPublishingYear(), "--wrong publishing year--");
		Assertions.assertEquals("FR", loadedNewAudioBook.getLanguage(), "--wrong language--");
		Assertions.assertEquals(12, loadedNewAudioBook.getTotalAvail(), "--wrong total availability--");
		Assertions.assertEquals(MediaType.AUDIOBOOK, loadedNewAudioBook.getMediaType(), "--wrong media type--");
		Assertions.assertEquals("Frank Elstner", ((AudioBook) loadedNewAudioBook).getSpeaker(), "--wrong speaker--");
		Assertions.assertEquals(42069, ((AudioBook) loadedNewAudioBook).getLength(), "--wrong length--");
		Assertions.assertEquals("Thomas Gottschalk", ((AudioBook) loadedNewAudioBook).getAuthor(), "--wrong author--");
		Assertions.assertEquals("1234-asdf", ((AudioBook) loadedNewAudioBook).getISBN(), "--wrong ISBN--");
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testSaveMediaExceptionThrow() throws MediaService.TotalAvailabilitySetTooLowException {
		Media media = this.mediaService.loadMedia(5L);
		media.setTotalAvail(2);
		Assertions.assertThrows(MediaService.TotalAvailabilitySetTooLowException.class,
				() -> this.mediaService.saveMedia(media));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testCreateAudioBook() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {

		this.mediaService.createAudioBook("Some French AudioBook", 2021, "FR", 12, "Frank Elstner", 42069,
				"Thomas Gottschalk", "1234-asdf");
		Media loadedNewAudioBook = this.mediaService.loadMedia(21L);

		Assertions.assertNotNull(loadedNewAudioBook);
		Assertions.assertEquals("Some French AudioBook", loadedNewAudioBook.getTitle(), "--wrong title--");
		Assertions.assertEquals(2021, loadedNewAudioBook.getPublishingYear(), "--wrong publishing year--");
		Assertions.assertEquals("FR", loadedNewAudioBook.getLanguage(), "--wrong language--");
		Assertions.assertEquals(12, loadedNewAudioBook.getTotalAvail(), "--wrong total availability--");
		Assertions.assertEquals(MediaType.AUDIOBOOK, loadedNewAudioBook.getMediaType(), "--wrong media type--");
		Assertions.assertEquals("Frank Elstner", ((AudioBook) loadedNewAudioBook).getSpeaker(), "--wrong speaker--");
		Assertions.assertEquals(42069, ((AudioBook) loadedNewAudioBook).getLength(), "--wrong length--");
		Assertions.assertEquals("Thomas Gottschalk", ((AudioBook) loadedNewAudioBook).getAuthor(), "--wrong author--");
		Assertions.assertEquals("1234-asdf", ((AudioBook) loadedNewAudioBook).getISBN(), "--wrong ISBN--");

	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testCreateBook() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {

		this.mediaService.createBook("Some Greek Book", 2020, "GR", 11, "Thomas Brezina", "5678-jkloe");
		Media loadedNewBook = this.mediaService.loadMedia(21L);

		Assertions.assertNotNull(loadedNewBook, "--media is null--");
		Assertions.assertEquals("Some Greek Book", loadedNewBook.getTitle(), "--wrong title--");
		Assertions.assertEquals(2020, loadedNewBook.getPublishingYear(), "--wrong publishing year--");
		Assertions.assertEquals("GR", loadedNewBook.getLanguage(), "--wrong language--");
		Assertions.assertEquals(11, loadedNewBook.getTotalAvail(), "--wrong total availability--");
		Assertions.assertEquals(MediaType.BOOK, loadedNewBook.getMediaType(), "--wrong media type--");
		Assertions.assertEquals("Thomas Brezina", ((Book) loadedNewBook).getAuthor(), "--wrong author--");
		Assertions.assertEquals("5678-jkloe", ((Book) loadedNewBook).getISBN(), "--wrong ISBN--");
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testCreateMagazine() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {

		this.mediaService.createMagazine("Some Spanish Magazine", 2019, "ES", 10, "Test Series");
		Media loadedNewMagazine = this.mediaService.loadMedia(21L);

		Assertions.assertNotNull(loadedNewMagazine, "--media is null--");
		Assertions.assertEquals("Some Spanish Magazine", loadedNewMagazine.getTitle(), "--wrong title--");
		Assertions.assertEquals(2019, loadedNewMagazine.getPublishingYear(), "--wrong publishing year--");
		Assertions.assertEquals("ES", loadedNewMagazine.getLanguage(), "--wrong language--");
		Assertions.assertEquals(10, loadedNewMagazine.getTotalAvail(), "--wrong total availability--");
		Assertions.assertEquals(MediaType.MAGAZINE, loadedNewMagazine.getMediaType(), "--wrong media type--");
		Assertions.assertEquals("Test Series", ((Magazine) loadedNewMagazine).getSeries(), "--wrong series--");
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testCreateVideo() throws MediaService.TotalAvailabilitySetTooLowException, UnallowedInputException {

		this.mediaService.createVideo("Some Portuguese Video", 2018, "PT", 9, 42068);
		Media loadedNewVideo = this.mediaService.loadMedia(21L);

		Assertions.assertNotNull(loadedNewVideo, "--media is null--");
		Assertions.assertEquals("Some Portuguese Video", loadedNewVideo.getTitle(), "--wrong title--");
		Assertions.assertEquals(2018, loadedNewVideo.getPublishingYear(), "--wrong publishing year--");
		Assertions.assertEquals("PT", loadedNewVideo.getLanguage(), "--wrong language--");
		Assertions.assertEquals(9, loadedNewVideo.getTotalAvail(), "--wrong total availability--");
		Assertions.assertEquals(MediaType.VIDEO, loadedNewVideo.getMediaType(), "--wrong media type--");
		Assertions.assertEquals(42068, ((Video) loadedNewVideo).getLength(), "--wrong length--");
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testFilterMediaByTitle() {
		Collection<Media> allMedia = this.mediaService.getAllMedia();
		Collection<Media> filteredMedia = this.mediaService.filterMediaByTitle(allMedia, "Pride and Prejudice");
		Assertions.assertEquals(3, filteredMedia.size());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testFilterMediaByAvailability() {
		Collection<Media> allMedia = this.mediaService.getAllMedia();
		Collection<Media> filteredMedia = this.mediaService.filterMediaByAvailability(allMedia, false);
		Assertions.assertEquals(1, filteredMedia.size());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testFilterMediaByLanguage() {
		Collection<Media> allMedia = this.mediaService.getAllMedia();
		Collection<Media> filteredMedia = this.mediaService.filterMediaByLanguage(allMedia, "DE");
		Assertions.assertEquals(7, filteredMedia.size());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testFilterMediaByType() {
		Collection<Media> allMedia = this.mediaService.getAllMedia();
		Collection<Media> filteredMedia = this.mediaService.filterMediaByType(allMedia, MediaType.MAGAZINE);
		Assertions.assertEquals(2, filteredMedia.size());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testLoadMediaByLanguageTypeYearTitle() {
		Media media = this.mediaService.loadMedia(3L);
		Media loadedMedia = this.mediaService.loadMediaByLanguageTypeYearTitle(media);

		Assertions.assertEquals(media, loadedMedia);
	}

	@Test
	@DirtiesContext
	public void testGetAllLanguages() {
		Assertions.assertEquals(4, this.mediaService.getAllLanguages().size());
		Assertions.assertTrue(this.mediaService.getAllLanguages().contains("EN"));
		Assertions.assertTrue(this.mediaService.getAllLanguages().contains("DE"));
		Assertions.assertTrue(this.mediaService.getAllLanguages().contains("IT"));
		Assertions.assertTrue(this.mediaService.getAllLanguages().contains("FR"));
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testDeleteMedia() {
		// ignore FacesContext Messages that the mediaservice delete function uses
		FacesContext context = ContextMocker.mockFacesContext();

		Media toDeleteMedia = this.mediaService.loadMedia(18L);
		this.mediaService.deleteMedia(toDeleteMedia);
		Media notExistingMedia = this.mediaService.loadMedia(18L);
		Assertions.assertNull(notExistingMedia);
	}

	@Test
	@DirtiesContext
	@WithMockUser(username = "admin", authorities = { "ADMIN" })
	public void testRefreshMedia() {
		Media media = this.mediaService.loadMedia(16L);

		Assertions.assertEquals("Ein verhei??enes Land", media.getTitle());
		media.setTitle("Harry Potter");
		Assertions.assertEquals("Harry Potter", media.getTitle());
		this.mediaService.refreshMedia(media);
		Assertions.assertEquals("Ein verhei??enes Land", media.getTitle());

	}
}
