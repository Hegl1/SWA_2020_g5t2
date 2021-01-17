package at.qe.skeleton.tests;

import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.BookmarkService;
import at.qe.skeleton.services.MediaService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.ContextMocker;
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
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BookmarkServiceTest {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testGetAllBookmarks() {
        Collection<Bookmark> bookmarks = this.bookmarkService.getAllBookmarks();

        Assertions.assertEquals(9, bookmarks.size());
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 1 && bookmark.getUser().getId().equals("lkalt")));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 2 && bookmark.getUser().getId().equals("csauer")));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 2 && bookmark.getUser().getId().equals("mfeld")));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 4 && bookmark.getUser().getId().equals("mfeld")));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 4 && bookmark.getUser().getId().equals("lkalt")));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 6 && bookmark.getUser().getId().equals("mfeld")));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 7 && bookmark.getUser().getId().equals("csauer")));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 8 && bookmark.getUser().getId().equals("csauer")));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 9 && bookmark.getUser().getId().equals("lkalt")));
    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testGetBookmarksByUser() {
        Collection<Bookmark> bookmarks = this.bookmarkService.getBookmarksByUser(this.userService.getAuthenticatedUser());

        Assertions.assertEquals(3, bookmarks.size());
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 2));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 7));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getMedia().getMediaID() == 8));
    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testLoadBookmark() {
        Bookmark bookmark = this.bookmarkService.loadBookmark(1L);

        Assertions.assertEquals("csauer", bookmark.getUser().getId());
        Assertions.assertEquals(2, bookmark.getMedia().getMediaID());
    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testGetMediaTypeAsString() {
        Bookmark bookmark = this.bookmarkService.loadBookmark(1L);
        String mediaType = this.bookmarkService.getMediaTypeAsString(bookmark);

        Assertions.assertEquals("AUDIOBOOK", mediaType);
    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testGetMediaTitle() {
        Bookmark bookmark = this.bookmarkService.loadBookmark(1L);
        String mediaTitle = this.bookmarkService.getMediaTitle(bookmark);

        Assertions.assertEquals("20.000 Meilen unter dem Meer", mediaTitle);
    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testGetMediaInfo() {

        // TODO: determine the usefulness of the method to test
    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testIfCurrentBorrowed() {

        // TODO: determine the usefulness of the method to test
    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testIsBookmarkedForAuthenticatedUser() {
        Media bookmarkedMedia = this.mediaService.loadMedia(2L);
        Media notBookmarkedMedia = this.mediaService.loadMedia(3L);

        Assertions.assertTrue(this.bookmarkService.isBookmarkedForAuthenticatedUser(bookmarkedMedia));
        Assertions.assertFalse(this.bookmarkService.isBookmarkedForAuthenticatedUser(notBookmarkedMedia));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testDeleteBookmark() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        User user = this.userService.getAuthenticatedUser();
        Bookmark bookmarkToDelete = this.bookmarkService.loadBookmark(1L);
        this.bookmarkService.deleteBookmark(bookmarkToDelete);
        Bookmark bookmarkDeleted = this.bookmarkService.loadBookmark(1L);

        Assertions.assertNull(bookmarkDeleted);

        Assertions.assertEquals(2, this.bookmarkService.getBookmarksByUser(user).size());
        this.bookmarkService.deleteBookmark(bookmarkDeleted);
        Assertions.assertEquals(2, this.bookmarkService.getBookmarksByUser(user).size());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testAddBookmark1() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(3L);

        Assertions.assertFalse(this.bookmarkService.isBookmarkedForAuthenticatedUser(media));
        this.bookmarkService.addBookmark(media);
        Assertions.assertTrue(this.bookmarkService.isBookmarkedForAuthenticatedUser(media));

        Assertions.assertEquals(4, this.bookmarkService.getBookmarksByUser(this.userService.getAuthenticatedUser()).size());
        this.bookmarkService.addBookmark(media);
        Assertions.assertEquals(4, this.bookmarkService.getBookmarksByUser(this.userService.getAuthenticatedUser()).size());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testAddBookmark2() {
        Media media = this.mediaService.loadMedia(3L);
        User user = this.userService.getAuthenticatedUser();

        Assertions.assertFalse(this.bookmarkService.isBookmarkedForAuthenticatedUser(media));
        this.bookmarkService.addBookmark(user, media);
        Assertions.assertTrue(this.bookmarkService.isBookmarkedForAuthenticatedUser(media));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testToggleBookmark() {
        // ignore FacesContext Messages that the mediaservice delete function uses
        FacesContext context = ContextMocker.mockFacesContext();

        Media media = this.mediaService.loadMedia(2L);

        Assertions.assertFalse(this.bookmarkService.toggleBookmark(media));
        Assertions.assertTrue(this.bookmarkService.toggleBookmark(media));

    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testGetBookmarkForAuthenticatedUserByMedia() {
        Media media = this.mediaService.loadMedia(2L);
        Bookmark bookmark = this.bookmarkService.getBookmarkForAuthenticatedUserByMedia(media);

        Assertions.assertEquals(1L, bookmark.getBookmarkID());
    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testGetBookmarkByMedia() {
        Media media = this.mediaService.loadMedia(2L);
        List<Bookmark> bookmarks = this.bookmarkService.getBookmarkByMedia(media);

        Assertions.assertEquals(2, bookmarks.size());
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getBookmarkID() == 1));
        Assertions.assertTrue(bookmarks.stream().anyMatch(bookmark -> bookmark.getBookmarkID() == 7));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testSaveBookmark () {
        Media media = this.mediaService.loadMedia(4L);

        Bookmark bookmark = new Bookmark();
        bookmark.setMedia(media);
        bookmark.setUser(this.userService.getAuthenticatedUser());
        this.bookmarkService.saveBookmark(bookmark);
        Bookmark loadedBookmark = this.bookmarkService.loadBookmark(11L);

        Assertions.assertNotNull(loadedBookmark, "New bookmark could not be loaded");
        Assertions.assertEquals(4, loadedBookmark.getMedia().getMediaID(), "password was not encrypted");
        Assertions.assertEquals("csauer", loadedBookmark.getUser().getId(), "First name was not persisted correctly");
    }

    @Test
    @WithMockUser(username = "csauer", authorities = {"Customer"})
    public void testGetBookmarkByUserAndMedia() {
        User user = this.userService.getAuthenticatedUser();
        Media media = this.mediaService.loadMedia(2L);
        Bookmark bookmark = this.bookmarkService.getBookmarkByUserAndMedia(user, media);

        Assertions.assertEquals(1L, bookmark.getBookmarkID());
    }

}
