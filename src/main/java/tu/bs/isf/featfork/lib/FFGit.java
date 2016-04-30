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
import tu.bs.isf.featfork.models.FFCommit;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by Christopher Sontag on 22.04.2016.
 */
public class FFGit extends FFVCSInterface {

    List<RevCommit> commits = new ArrayList<>();
    List<String> urlForks = new ArrayList<>();

    public FFGit(List<String> fileEndings, List<String> blackList) {
        super(fileEndings, blackList);
        this.database = new Database();
        this.analyzer = new Analyzer();
    }

    public FFGit() {
        super(new ArrayList<String>(), new ArrayList<String>());
        this.database = new Database();
        this.analyzer = new Analyzer();
    }

    @Override
    public void startForks(List<String> urls) {
        int i = 0;
        this.urlForks = urls;
        for (String url : urlForks) {
            System.out.println("Processing fork " + (++i));
            run(url);
        }
    }

    @Override
    public void startMain(String url) {
        run(url);
    }

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
        } catch (GitAPIException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("\nError: " + ex.getMessage());
        }
    }

    @Override
    protected void getCommitsForRepository(File file) {
        Repository repo = null;
        FFCommit commitExists = null;
        try {
            repo = new FileRepository(file.getPath() + "/.git");

            Git git = new Git(repo);
            RevWalk revWalk = new RevWalk(repo);
            AnyObjectId headId;

            headId = repo.resolve(Constants.HEAD);
            if (headId != null) {
                RevCommit root = revWalk.parseCommit(headId);
                revWalk.markStart(root);
                Iterator<RevCommit> commitIterator = revWalk.iterator();

                int i = 0;
                if (commitIterator.hasNext()) {
                    RevCommit first = commitIterator.next();
                    while (commitIterator.hasNext()) {
                        RevCommit next = commitIterator.next();
                        System.out.println("Commit: " + (++i));
                        if (urlForks.isEmpty() || !database.existsCommit(first.getName())) {
                            database.insertCommit(first, getFromBranch(git, first.getName()));
                            if (!database.existsChangesForCommit(first.getName())) {
                                commits.add(first);
                            }
                        }
                        if (urlForks.isEmpty() || !database.isCommitInRepo(first.getName(), file.hashCode()))
                            database.insertRepoCommit(file.hashCode(), first.getName());
                        first = next;
                    }
                }
            }
            revWalk.dispose();
            git.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    protected void getChangesForCommit(File file) {
        Repository repo = null;
        List<DiffEntry> diff = null;
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
                    if (filePath.indexOf(".") != -1) {
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    private String getFromBranch(Git git, String hash) {
        List<Ref> refs = new ArrayList<>();
        try {
            refs = git.branchList().setContains(hash).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        if (refs.size() > 0) {
            return refs.get(0).getName();
        }
        return "";
    }

    /**
     * Class for the DiffFormatter
     */
    private class StringOutputStream extends OutputStream {

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