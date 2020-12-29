package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.Video;

public interface VideoRepository extends AbstractRepository<Video, Long> {

    Media findFirstByTitle(String title);

    Media findFirstByMediaID(Long mediaID);

    // TODO: add queries as needed
}
