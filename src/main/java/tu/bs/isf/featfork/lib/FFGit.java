package tu.bs.isf.featfork.lib;

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
import tu.bs.isf.featfork.FeatFork;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Christopher Sontag
 */
public class FFGit extends FFVCSInterface {

    private List<RevCommit> commits = new ArrayList<>();
    private List<String> urlForks = new ArrayList<>();

    /**
     * Constructor
     *
     * @param fileEndings The allowed file endings
     * @param blackList   The file blacklist
     */
    public FFGit(List<String> fileEndings, List<String> blackList) {
        super(fileEndings, blackList);
        this.database = new FFDatabase();
        this.analyzer = new FFAnalyser();
    }

    /**
     * Constructor
     */
    public FFGit() {
        super(new ArrayList<String>(), new ArrayList<String>());
        this.database = new FFDatabase();
        this.analyzer = new FFAnalyser();
    }

    /**
     * Starts the forking process for multiple urls
     *
     * @param urls The cloneURLs of the forks
     */
    @Override
    public void startForks(List<String> urls) {
        int i = 0;
        this.urlForks = urls;
        for (String url : urlForks) {
            System.out.println("Processing fork " + (++i));
            run(url);
        }
    }

    /**
     * Start the gathering of the main repository
     *
     * @param url The URL of the main repository
     */
    @Override
    public void startMain(String url) {
        run(url);
    }

    /**
     * The generalized gathering method
     *
     * @param url The URL, which should be gathered
     */
    @Override
    protected void run(String url) {
        String filePath = url.replaceFirst("https://.*.com/", "").replace(".git", "").replace("/", "_");
        File pathToFolder = new File(FeatFork.PATH + filePath);
        downloadRepository(url, pathToFolder);
        getCommitsForRepository(pathToFolder);
        System.out.println("Checking " + commits.size());
        if (!getMainRepository().equals(url) && !commits.isEmpty()) {
            getChangesForCommit(pathToFolder);
        }
        commits.clear();
    }

    /**
     * Downloads the repository under the given URL to the given path
     * @param cloneURL The cloneURL
     * @param savePath The path, where the repository should be saved
     */
    @Override
    protected void downloadRepository(String cloneURL, File savePath) {
        System.out.print("Start downloading " + cloneURL + " ... ");
        try {
            if (!savePath.exists()) {
                Git git = Git.cloneRepository().setURI(cloneURL).setDirectory(savePath).call();
                git.close();
            } else {
                System.out.println("Repository already downloaded. Pull the latest changes ...");
                Repository repo = new FileRepository(savePath.getPath() + "/.git");
                Git git = new Git(repo);
                git.pull().call();
            }
            System.out.println("done.");
        } catch (Exception ex) {
            System.out.println("\nError: " + ex.getMessage());
        }
    }

    /**
     * Extraction of the commits for a given repository
     * @param file The folder for the repository, which should be analyzed
     */
    @Override
    protected void getCommitsForRepository(File file) {
        Repository repo;
        try {
            repo = new FileRepository(file.getPath() + "/.git");

            Git git = new Git(repo);
            RevWalk revWalk = new RevWalk(repo);
            AnyObjectId headId;

            headId = repo.resolve(Constants.HEAD);
            if (headId != null) {
                RevCommit root = revWalk.parseCommit(headId);
                revWalk.markStart(root);

                int i = 0;
                for (RevCommit first : revWalk) {
                    i++;
                    String hash = first.getName();
                    if (!database.existsCommit(hash)) {
                        database.insertCommit(first, getBranch(git, hash));
                        if (!database.existsChangesForCommit(hash)) {
                            System.out.println("Commit: " + i + " added.");
                            commits.add(first);
                        } else {
                            System.out.println("Commit: " + i + " already in database.");
                        }
                        if (!database.isCommitInRepo(hash, file.hashCode())) {
                            System.out.println("RepoCommit: " + i + " added.");
                            database.insertRepoCommit(file.hashCode(), hash);
                        }
                    }
                }
            }
            revWalk.dispose();
            git.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Extract the changes from each commit
     * @param file The folder for the repository, which should be analyzed
     */
    @Override
    protected void getChangesForCommit(File file) {
        Repository repo;
        List<DiffEntry> diff;
        try {
            repo = new FileRepository(file.getPath() + "/.git");

            Git git = new Git(repo);
            RevWalk revWalk = new RevWalk(repo);
            AnyObjectId headId;

            headId = repo.resolve(Constants.HEAD);
            RevCommit root = revWalk.parseCommit(headId);
            revWalk.markStart(root);

            for (int i = 0; i < commits.size() - 2; i++) {
                System.out.println("Change: " + i);
                final int index = i;

                RevCommit first = commits.get(index);

                RevCommit next = commits.get(index + 1);

                RevTree newRevTree = next.getTree();
                CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
                ObjectReader newReader = repo.newObjectReader();
                newTreeParser.reset(newReader, newRevTree.getId());

                RevTree oldRevTree = first.getTree();
                CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
                ObjectReader oldReader = repo.newObjectReader();
                oldTreeParser.reset(oldReader, oldRevTree.getId());
                revWalk.dispose();

                diff = git.diff().
                        setOldTree(oldTreeParser).
                        setNewTree(newTreeParser).
                        call();

                OutputStream outputStream = new StringOutputStream();
                for (DiffEntry diffEntry : diff) {
                    String filePath = diffEntry.getNewPath();
                    boolean onBlackList = false;
                    for (String blackWord : blackList) {
                        if (filePath.contains(blackWord)) onBlackList = true;
                    }
                    if (filePath.contains(".")) {
                        String fileExtension = filePath.substring(filePath.lastIndexOf("."));
                        if (fileExtension.contains(fileExtension) && !onBlackList) {
                            ((StringOutputStream) outputStream).reset();
                            try (DiffFormatter formatter = new DiffFormatter(outputStream)) {
                                formatter.setRepository(repo);
                                formatter.setContext(0);
                                formatter.format(diffEntry);

                                String output = ((StringOutputStream) outputStream).getString();
                                String[] strs = output.split("\n");

                                for (String str : strs) {
                                    List<String> features = analyzer.analyse(str);
                                    for (String feature : features) {
                                        if (!feature.isEmpty() && !feature.equals("")) {
                                            String uuid = UUID.randomUUID().toString().replace("-", "");
                                            database.insertChange(uuid, filePath, feature);
                                            database.insertCommitChange(first.getName(), uuid);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                database.insertCommitRatioOverall(first.getName(), analyzer.getRatioOverall());
                HashMap<String, Double> ratioSpecific = analyzer.getRatiosSpecific();
                for (String key : ratioSpecific.keySet()) {
                    database.insertCommitRatioSpecific(first.getName(), key, ratioSpecific.get(key));
                }
                analyzer.resetRatio();
            }
            revWalk.close();
            git.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Returns a branch for a specific commit
     *
     * @param git  The git instance
     * @param hash The commit hash
     * @return String The branchname
     */
    private String getBranch(Git git, String hash) {
        List<Ref> refs = new ArrayList<>();
        try {
            refs = git.branchList().setContains(hash).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        if (refs.size() > 0) {
            return refs.get(0).getName().replace("refs/heads/", "");
        }
        return "";
    }

    /**
     * Class for the DiffFormatter
     */
    private class StringOutputStream extends OutputStream {

        private String mBuf = "";

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
