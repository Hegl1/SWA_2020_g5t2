package at.qe.skeleton.tests;


import java.util.Collection;
import java.util.Optional;

import at.qe.skeleton.repositories.MediaRepository;
import at.qe.skeleton.services.MediaService;
import at.qe.skeleton.ui.beans.ContextMocker;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import at.qe.skeleton.ui.controllers.FMSpamController;
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
import javax.persistence.AttributeOverride;


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

		Collection<Bookmark> bcoll_sql = bookmarkService.getAllBookmarks();
		Media media1 = mediaService.loadMedia(4L);
		bookmarkService.addBookmark(media1);
		Collection<Bookmark> bcoll_new = bookmarkService.getAllBookmarks();
		for (Bookmark bookmark0 : bcoll_sql) {
			System.out.println(">> Bookmark ID: " + bookmark0.getBookmarkID() + ", Media: " + bookmark0.getMedia()
					+ ", User: " + bookmark0.getUser() + " <<");
		}
		for (Bookmark bookmark : bcoll_new) {
			System.out.println(">> Bookmark ID: " + bookmark.getBookmarkID() + ", Media: " + bookmark.getMedia()
					+ ", User: " + bookmark.getUser() + " <<");
		}

		Assertions.assertEquals(bcoll_sql.size()+1,bcoll_new.size());
	}

	@Test
	@WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
	public void bookmarkRemoveTest() {

		// ignore FacesContext Messages that some our the services use during test
		FacesContext context = ContextMocker.mockFacesContext();

		Collection<Bookmark> bcoll_sql_start = bookmarkService.getBookmarksByUser(sessionInfoBean.getCurrentUser());
		Optional<Bookmark> firstElement = bcoll_sql_start.stream().findFirst();
		bookmarkService.loadBookmark(firstElement.get().getBookmarkID());
		bookmarkService.deleteBookmark(firstElement.get());
		Collection<Bookmark> bcoll_sql_end = bookmarkService.getBookmarksByUser(sessionInfoBean.getCurrentUser());

		Assertions.assertEquals(bcoll_sql_start.size()-1,bcoll_sql_end.size());
	}

}
