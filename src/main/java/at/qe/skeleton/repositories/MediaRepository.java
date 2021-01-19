package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MediaRepository extends AbstractRepository<Media, Long> {

	Media findFirstByTitle(String title);

	Media findFirstByMediaID(Long mediaID);

	Media findFirstByTitleAndPublishingYearAndLanguageAndMediaType(String title, int publishingYear, String language,
			MediaType mediaType);

	List<Media> findByMediaType(MediaType mediaType);

	@Query("SELECT DISTINCT language FROM Media")
	List<String> getAllLanguages();

}
