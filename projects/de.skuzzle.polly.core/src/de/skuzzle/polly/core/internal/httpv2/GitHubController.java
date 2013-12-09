package de.skuzzle.polly.core.internal.httpv2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import de.skuzzle.polly.http.annotations.Post;
import de.skuzzle.polly.http.api.Controller;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswers;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.httpv2.PollyController;
import de.skuzzle.polly.sdk.time.Time;


public class GitHubController extends PollyController {

    private final static Logger logger = Logger.getLogger(GitHubController.class
            .getName());
    
    private final static String API_RECEIVE_EVENT = "/api/github/receive"; //$NON-NLS-1$
    
    private final static int MAX_COMMITS = 10;
    private final static String GIT_HUB_OWNER = "skuzzle"; //$NON-NLS-1$
    private final static String GIT_HUB_REPO = "polly"; //$NON-NLS-1$

    
    private final static List<RepositoryCommit> LATEST_COMMITS;
    private static Date lastRefresh;
    static {
        LATEST_COMMITS = new ArrayList<>(MAX_COMMITS);
    }
    
    
    
    public static Collection<RepositoryCommit> getLatestCommits() {
        synchronized (LATEST_COMMITS) {
            return new ArrayList<>(LATEST_COMMITS);
        }
    }
    
    
    
    public static Date getLastRefresh() {
        return lastRefresh;
    }
    
    
    
    private static void updateCommitList(Collection<RepositoryCommit> all, CommitService cservice) {
        synchronized (LATEST_COMMITS) {
            LATEST_COMMITS.clear();
            int i = 0;
            for (RepositoryCommit ci : all) {
                if (i++ == MAX_COMMITS) {
                    break;
                }
                LATEST_COMMITS.add(ci);
            }
            lastRefresh = Time.currentTime();
            logger.info("Succcessfully read " + i + " commits");  //$NON-NLS-1$//$NON-NLS-2$
        }
    }
    
    
    
    public static void refreshCommits() {
        try {
            final RepositoryService rservice = new RepositoryService();
            final CommitService cservice = new CommitService();
            final Repository repo = rservice.getRepository(GIT_HUB_OWNER, GIT_HUB_REPO);
            final PageIterator<RepositoryCommit> pit = cservice.pageCommits(repo, MAX_COMMITS);
            if (pit.hasNext()) {
                updateCommitList(pit.next(), cservice);
            }
        } catch (IOException e) {
            logger.error("Error while updating GitHub commits", e); //$NON-NLS-1$
        }
    }
    
    
    
    public GitHubController(MyPolly myPolly) {
        super(myPolly);
    }

    
    
    @Override
    protected Controller createInstance() {
        return new GitHubController(this.getMyPolly());
    }
    
    
    
    @Post(API_RECEIVE_EVENT)
    public HttpAnswer receiveEvent() {
        logger.trace("GitHub API Post Request"); //$NON-NLS-1$
        refreshCommits();
        return HttpAnswers.newStringAnswer("ok"); //$NON-NLS-1$
    }
}
