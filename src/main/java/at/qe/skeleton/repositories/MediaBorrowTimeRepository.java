package at.qe.skeleton.repositories;

import at.qe.skeleton.model.MediaBorrowTime;
import at.qe.skeleton.model.MediaType;

import java.util.List;

public interface MediaBorrowTimeRepository extends AbstractRepository<MediaBorrowTime, MediaType> {

	MediaBorrowTime findFirstByMediaType(MediaType mediaType);

	List<MediaBorrowTime> findByAllowedBorrowTime(Integer allowedBorrowTime);

}
