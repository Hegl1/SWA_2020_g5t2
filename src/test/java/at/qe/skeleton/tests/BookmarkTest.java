package at.qe.skeleton.tests;


import java.util.Collection;
import java.util.Optional;

import at.qe.skeleton.services.MediaService;
import at.qe.skeleton.ui.beans.ContextMocker;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import at.qe.skeleton.model.Bookmark;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.services.BookmarkService;

import javax.faces.context.FacesContext;



@RunWith(SpringJUnit4ClassRunner.class)

@SpringBootTest
@WebAppConfiguration
public class BookmarkTest {

	@Autowired
	BookmarkService bookmarkService;

	@Autowired
	MediaService mediaService;

	@Autowired
	SessionInfoBean sessionInfoBean;

	@Test
	@WithMockUser(username = "customer2", authorities = { "CUSTOMER" })
	public void bookmarkAddTest() {

		// ignore FacesContext Messages that some our the services use during test
		FacesContext context = ContextMocker.mockFacesContext();

		System.out.println("The initial size of bookmarked items is 1 (see data.sql) ");
		Collection<Bookmark> bcoll_sql = bookmarkService.getAllBookmarks();

		System.out.println("> add 1 media and check if the size has increased from 0 to 1");
		Media media1 = mediaService.loadMedia(4L);
		bookmarkService.addBookmark(media1);

		Collection<Bookmark> bcoll_new = bookmarkService.getAllBookmarks();

		System.out.println(">> These entries are in the starting SQL Bookmark Table:");
		for (Bookmark bookmark0 : bcoll_sql) {
			System.out.println(">> Bookmark ID: " + bookmark0.getBookmarkID() + ", Media: " + bookmark0.getMedia()
					+ ", User: " + bookmark0.getUser() + " <<");
		}

		System.out.println(">> These entries are in the new Bookmark Table:");
		for (Bookmark bookmark : bcoll_new) {
			System.out.println(">> Bookmark ID: " + bookmark.getBookmarkID() + ", Media: " + bookmark.getMedia()
					+ ", User: " + bookmark.getUser() + " <<");
		}

		Assertions.assertEquals(bcoll_sql.size()+1,bcoll_new.size());

		System.out.println(">> Worked.");

	}

	@Test
	@WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
	public void bookmarkRemoveTest() {

		// ignore FacesContext Messages that some our the services use during test
		FacesContext context = ContextMocker.mockFacesContext();

		System.out.println("The initial size of bookmarked items is 1 (see data.sql) ");
		Collection<Bookmark> bcoll_sql = bookmarkService.getAllBookmarks();

		System.out.println("In data.sql there is a bookmark entry for user 'csauer' with bookmarkid=1 and mediaId=2");
		Collection<Bookmark> bcoll_sql_start = bookmarkService.getBookmarksByUser(sessionInfoBean.getCurrentUser());
		Optional<Bookmark> firstElement = bcoll_sql_start.stream().findFirst();
		bookmarkService.loadBookmark(firstElement.get().getBookmarkID());

		System.out.println("Deleting the first bookmark in the collection");
		bookmarkService.deleteBookmark(firstElement.get());

		Collection<Bookmark> bcoll_sql_end = bookmarkService.getBookmarksByUser(sessionInfoBean.getCurrentUser());

		Assertions.assertEquals(bcoll_sql_start.size()-1,bcoll_sql_end.size());
		System.out.println(">> Done.");

	}

}
