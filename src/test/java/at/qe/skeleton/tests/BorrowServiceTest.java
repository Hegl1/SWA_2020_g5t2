package at.qe.skeleton.tests;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.*;
import at.qe.skeleton.services.BorrowService;
import at.qe.skeleton.services.MailService;
import at.qe.skeleton.services.UserService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)

@SpringBootTest
@WebAppConfiguration
public class BorrowServiceTest {

    @Autowired
    private BorrowedRepository borrowedRepository;

    @Autowired
    private ReservedRepository reservedRepository;

    @Autowired
    private MediaBorrowTimeRepository mediaBorrowTimeRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedBorrowMedia() {

        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia = this.mediaRepository.findFirstByMediaID(2L);

        Assertions.assertFalse(this.borrowService.getAllBorrowsByUser(testUser).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));

        this.borrowService.borrowMedia(testUser, testMedia);

        Assertions.assertTrue(this.borrowService.getAllBorrowsByUser(testUser).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException.class)
    @DirtiesContext
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedBorrowMedia() {

        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia = this.mediaRepository.findFirstByMediaID(2L);

        Assertions.assertFalse(this.borrowService.getAllBorrowsByUser(testUser).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));

        this.borrowService.borrowMedia(testUser, testMedia);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testBorrowMediaForAuthenticatedUser() {

        Media testMedia = this.mediaRepository.findFirstByMediaID(2L);

        Assertions.assertFalse(this.borrowService.getAllBorrowsByUser(
                this.userService.getAuthenticatedUser()).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));

        Assertions.assertTrue(this.borrowService.borrowMediaForAuthenticatedUser(testMedia));

        Assertions.assertTrue(this.borrowService.getAllBorrowsByUser(
                this.userService.getAuthenticatedUser()).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedUnBorrowMedia() {

        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia1 = this.mediaRepository.findFirstByMediaID(3L);
        Media testMedia2 = this.mediaRepository.findFirstByMediaID(2L);

        Assertions.assertTrue(this.borrowService.unBorrowMedia(testUser, testMedia1));
        Assertions.assertFalse(this.borrowService.unBorrowMedia(testUser, testMedia2));

    }

    @Test(expected = org.springframework.security.access.AccessDeniedException.class)
    @DirtiesContext
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedUnBorrowMedia() {

        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia = this.mediaRepository.findFirstByMediaID(3L);

        Assertions.assertFalse(this.borrowService.unBorrowMedia(testUser, testMedia));
    }

    @Test
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testUnBorrowMediaForAuthenticatedUser() {

        Media testMedia = this.mediaRepository.findFirstByMediaID(3L);

        Assertions.assertTrue(this.borrowService.borrowMediaForAuthenticatedUser(testMedia));
        Assertions.assertFalse(this.borrowService.getAllBorrowsByUser(
                this.userService.getAuthenticatedUser()).stream().noneMatch(
                        borrowed -> borrowed.getMedia().getMediaID() == 3));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllBorrows() {

        Collection<Borrowed> testCollectionOfBorrowed = this.borrowService.getAllBorrows();
        Assertions.assertEquals(10, testCollectionOfBorrowed.size());

        Assertions.assertTrue(testCollectionOfBorrowed.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 3 &&
                        borrowed.getMedia().getCurBorrowed() == 1));
        Assertions.assertTrue(testCollectionOfBorrowed.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 5 &&
                        borrowed.getMedia().getCurBorrowed() == 3));
        Assertions.assertTrue(testCollectionOfBorrowed.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 6 &&
                        borrowed.getMedia().getCurBorrowed() == 2));
        Assertions.assertTrue(testCollectionOfBorrowed.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 7 &&
                        borrowed.getMedia().getCurBorrowed() == 2));
        Assertions.assertTrue(testCollectionOfBorrowed.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 8 &&
                        borrowed.getMedia().getCurBorrowed() == 1));
        Assertions.assertTrue(testCollectionOfBorrowed.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 10 &&
                        borrowed.getMedia().getCurBorrowed() == 1));
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException.class)
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllBorrows() {
        Collection<Borrowed> testCollectionOfBorrowed = this.borrowService.getAllBorrows();
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testGetAllBorrowsByUser() {

        User testUser = this.userRepository.findFirstByUsername("csauer");
        Collection<Borrowed> testCollection = this.borrowService.getAllBorrowsByUser(testUser);
        Assertions.assertEquals(4, testCollection.size());
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 3));
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 5));
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 6));
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 10));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testGetAllBorrowsByUsername() {

        Collection<Borrowed> testCollection = this.borrowService.getAllBorrowsByUsername("csauer");
        Assertions.assertEquals(4, testCollection.size());
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 3));
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 5));
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 6));
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 10));
    }

    @Test
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testGetAllBorrowsByAuthenticatedUser() {

        User testUser = this.userService.getAuthenticatedUser();
        Collection<Borrowed> testCollection = this.borrowService.getAllBorrowsByUser(testUser);
        Assertions.assertEquals(4, testCollection.size());
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 3));
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 5));
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 6));
        Assertions.assertTrue(testCollection.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 10));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllBorrowsByMedia() {
        Collection<Borrowed> testCollectionOfBorrowed = this.borrowService.getAllBorrows();
        Assertions.assertEquals(10, testCollectionOfBorrowed.size());

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllBorrowsByMedia() {

    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedLoadBorrowed() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedLoadBorrowed() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testLoadBorrowedForAuthenticatedUser() {

    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedReserveMedia() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedReserveMedia() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testReserveMedia() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testReserveMediaForAuthenticatedUser() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testRemoveReservationForAuthenticatedUser() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testIsReservedForAuthenticatedUser() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnReserveMedia() {

    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllReserved() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllReserved() {

    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllReservedByUser() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllReservedByUser() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testGetAllReservedByAuthenticatedUser() {

    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllReservedByMedia() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllReservedByMedia() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testGetReservationCountForMedia() {

    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedLoadReserved() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedLoadReserved() {

    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testCheckBorrowTimeout() {

    }

}
