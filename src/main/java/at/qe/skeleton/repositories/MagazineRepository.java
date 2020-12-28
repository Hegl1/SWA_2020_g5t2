package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Magazine;
import at.qe.skeleton.model.Media;

public interface MagazineRepository extends AbstractRepository<Magazine, Long> {

    Media findFirstByTitle(String title);

    Media findFirstByMediaID(Long mediaID);

    // TODO: add queries as needed
}
