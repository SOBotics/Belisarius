package bugs.stackoverflow.belisarius.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.VandalisedPost;

import org.jsoup.parser.Parser;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.BotApi;
import io.swagger.client.model.AquireTokenRequest;
import io.swagger.client.model.AquireTokenResponse;
import io.swagger.client.model.RegisterPostContentFragment;
import io.swagger.client.model.RegisterPostReason;
import io.swagger.client.model.RegisterPostRequest;
import io.swagger.client.model.RegisterUserFeedbackRequest;

public final class HiggsService {

    private static HiggsService instance;
    private String url;
    private BotApi botApi;

    private HiggsService(String url, String key) throws ApiException {
        // super();
        this.url = url;
        initHiggs(key);
    }

    public static void initInstance(String url, String key) throws ApiException {
        instance = new HiggsService(url, key);
    }

    public static HiggsService getInstance() throws ApiException {
        if (instance == null) {
            throw new ApiException("Higgs has not been initialised");
        }
        return instance;
    }

    private void initHiggs(String key) throws ApiException {
        this.botApi = new BotApi(Configuration.getDefaultApiClient().setBasePath(this.url));

        AquireTokenRequest tokenRequest = new AquireTokenRequest();
        tokenRequest.dashboardId(3);
        tokenRequest.setSecret(key);

        AquireTokenResponse tokenResponse = this.botApi.botAquireTokenPost(tokenRequest);

        Configuration.getDefaultApiClient().setAccessToken(tokenResponse.getToken());
    }

    public int registerVandalisedPost(VandalisedPost vandalisedPost, Post post, String lastBodyMarkdown, String bodyMarkdown) throws ApiException {

        String body = bodyMarkdown == null ? "The body was not changed in this revision" : bodyMarkdown;
        String lastBody = lastBodyMarkdown == null ? "The body was not changed in this revision" : lastBodyMarkdown;

        String title = Parser.unescapeEntities(post.getTitle(), true);
        // Make it clear the post is an answer in Higgs by prepending 'Answer to: '
        if (post.getPostType().equals("answer")) {
            title = "Answer to: " + title;
        }

        RegisterPostRequest postRequest = new RegisterPostRequest();
        postRequest.setContentId((long) post.getPostId());
        postRequest.setContentSite(post.getSite());
        postRequest.setContentType(post.getPostType());
        postRequest.setContentUrl(post.getRevisionUrl());
        postRequest.setTitle(title);
        postRequest.setAuthorName(post.getUser().getUsername());
        postRequest.setAuthorReputation((int) post.getUser().getReputation());

        RegisterPostContentFragment contentFragmentLastBody = new RegisterPostContentFragment();
        contentFragmentLastBody.setContent(lastBody);
        contentFragmentLastBody.setName("Last body");
        contentFragmentLastBody.setOrder(2);

        RegisterPostContentFragment contentFragmentBody = new RegisterPostContentFragment();
        contentFragmentBody.setContent(body);
        contentFragmentBody.setName("Current body");
        contentFragmentBody.setOrder(1);

        RegisterPostContentFragment contentFragmentComment = new RegisterPostContentFragment();
        contentFragmentComment.setContent(Parser.unescapeEntities(post.getComment(), true));
        contentFragmentComment.setName("Edit summary");
        contentFragmentComment.setOrder(3);

        List<RegisterPostContentFragment> contentFragments = new ArrayList<>();
        contentFragments.add(contentFragmentLastBody);
        contentFragments.add(contentFragmentBody);
        contentFragments.add(contentFragmentComment);
        postRequest.contentFragments(contentFragments);

        postRequest.setDetectionScore(vandalisedPost.getScore());

        Instant detected = Instant.ofEpochSecond(System.currentTimeMillis() / 1000);
        postRequest.setDetectedDate(OffsetDateTime.ofInstant(detected, ZoneOffset.UTC));

        Instant created = Instant.ofEpochSecond(post.getCreationDate());
        postRequest.setContentCreationDate(OffsetDateTime.ofInstant(created, ZoneOffset.UTC));

        List<RegisterPostReason> reasons = new ArrayList<>();
        for (Map.Entry<String, Double> reason : vandalisedPost.getReasonNames().entrySet()) {
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

    public void sendFeedback(int reportId, int userId, VandalisedPost.Feedback feedback) throws ApiException {
        RegisterUserFeedbackRequest registerUserFeedbackRequest = new RegisterUserFeedbackRequest();
        registerUserFeedbackRequest.setReportId(reportId);
        registerUserFeedbackRequest.setFeedback(feedback.toString());
        registerUserFeedbackRequest.setUserId(userId);
        botApi.botRegisterUserFeedbackPost(registerUserFeedbackRequest);
    }
}
