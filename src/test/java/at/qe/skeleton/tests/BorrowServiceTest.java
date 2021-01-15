package at.qe.skeleton.tests;

import at.qe.skeleton.model.Borrowed;
import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.Reserved;
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
import org.springframework.security.access.AccessDeniedException;
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

    @Test
    @DirtiesContext
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedBorrowMedia() {

        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia = this.mediaRepository.findFirstByMediaID(2L);

        Assertions.assertFalse(this.borrowService.getAllBorrowsByUser(testUser).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.borrowMedia(testUser, testMedia), "AccessDeniedException was not thrown");
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

        this.borrowService.unBorrowMedia(testUser, testMedia1);
        this.borrowService.unBorrowMedia(testUser, testMedia2);

        Assertions.assertNull(this.borrowedRepository.findFirstByMedia(testMedia1));
        Assertions.assertNull(this.borrowedRepository.findFirstByMedia(testMedia2));

        // TODO: provoke call for unreserveMedia()
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedUnBorrowMedia() {

        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia = this.mediaRepository.findFirstByMediaID(3L);

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.unBorrowMedia(testUser, testMedia), "AccessDeniedException was not thrown");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testUnBorrowMediaForAuthenticatedUser() {

        Media testMedia = this.mediaRepository.findFirstByMediaID(3L);

        Assertions.assertTrue(this.borrowService.borrowMediaForAuthenticatedUser(testMedia));
        Assertions.assertFalse(this.borrowService.getAllBorrowsByUser(
                this.userService.getAuthenticatedUser()).stream().noneMatch(
                        borrowed -> borrowed.getMedia().getMediaID() == 3));
    }

    @Test
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

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllBorrows() {
        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.getAllBorrows(), "AccessDeniedException was not thrown");
    }

    @Test
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
        Media testMedia = this.mediaRepository.findFirstByMediaID(5L);
        Collection<Borrowed> testCollectionOfBorrowed = this.borrowService.getAllBorrowsByMedia(testMedia);
        Assertions.assertEquals(3, testCollectionOfBorrowed.size());

        Assertions.assertTrue(testCollectionOfBorrowed.stream().anyMatch(borrowed -> borrowed.getUser().getId().equals("csauer")));
        Assertions.assertTrue(testCollectionOfBorrowed.stream().anyMatch(borrowed -> borrowed.getUser().getId().equals("lkalt")));
        Assertions.assertTrue(testCollectionOfBorrowed.stream().anyMatch(borrowed -> borrowed.getUser().getId().equals("mfeld")));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllBorrowsByMedia() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(5L);
        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.getAllBorrowsByMedia(testMedia), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedLoadBorrowed() {
        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia = this.mediaRepository.findFirstByMediaID(3L);
        Borrowed testBorrowed = this.borrowService.loadBorrowed(testUser, testMedia);
        Assertions.assertEquals(1L, (long) testBorrowed.getBorrowID());
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedLoadBorrowed() {
        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia = this.mediaRepository.findFirstByMediaID(3L);
        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.loadBorrowed(testUser, testMedia), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testLoadBorrowedForAuthenticatedUser() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(3L);
        Borrowed testBorrowed = this.borrowService.loadBorrowedForAuthenticatedUser(testMedia);
        Assertions.assertEquals("Playboy", testBorrowed.getMedia().getTitle());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedReserveMedia() {
        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia = this.mediaRepository.findFirstByMediaID(20L);
        this.borrowService.reserveMedia(testUser, testMedia);

        Collection<Reserved> testCollectionOfReserved = this.borrowService.getAllReservedByUser(testUser);
        Assertions.assertTrue(testCollectionOfReserved.stream().anyMatch(reserved -> reserved.getMedia().getMediaID() == 20));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedReserveMedia() {
        User testUser = this.userRepository.findFirstByUsername("csauer");
        Media testMedia = this.mediaRepository.findFirstByMediaID(20L);
        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.reserveMedia(testUser, testMedia), "AccessDeniedException was not thrown");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testReserveMediaForAuthenticatedUser() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(19L);
        this.borrowService.reserveMediaForAuthenticatedUser(testMedia);
        Assertions.assertTrue(this.borrowService.isReservedForAuthenticatedUser(testMedia));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testRemoveReservationForAuthenticatedUser() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(6L);
        this.borrowService.removeReservationForAuthenticatedUser(testMedia);
        Assertions.assertFalse(this.borrowService.isReservedForAuthenticatedUser(testMedia));
    }

    @Test
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testIsReservedForAuthenticatedUser() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(6L);
        Assertions.assertTrue(this.borrowService.isReservedForAuthenticatedUser(testMedia));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllReserved() {
        Collection<Reserved> testCollectionOfReserved = this.borrowService.getAllReserved();
        Assertions.assertEquals(testCollectionOfReserved.size(), 3);
        Assertions.assertTrue(testCollectionOfReserved.stream().anyMatch(reserved -> reserved.getMedia().getMediaID() == 6));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllReserved() {
        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.getAllReserved(), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllReservedByUser() {
        User testUser = this.userRepository.findFirstByUsername("csauer");
        Collection<Reserved> testCollectionOfReserved = this.borrowService.getAllReservedByUser(testUser);
        Assertions.assertEquals(testCollectionOfReserved.size(), 1);
        Assertions.assertTrue(testCollectionOfReserved.stream().allMatch(reserved -> reserved.getMedia().getMediaID() == 6));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllReservedByUser() {
        User testUser = this.userRepository.findFirstByUsername("csauer");
        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.getAllReservedByUser(testUser), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testGetAllReservedByAuthenticatedUser() {
        Collection<Reserved> testCollectionOfReserved = this.borrowService.getAllReservedByAuthenticatedUser();
        Assertions.assertEquals(testCollectionOfReserved.size(), 1);
        Assertions.assertTrue(testCollectionOfReserved.stream().allMatch(reserved -> reserved.getMedia().getMediaID() == 6));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllReservedByMedia() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(6L);
        Collection<Reserved> testCollectionOfReserved = this.borrowService.getAllReservedByMedia(testMedia);
        Assertions.assertEquals(testCollectionOfReserved.size(), 3);
        Assertions.assertTrue(testCollectionOfReserved.stream().allMatch(reserved -> reserved.getMedia().getMediaID() == 6));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllReservedByMedia() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(6L);
        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.getAllReservedByMedia(testMedia), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testGetReservationCountForMedia() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(6L);
        Assertions.assertEquals(3, this.borrowService.getReservationCountForMedia(testMedia));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedLoadReserved() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(6L);
        User testUser = this.userRepository.findFirstByUsername("csauer");
        Assertions.assertEquals(4, this.borrowService.loadReserved(testUser, testMedia).getReservedID());
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedLoadReserved() {
        Media testMedia = this.mediaRepository.findFirstByMediaID(6L);
        User testUser = this.userRepository.findFirstByUsername("csauer");
        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.loadReserved(testUser, testMedia), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testCheckBorrowTimeout() {
        this.borrowService.checkBorrowTimeout();
    }

}
