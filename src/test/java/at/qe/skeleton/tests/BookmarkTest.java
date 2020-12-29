package at.qe.skeleton.tests;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.BookmarkRepository;
import at.qe.skeleton.services.BookmarkService;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BookmarkTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @MockBean
    private BookmarkService bookmarkService;

    @MockBean
    private Media media1;


// BookmarkService
    @Test
    @WithMockUser(username = "customer1", authorities = {"CUSTOMER"})
    public void bookmarkAddTest() {

    System.out.println("> add 1 media and check if the size has increased from 0 to 1");

        media1.setMediaType(MediaType.BOOK);
        media1.setMediaID(1);
        media1.setTitle("A Test Book");

        bookmarkService.addBookmark(media1);
        Collection<Bookmark> bcoll = bookmarkRepository.findAll();

        System.out.println(">> These entries are in the Bookmark Table:");
        for(Bookmark bookmark : bcoll) {
            System.out.println(">> Bookmark ID: " + bookmark.getBookmarkID() +", Media: "+bookmark.getMedia() + ", User: " + bookmark.getUser() +" <<");
        }

        Assertions.assertEquals(bcoll.size(), 1);


    System.out.println("> Trying to add again. In intellij the customer user gets the message 'already booked'. In this test I see if the size has stayed at 1");

        bookmarkService.addBookmark(media1);
        Assertions.assertEquals(bcoll.size(), 1);
        System.out.println(">> Worked.");

    }

}



