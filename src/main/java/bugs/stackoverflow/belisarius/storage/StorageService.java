package bugs.stackoverflow.belisarius.storage;

import bugs.stackoverflow.belisarius.models.VandalisedPost;

public interface StorageService {

    void saveVandalisedPost(VandalisedPost vandalisedPost);

    VandalisedPost retrieveVandalisedPost(long postId);

    void saveFeedback(long postId, int revisionId, String feedback, long userId);

}
