package tu.bs.isf.featfork;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christopher Sontag on 22.04.2016.
 */
public class FFGithubForkFetcher extends FFForkFetcherInterface {

    private static RepositoryService repoService = new RepositoryService();
    private CommitService commitService = new CommitService(new GitHubClient());
    private static Repository main;

    public FFGithubForkFetcher(String username, String repositoryName, int maxCount, int maxLevel) {
        super(username, repositoryName, maxCount, maxLevel);
        try {
            main = repoService.getRepository(username, repositoryName);
            File filePath = new File(FeatFork.PATH + main.getOwner().getLogin() + "_" + main.getName());
            database.insertRepository(main, filePath.hashCode());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public List<String> getForkURLs() {
        List<String> urls = new ArrayList<>();
        urls.add(main.getCloneUrl());
        urls.addAll(getForksForLevel(main, 1));
        return urls;
    }

    private List<String> getForksForLevel(Repository repository, int level) {
        List<String> urls = new ArrayList<>();
        if (level < maxLevel + 1) {
            List<Repository> repositories = new ArrayList<>();
            try {
                repositories = repoService.getForks(repository);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (!repositories.isEmpty()) {
                for (int i = 0; i < this.maxCount; i++) {
                    Repository repo = repositories.get(i);
                    Collection<RepositoryCommit> commits = commitService.pageCommits(repo, 1).next();
                    System.out.println("Checking SHA: " + commits.iterator().next().getSha() + " for Repository(Level " + level + " ): " + repo.getOwner().getLogin() + "_" + repo.getName());
                    File filePath = new File(FeatFork.PATH + repo.getOwner().getLogin() + "_" + repo.getName());
                    if ((!database.isCommitInMain(commits.iterator().next().getSha(), filePath.hashCode())) || filePath.exists()) {
                        System.out.println("Repository(Level " + level + " ) " + repo.getOwner().getLogin() + "_" + repo.getName() + " added ...");
                        database.insertRepository(repo, filePath.hashCode());
                        urls.add(repo.getCloneUrl());
                        getForksForLevel(repo, level + 1);
                    } else {
                        System.out.println("Repository(Level " + level + " ) " + repo.getOwner().getLogin() + "_" + repo.getName() + " is only a copy of the main repository. Skipping commits ...");
                        maxCount++;
                        cleanForks++;
                    }
                    searchedForks++;
                }
            }
            return urls;
        }
        return urls;
    }

    @Override
    public String getMainURL() {
        if (main == null) return "";
        return main.getCloneUrl();
    }

}
