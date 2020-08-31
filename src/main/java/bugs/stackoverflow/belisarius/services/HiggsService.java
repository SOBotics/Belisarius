package bugs.stackoverflow.belisarius.services;

import java.util.*;

import io.swagger.client.*;
import io.swagger.client.api.BotApi;
import io.swagger.client.model.*;
import org.threeten.bp.*;

import bugs.stackoverflow.belisarius.models.VandalisedPost;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class HiggsService {

    private static HiggsService instance;
    private String url;
    private BotApi botApi;

    private HiggsService(String url, String key) throws ApiException {
        super();
        this.url = url;
        initHiggs(key);
    }

    public String getUrl() { return url; }

    public static void initInstance(String url, String key) throws ApiException {
        instance = new HiggsService(url, key);
    }

    public static HiggsService getInstance() throws ApiException {
        if (instance == null) {
            throw new ApiException("Higgs has not been initialised");
        }
        return instance;
    }

    private void initHiggs(String key) throws  ApiException {
        this.botApi = new BotApi(Configuration.getDefaultApiClient().setBasePath(this.url));

        AquireTokenRequest tokenRequest = new AquireTokenRequest();
        tokenRequest.dashboardId(3);
        tokenRequest.setSecret(key);

        AquireTokenResponse tokenResponse = this.botApi.botAquireTokenPost(tokenRequest);

        Configuration.getDefaultApiClient().setAccessToken(tokenResponse.getToken());
    }

    public int registerVandalisedPost(VandalisedPost vandalisedPost) throws ApiException {

        RegisterPostRequest postRequest = new RegisterPostRequest();
        postRequest.setContentId(Long.valueOf(vandalisedPost.getPost().getPostId()));
        postRequest.setContentSite(vandalisedPost.getPost().getSite());
        postRequest.setContentType(vandalisedPost.getPost().getPostType());
        postRequest.setContentUrl(vandalisedPost.getPost().getRevisionUrl());
        postRequest.setTitle(vandalisedPost.getPost().getTitle());
        postRequest.setAuthorName(vandalisedPost.getPost().getUser().getUsername());
        postRequest.setAuthorReputation((int) vandalisedPost.getPost().getUser().getReputation());

        List<RegisterPostContentFragment> contentFragments = new ArrayList<>();
        if(!vandalisedPost.getPost().getLastBody().equals("")) {
            RegisterPostContentFragment registerPostContentFragment = new RegisterPostContentFragment();
            registerPostContentFragment.setContent(vandalisedPost.getPost().getLastBody());
            contentFragments.add(registerPostContentFragment);
        }
        if(!vandalisedPost.getPost().getBody().equals("")) {
            RegisterPostContentFragment registerPostContentFragment = new RegisterPostContentFragment();
            registerPostContentFragment.setContent(vandalisedPost.getPost().getBody());
            contentFragments.add(registerPostContentFragment);
        }
        if(!vandalisedPost.getPost().getComment().equals("")) {
            RegisterPostContentFragment registerPostContentFragment = new RegisterPostContentFragment();
            registerPostContentFragment.setContent(vandalisedPost.getPost().getComment());
            contentFragments.add(registerPostContentFragment);
        }
        postRequest.contentFragments(contentFragments);

        postRequest.setDetectionScore(vandalisedPost.getScore());

        Instant detected = Instant.ofEpochSecond(System.currentTimeMillis()/1000);
        postRequest.setDetectedDate(OffsetDateTime.ofInstant(detected, ZoneOffset.UTC));

        Instant created = Instant.ofEpochSecond(vandalisedPost.getPost().getCreationDate());
        postRequest.setContentCreationDate(OffsetDateTime.ofInstant(created, ZoneOffset.UTC));

        List<RegisterPostReason> reasons = new ArrayList<>();
        for(Map.Entry<String, Double> reason : vandalisedPost.getReasons().entrySet()){
            RegisterPostReason registerPostReason = new RegisterPostReason();
            registerPostReason.setReasonName(reason.getKey());
            registerPostReason.setTripped(true);
            registerPostReason.setConfidence(reason.getValue());
            reasons.add(registerPostReason);
        }
        postRequest.setReasons(reasons);

        postRequest.setAllowedFeedback(Arrays.asList(VandalisedPost.Feedback.TP.toString(), VandalisedPost.Feedback.FP.toString()));

        return botApi.botRegisterPostPost(postRequest);
    }

    public void sendFeedback(int reportId, int userId, VandalisedPost.Feedback feedback) throws ApiException{
        RegisterUserFeedbackRequest registerUserFeedbackRequest = new RegisterUserFeedbackRequest();
        registerUserFeedbackRequest.setReportId(reportId);
        registerUserFeedbackRequest.setFeedback(feedback.toString());
        registerUserFeedbackRequest.setUserId(userId);
        botApi.botRegisterUserFeedbackPost(registerUserFeedbackRequest);
    }
}