package at.qe.skeleton.repositories;

import java.util.List;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;

public interface MediaRepository extends AbstractRepository<Media, Long> {

	Media findFirstByTitle(String title);

	Media findFirstByMediaID(Long mediaID);

	List<Media> findByMediaType(MediaType mediaType);

	// TODO: add queries as needed

}
