package tu.bs.isf.featfork.lib;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import tu.bs.isf.featfork.FeatFork;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christopher Sontag
 */
public class FFGithubForkFetcher extends FFForkFetcherInterface {

    private static GitHubClient gitHubClient = new GitHubClient().setOAuth2Token("3e24be4b14c1168b5f65b71e1dc1d989d553e6ae");
    private static RepositoryService repoService = new RepositoryService(gitHubClient);
    private static Repository main;
    private File filePathMain;
    private CommitService commitService = new CommitService(gitHubClient);

    /**
     * Constructor
     *
     * @param username       The username of the main repository
     * @param repositoryName The repositoryname of the main repository
     * @param maxCount       The number of maximal forks gathered
     * @param maxLevel       The number of maximal levels searched
     */
    public FFGithubForkFetcher(String username, String repositoryName, int maxCount, int maxLevel) {
        super(username, repositoryName, maxCount + 1, maxLevel);
        try {
            main = repoService.getRepository(username, repositoryName);
            filePathMain = new File(FeatFork.PATH + main.getOwner().getLogin() + "_" + main.getName());
            if (!database.existsRepository(filePathMain.hashCode()))
                database.insertRepository(main, filePathMain.hashCode());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Returns the list of forks URLs
     *
     * @return List<String> The fork URLs
     */
    @Override
    public List<String> getForkURLs() {
        List<String> urls = new ArrayList<>();
        urls.addAll(getForksForLevel(main, 1, "MAIN <- "));
        return urls;
    }

    /**
     * Returns the fork URLs with a given level of depth
     *
     * @param repository The repository
     * @param level      The actual level of the gathering
     * @param chain      The actual pullRequestChain
     * @return List<String> The URLs of the forks
     */
    private List<String> getForksForLevel(Repository repository, int level, String chain) {
        List<String> urls = new ArrayList<>();
        if (level < maxLevel + 1) {
            List<Repository> repositories = new ArrayList<>();
            try {
                repositories = repoService.getForks(repository);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (!repositories.isEmpty()) {
                for (int i = 0; i < (repositories.size() - 1); i++) {
                    if (tempCount == 0) break;
                    Repository repo = repositories.get(i);
                    String chainRepo = chain + repo.getOwner().getLogin();
                    String hash = "";
                    boolean error = false;
                    try {
                        Collection<RepositoryCommit> commits = commitService.pageCommits(repo, 1).next();
                        hash = commits.iterator().next().getSha();
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.getMessage());
                        error = true;
                    }
                    System.out.println("Checking SHA: " + hash + " for Repository(Level " + level + "): " + repo.getOwner().getLogin() + "_" + repo.getName());

                    File filePath = new File(FeatFork.PATH + repo.getOwner().getLogin() + "_" + repo.getName());
                    if (((!database.isCommitInMain(hash, filePathMain.hashCode())) || error)) {
                        if (!database.existsRepository(filePath.hashCode())) {
                            System.out.println("Repository(Level " + level + ") " + repo.getOwner().getLogin() + "_" + repo.getName() + " added with clone URL: " + repo.getCloneUrl() + " ...");
                            database.insertRepository(repo, filePath.hashCode(), chainRepo);
                            tempCount--;
                            urls.add(repo.getCloneUrl());
                            urls.addAll(getForksForLevel(repo, level + 1, chainRepo + " <- "));
                        } else {
                            System.out.println("Repository(Level " + level + ") " + repo.getOwner().getLogin() + "_" + repo.getName() + " is already in the database. Skipping ...");
                        }
                    } else {
                        System.out.println("Repository(Level " + level + ") " + repo.getOwner().getLogin() + "_" + repo.getName() + " is only a copy of the main repository. Skipping ...");
                        cleanForks++;
                    }
                    System.out.println();
                    searchedForks++;
                }
            }
            return urls;
        }
        return urls;
    }

    /**
     * Returns the main URL
     *
     * @return String The main URL
     */
    @Override
    public String getMainURL() {
        if (main == null) return "";
        return main.getCloneUrl();
    }

}
