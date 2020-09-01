package bugs.stackoverflow.belisarius.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import bugs.stackoverflow.belisarius.models.VandalisedPost;
import bugs.stackoverflow.belisarius.utils.PathUtils;

public class DbStorageService implements StorageService {

    @Override
    public void saveVandalisedPost(VandalisedPost vandalisedPost) {
        // TODO Auto-generated method stub

    }

    @Override
    public VandalisedPost retrieveVandalisedPost(long postId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveFeedback(long postId, int revisionId, String feedback, long userId) {
        // TODO Auto-generated method stub

    }

}
