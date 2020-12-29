package at.qe.skeleton.repositories;

import java.util.List;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;
import org.springframework.data.jpa.repository.Query;

public interface MediaRepository extends AbstractRepository<Media, Long> {

	Media findFirstByTitle(String title);

	Media findFirstByMediaID(Long mediaID);

	List<Media> findByMediaType(MediaType mediaType);

	@Query("SELECT DISTINCT language FROM Media")
	List<String> getAllLanguages();

	// TODO: add queries as needed

}
