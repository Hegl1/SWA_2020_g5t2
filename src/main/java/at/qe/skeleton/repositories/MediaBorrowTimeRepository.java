package at.qe.skeleton.repositories;

import java.util.List;

import at.qe.skeleton.model.MediaBorrowTime;
import at.qe.skeleton.model.MediaType;

public interface MediaBorrowTimeRepository extends AbstractRepository<MediaBorrowTime, MediaType> {

	MediaBorrowTime findFirstByMediaType(MediaType mediaType);

	List<MediaBorrowTime> findByAllowedBorrowTime(Integer allowedBorrowTime);

}
