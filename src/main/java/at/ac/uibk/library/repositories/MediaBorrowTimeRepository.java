package at.ac.uibk.library.repositories;

import at.ac.uibk.library.model.MediaBorrowTime;
import at.ac.uibk.library.model.MediaType;

import java.util.List;

public interface MediaBorrowTimeRepository extends AbstractRepository<MediaBorrowTime, MediaType> {

	MediaBorrowTime findFirstByMediaType(MediaType mediaType);

	List<MediaBorrowTime> findByAllowedBorrowTime(Integer allowedBorrowTime);

}
