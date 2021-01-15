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

        User user = this.userRepository.findFirstByUsername("csauer");
        Media media = this.mediaRepository.findFirstByMediaID(2L);
        Media unavailableMedia = this.mediaRepository.findFirstByMediaID(6L);

        Assertions.assertFalse(this.borrowService.borrowMedia(user, unavailableMedia));

        Assertions.assertFalse(this.borrowService.getAllBorrowsByUser(user).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));

        this.borrowService.borrowMedia(user, media);

        Assertions.assertTrue(this.borrowService.getAllBorrowsByUser(user).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedBorrowMedia() {

        User user = this.userRepository.findFirstByUsername("csauer");
        Media media = this.mediaRepository.findFirstByMediaID(2L);

        Assertions.assertFalse(this.borrowService.getAllBorrowsByUser(user).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.borrowMedia(user, media), "AccessDeniedException was not thrown");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testBorrowMediaForAuthenticatedUser() {

        Media media = this.mediaRepository.findFirstByMediaID(2L);

        Assertions.assertFalse(this.borrowService.getAllBorrowsByUser(
                this.userService.getAuthenticatedUser()).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));

        Assertions.assertTrue(this.borrowService.borrowMediaForAuthenticatedUser(media));

        Assertions.assertTrue(this.borrowService.getAllBorrowsByUser(
                this.userService.getAuthenticatedUser()).stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 2));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedUnBorrowMedia() {

        User user = this.userRepository.findFirstByUsername("csauer");
        Media notReservedMedia = this.mediaRepository.findFirstByMediaID(3L);
        Media reservedMedia = this.mediaRepository.findFirstByMediaID(6L);

        this.borrowService.unBorrowMedia(user, notReservedMedia);
        this.borrowService.unBorrowMedia(user, reservedMedia);

        Assertions.assertNull(this.borrowedRepository.findFirstByUserAndMedia(user, notReservedMedia));
        Assertions.assertNull(this.borrowedRepository.findFirstByUserAndMedia(user, reservedMedia));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedUnBorrowMedia() {

        User user = this.userRepository.findFirstByUsername("csauer");
        Media media = this.mediaRepository.findFirstByMediaID(3L);

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.unBorrowMedia(user, media), "AccessDeniedException was not thrown");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testUnBorrowMediaForAuthenticatedUser() {
        Media media = this.mediaRepository.findFirstByMediaID(3L);
        this.borrowService.unBorrowMediaForAuthenticatedUser(media);

        Assertions.assertTrue(this.borrowService.getAllBorrowsByAuthenticatedUser().stream().noneMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 3));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllBorrows() {

        Collection<Borrowed> borrows = this.borrowService.getAllBorrows();
        Assertions.assertEquals(10, borrows.size());

        Assertions.assertTrue(borrows.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 3 &&
                        borrowed.getMedia().getCurBorrowed() == 1));
        Assertions.assertTrue(borrows.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 5 &&
                        borrowed.getMedia().getCurBorrowed() == 3));
        Assertions.assertTrue(borrows.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 6 &&
                        borrowed.getMedia().getCurBorrowed() == 2));
        Assertions.assertTrue(borrows.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 7 &&
                        borrowed.getMedia().getCurBorrowed() == 2));
        Assertions.assertTrue(borrows.stream().anyMatch(
                borrowed -> borrowed.getMedia().getMediaID() == 8 &&
                        borrowed.getMedia().getCurBorrowed() == 1));
        Assertions.assertTrue(borrows.stream().anyMatch(
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
        User user = this.userRepository.findFirstByUsername("csauer");
        Collection<Borrowed> borrows = this.borrowService.getAllBorrowsByUser(user);

        Assertions.assertEquals(4, borrows.size());
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 3));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 5));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 6));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 10));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testGetAllBorrowsByUsername() {
        Collection<Borrowed> borrows = this.borrowService.getAllBorrowsByUsername("csauer");

        Assertions.assertEquals(4, borrows.size());
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 3));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 5));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 6));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 10));
    }

    @Test
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testGetAllBorrowsByAuthenticatedUser() {
        Collection<Borrowed> borrows = this.borrowService.getAllBorrowsByAuthenticatedUser();

        Assertions.assertEquals(4, borrows.size());
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 3));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 5));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 6));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getMedia().getMediaID() == 10));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllBorrowsByMedia() {
        Media media = this.mediaRepository.findFirstByMediaID(5L);
        Collection<Borrowed> borrows = this.borrowService.getAllBorrowsByMedia(media);

        Assertions.assertEquals(3, borrows.size());
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getUser().getId().equals("csauer")));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getUser().getId().equals("lkalt")));
        Assertions.assertTrue(borrows.stream().anyMatch(borrowed -> borrowed.getUser().getId().equals("mfeld")));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllBorrowsByMedia() {
        Media media = this.mediaRepository.findFirstByMediaID(5L);

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.getAllBorrowsByMedia(media), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedLoadBorrowed() {
        User user = this.userRepository.findFirstByUsername("csauer");
        Media media = this.mediaRepository.findFirstByMediaID(3L);
        Borrowed borrowed = this.borrowService.loadBorrowed(user, media);

        Assertions.assertEquals(1L, (long) borrowed.getBorrowID());
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedLoadBorrowed() {
        User user = this.userRepository.findFirstByUsername("csauer");
        Media media = this.mediaRepository.findFirstByMediaID(3L);

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.loadBorrowed(user, media), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testLoadBorrowedForAuthenticatedUser() {
        Media media = this.mediaRepository.findFirstByMediaID(3L);
        Borrowed borrowed = this.borrowService.loadBorrowedForAuthenticatedUser(media);

        Assertions.assertEquals("Playboy", borrowed.getMedia().getTitle());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedReserveMedia() {
        User user = this.userRepository.findFirstByUsername("csauer");
        Media media = this.mediaRepository.findFirstByMediaID(20L);
        this.borrowService.reserveMedia(user, media);
        Collection<Reserved> testCollectionOfReserved = this.borrowService.getAllReservedByUser(user);

        Assertions.assertTrue(testCollectionOfReserved.stream().anyMatch(reserved -> reserved.getMedia().getMediaID() == 20));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedReserveMedia() {
        User user = this.userRepository.findFirstByUsername("csauer");
        Media media = this.mediaRepository.findFirstByMediaID(20L);

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.reserveMedia(user, media), "AccessDeniedException was not thrown");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testReserveMediaForAuthenticatedUser() {
        Media media = this.mediaRepository.findFirstByMediaID(19L);
        this.borrowService.reserveMediaForAuthenticatedUser(media);

        Assertions.assertTrue(this.borrowService.isReservedForAuthenticatedUser(media));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testRemoveReservationForAuthenticatedUser() {
        Media media = this.mediaRepository.findFirstByMediaID(6L);
        this.borrowService.removeReservationForAuthenticatedUser(media);

        Assertions.assertFalse(this.borrowService.isReservedForAuthenticatedUser(media));
    }

    @Test
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testIsReservedForAuthenticatedUser() {
        Media media = this.mediaRepository.findFirstByMediaID(6L);

        Assertions.assertTrue(this.borrowService.isReservedForAuthenticatedUser(media));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllReserved() {
        Collection<Reserved> borrows = this.borrowService.getAllReserved();

        Assertions.assertEquals(borrows.size(), 3);
        Assertions.assertTrue(borrows.stream().anyMatch(reserved -> reserved.getMedia().getMediaID() == 6));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllReserved() {
        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.getAllReserved(), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllReservedByUser() {
        User user = this.userRepository.findFirstByUsername("csauer");
        Collection<Reserved> borrows = this.borrowService.getAllReservedByUser(user);

        Assertions.assertEquals(borrows.size(), 1);
        Assertions.assertTrue(borrows.stream().allMatch(reserved -> reserved.getMedia().getMediaID() == 6));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllReservedByUser() {
        User user = this.userRepository.findFirstByUsername("csauer");

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.getAllReservedByUser(user), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "csauer", authorities = { "CUSTOMER" })
    public void testGetAllReservedByAuthenticatedUser() {
        Collection<Reserved> borrows = this.borrowService.getAllReservedByAuthenticatedUser();

        Assertions.assertEquals(borrows.size(), 1);
        Assertions.assertTrue(borrows.stream().allMatch(reserved -> reserved.getMedia().getMediaID() == 6));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedGetAllReservedByMedia() {
        Media media = this.mediaRepository.findFirstByMediaID(6L);
        Collection<Reserved> borrows = this.borrowService.getAllReservedByMedia(media);

        Assertions.assertEquals(borrows.size(), 3);
        Assertions.assertTrue(borrows.stream().allMatch(reserved -> reserved.getMedia().getMediaID() == 6));
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedGetAllReservedByMedia() {
        Media media = this.mediaRepository.findFirstByMediaID(6L);

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.getAllReservedByMedia(media), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testGetReservationCountForMedia() {
        Media media = this.mediaRepository.findFirstByMediaID(6L);

        Assertions.assertEquals(3, this.borrowService.getReservationCountForMedia(media));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testAuthorizedLoadReserved() {
        Media media = this.mediaRepository.findFirstByMediaID(6L);
        User user = this.userRepository.findFirstByUsername("csauer");

        Assertions.assertEquals(4, this.borrowService.loadReserved(user, media).getReservedID());
    }

    @Test
    @WithMockUser(username = "customer", authorities = { "CUSTOMER" })
    public void testUnauthorizedLoadReserved() {
        Media media = this.mediaRepository.findFirstByMediaID(6L);
        User user = this.userRepository.findFirstByUsername("csauer");

        Assertions.assertThrows(AccessDeniedException.class, () -> this.borrowService.loadReserved(user, media), "AccessDeniedException was not thrown");
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testCheckBorrowTimeout() {
        this.borrowService.checkBorrowTimeout();
    }

}
