package at.qe.skeleton.repositories;

import at.qe.skeleton.model.AudioBook;
import at.qe.skeleton.model.Media;

public interface AudioBookRepository extends AbstractRepository<AudioBook, Long> {

    Media findFirstByTitle(String title);

    Media findFirstByMediaID(Long mediaID);

    // TODO: add queries as needed
}
