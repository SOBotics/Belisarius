package bugs.stackoverflow.belisarius.storage;

import bugs.stackoverflow.belisarius.models.VandalisedPost;

public interface StorageService {

	public void saveVandalisedPost(VandalisedPost vandalisedPost);
	public VandalisedPost retrieveVandalisedPost(long postId);
	public void saveFeedback(long postId, int revisionId, String feedback, long userId);

}