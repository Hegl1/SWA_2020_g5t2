package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Book;
import at.qe.skeleton.model.Media;

public interface BookRepository extends AbstractRepository<Book, Long> {

    Media findFirstByTitle(String title);

    Media findFirstByMediaID(Long mediaID);

    // TODO: add queries as needed
}
