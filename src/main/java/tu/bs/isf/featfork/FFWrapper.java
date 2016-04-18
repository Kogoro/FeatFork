package tu.bs.isf.featfork;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.h2.store.Data;
import org.hsqldb.jdbc.JDBCDriver;
import org.sql2o.*;

import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Christopher Sontag on 31.03.2016.
 */
public class FFWrapper {

    private int maxForks;
    private static Repository main;
    private static RepositoryService repoService = new RepositoryService();
    private CommitService commitService = new CommitService(new GitHubClient());

    public int searchedForks = 0, cleanForks = 0;
    public Database database;


    public FFWrapper() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return;
        }

        try {
            DriverManager.registerDriver(new JDBCDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        database = new Database();
    }

    public void setMain(String username, String repo) {
        try {
            main = repoService.getRepository(username, repo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Repository getMain() {
        return main;
    }

    public void getRepositoryAndForks(int maxRepos) throws IOException {
        this.maxForks = maxRepos;

        System.out.println("Downloading: " + main.getId() + " ...");
        downloadRepository(main);
        database.insertRepository(main);
        System.out.println("Gathering Commits for Main Repository: " + main.getId() + " with " + main.getForks() + " Forks ...");
        getCommits((int) main.getId());

        getForks();
    }

    private void downloadRepository(Repository repo) {
        try {
            Git git = Git.cloneRepository().setURI(repo.getCloneUrl()).setDirectory(new File(FeatFork.PATH + repo.getId())).call();
            git.getRepository().close();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void getForks() throws IOException {

        List<Repository> repositories = new ArrayList<>();
        Iterator<Collection<Repository>> repositoryPageIterator = repoService.pageForks(main, maxForks * 2).iterator();
        Collection<Repository> repositoryCollection;
        while (repositoryPageIterator.hasNext()) {
            repositoryCollection = repositoryPageIterator.next();
            repositories.addAll(repositoryCollection);
        }
        for (int i = 0; i < this.maxForks; i++) {
            Repository repo = repositories.get(i);
            Collection<RepositoryCommit> commits = commitService.pageCommits(repo, 1).next();
            System.out.println("Checking SHA: " + commits.iterator().next().getSha() + " for Repository: " + repo.getId());
            if (!database.isCommitInMain(commits.iterator().next().getSha(), (int) main.getId())) {
                if (!database.existsRepository((int) repo.getId())) {
                    System.out.println("Downloading: " + repo.getId() + " with " + repo.getForks() + " Forks ...");
                    downloadRepository(repo);
                    database.insertRepository(repo);
                    System.out.println("Gathering Commits for: " + repo.getId() + " ...");
                    getCommits((int) repo.getId());
                } else {
                    System.out.println("Repository " + repo.getId() + " is already analysed. Skipping repository ...");
                    maxForks++;
                }
            } else {
                System.out.println("Repository " + repo.getId() + " is only a copy of the main repository. Skipping commits ...");
                maxForks++;
                cleanForks++;
            }
            searchedForks++;

        }
    }


    public void getCommits(int idRepository) throws IOException {
        org.eclipse.jgit.lib.Repository repo = new FileRepository(FeatFork.PATH + idRepository + "/.git");
        Git git = new Git(repo);
        RevWalk revWalk = new RevWalk(repo);
        AnyObjectId headId;

        headId = repo.resolve(Constants.HEAD);
        RevCommit root = revWalk.parseCommit(headId);
        revWalk.markStart(root);
        Iterator<RevCommit> commitIterator = revWalk.iterator();
        if (commitIterator.hasNext()) {
            RevCommit first = commitIterator.next();
            // if (!isCommitInMain(first)) {
            while (commitIterator.hasNext()) {
                RevCommit next = commitIterator.next();
                FFCommit commitExists = null;
                commitExists = database.getCommit(first.getName());
                if (commitExists == null) {
                    database.insertCommit(first, getFromBranch(git, first));
                    getDiff(repo, first.getName(), first, next);
                }
                database.insertRepoCommit(idRepository, first.getName());

                first = next;
            }
            /*}
            else {
                System.out.println("Repository "+idRepository+" is only a copy of the main repository. Skipping commits ...");
                maxForks++;
                cleanForks++;
            }*/
        }
    }

    private void getDiff(org.eclipse.jgit.lib.Repository repo, String idCommit, RevCommit first, RevCommit next) throws IOException {

        RevWalk revWalk = new RevWalk(repo);

        RevTree newRevTree = next.getTree();
        CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
        ObjectReader newReader = repo.newObjectReader();
        newTreeParser.reset(newReader, newRevTree.getId());

        RevTree oldRevTree = first.getTree();
        CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
        ObjectReader oldReader = repo.newObjectReader();
        oldTreeParser.reset(oldReader, oldRevTree.getId());
        revWalk.dispose();

        Git git = new Git(repo);
        List<DiffEntry> diff = null;
        try {
            diff = git.diff().
                    setOldTree(oldTreeParser).
                    setNewTree(newTreeParser).
                    call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        OutputStream outputStream = new StringOutputStream();
        for (DiffEntry diffEntry : diff) {
            String file = diffEntry.getNewPath();
            if ((file.endsWith(".h") || file.endsWith(".cpp") || file.endsWith(".c")) && !file.contains("font_data.c")) {
                ((StringOutputStream) outputStream).reset();
                try (DiffFormatter formatter = new DiffFormatter(outputStream)) {
                    formatter.setRepository(repo);
                    formatter.setContext(0);
                    formatter.format(diffEntry);
                    String output = ((StringOutputStream) outputStream).getString();
                    String[] strs = output.split("\n");

                    for (String str : strs) {
                        str = str.trim();

                        if (str.startsWith("+")) {
                            if (str.contains("#") && !str.contains("#define") && !str.contains("#include") && !str.contains("#error") && !str.isEmpty()) {

                                //Remove the plus sign, the comments and unimportant whitespaces
                                str = str.replace("+", "").replace("\"", "\"\"").replaceFirst("\\s?\\/\\/.*", "").trim();

                                String uuid = UUID.randomUUID().toString().replace("-", "");
                                database.insertChange(uuid, file, str);
                                database.insertCommitChange(idCommit, uuid);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * find out which branch that specified commit come from.
     * http://stackoverflow.com/a/13925765
     *
     * @param commit
     * @return branch name.
     * @throws GitAPIException
     */
    public String getFromBranch(Git git, RevCommit commit) {
        Collection<ReflogEntry> entries = null;
        try {
            entries = git.reflog().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        for (ReflogEntry entry : entries) {
            if (!entry.getOldId().getName().equals(commit.getName())) {
                continue;
            }

            CheckoutEntry checkOutEntry = entry.parseCheckout();
            if (checkOutEntry != null) {
                return checkOutEntry.getFromBranch();
            }
        }

        return null;

    }

    /**
     * Class for the DiffFormatter
     */
    class StringOutputStream extends OutputStream {

        String mBuf = "";

        public String getString() {
            return mBuf;
        }

        @Override
        public void write(int b) throws IOException {
            mBuf += ((char) b);
        }

        public void reset() {
            mBuf = "";
        }
    }

}

