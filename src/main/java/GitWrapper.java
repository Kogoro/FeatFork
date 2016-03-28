import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by csont on 24.03.2016.
 */
public class GitWrapper {

    public static Repository init(File git) {
        try {
            return new FileRepositoryBuilder()
                    .setGitDir(git)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, RevCommit> getCommitList(Repository repo) {
        RevWalk revWalk = new RevWalk(repo);
        AnyObjectId headId;
        RevCommit c;
        HashMap<String, RevCommit> commits = new HashMap();
        try {
            headId = repo.resolve(Constants.HEAD);
            RevCommit root = revWalk.parseCommit(headId);
            revWalk.markStart(root);
            c = revWalk.next();
            while (c != null) {
                commits.put(c.getName(), c);
                c = revWalk.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return commits;
    }

    public static HashMap<String, RevCommit> removeUnesessaryCommits(HashMap<String, RevCommit> main, HashMap<String, RevCommit> repo) {
        if (main == null || repo == null) {
            System.out.println("Nullpointer");
        }
        for (String str : main.keySet()) {
            if (repo.containsKey(str)) {
                repo.remove(str);
            }
        }
        return repo;
    }

    public static List<Entry> getDiff(Repository repo, HashMap<String, RevCommit> commits) {
        try {
            RevWalk revWalk = new RevWalk(repo);
            List<Entry> entries = new ArrayList<>();
            for (int i = 1; i < commits.size(); i++) {
                RevCommit newCommit = commits.get(commits.keySet().toArray()[i]);
                RevCommit oldCommit = commits.get(commits.keySet().toArray()[i - 1]);
                Entry entry = new Entry();
                entry.setAuthor(newCommit.getAuthorIdent().getEmailAddress());
                entry.setDate(new Date(newCommit.getCommitTime() * 1000L));
                entry.setRepositoryName(repo.getFullBranch());
                entry.setCommitHash(newCommit.getName());

                RevTree newRevTree = commits.get(newCommit.getName()).getTree();
                CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
                ObjectReader newReader = repo.newObjectReader();
                newTreeParser.reset(newReader, newRevTree.getId());

                RevTree oldRevTree = commits.get(oldCommit.getName()).getTree();
                CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
                ObjectReader oldReader = repo.newObjectReader();
                oldTreeParser.reset(oldReader, oldRevTree.getId());
                revWalk.dispose();

                Git git = new Git(repo);
                List<DiffEntry> diff = git.diff().
                        setOldTree(oldTreeParser).
                        setNewTree(newTreeParser).
                        call();

                List<Change> changes = new ArrayList<>();
                for (DiffEntry diffEntry : diff) {
                    if (!diffEntry.getNewPath().endsWith(".md")) {

                        String[] strs;
                        OutputStream outputStream = new StringOutputStream();
                        try (DiffFormatter formatter = new DiffFormatter(outputStream)) {
                            formatter.setRepository(repo);
                            formatter.format(diffEntry);
                            String output = ((StringOutputStream) outputStream).getString();
                            strs = output.split("\n");
                            for (String str : strs) {
                                if (str.contains("+") && str.contains("#") && !str.contains("#include") && !str.contains("#error")) {
                                    Change change = new Change();
                                    change.setChangeType(diffEntry.getChangeType());
                                    change.setFile(diffEntry.getNewPath());
                                    change.setRawExpression(str);

                                    //Remove the plus sign, the comments and unimportant whitespaces
                                    str = str.replace("+", "").replaceFirst("\\s?\\/\\/.*", "").trim();
                                    while (str.indexOf(" ") != str.lastIndexOf(" ") && str.indexOf(" ") != -1) {
                                        str = str.replaceFirst(" ", "");
                                    }

                                    // Identify Type and extract Expression
                                    if (str.contains("#ifdef")) {
                                        change.setExpressionType(Change.ExpressionType.IFDEF);
                                        change.setExpression(str.replace("#ifdef", "").replace("#ifndef", "").trim());
                                    } else if (str.contains("#ifndef")) {
                                        change.setExpressionType(Change.ExpressionType.IFNDEF);
                                        change.setExpression(str.replace("#ifndef", "").trim());
                                    } else if (str.contains("#endif")) {
                                        change.setExpressionType(Change.ExpressionType.ENDIF);
                                        change.setExpression(str.replace("#endif", "").trim());
                                    } else if ((str.contains("#if") || str.contains("#elif")) && (str.contains("&") || str.contains("|"))) {
                                        change.setExpressionType(Change.ExpressionType.IF);
                                        change.setExpression(str.replace("#if", "").replace("#elif", "").trim());
                                    } else if ((str.contains("#if") || str.contains("#elif")) && !(str.contains("&") || str.contains("|"))) {
                                        change.setExpressionType(Change.ExpressionType.IF);
                                        change.setExpression(str.replace("#if", "").replace("#elif", "").trim());
                                    } else if (str.contains("#define")) {
                                        change.setExpressionType(Change.ExpressionType.DEF);
                                        change.setExpression(str.replace("#define", "").trim());
                                    } else if (str.contains("#undef")) {
                                        change.setExpressionType(Change.ExpressionType.UNDEF);
                                        change.setExpression(str.replace("#undef", "").trim());
                                    } else if (str.contains("#else")) {
                                        change.setExpressionType(Change.ExpressionType.ELSE);
                                        change.setExpression(str.replace("#else", "").trim());
                                    } else {
                                        change.setExpressionType(Change.ExpressionType.UNKNOWN);
                                        change.setExpression(str);
                                    }
                                    boolean isUnique = true;
                                    for (Change tester : changes) {
                                        if (tester.getExpression() == change.getExpression()) {
                                            System.out.println(change.getExpression() + " is already in the set");
                                            isUnique = false;
                                        }
                                    }
                                    if (isUnique) {
                                        changes.add(change);
                                    }
                                }
                            }
                        }
                    }
                }
                entry.setChanges(changes);
                entries.add(entry);
            }
            return entries;
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (GitAPIException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static class StringOutputStream extends OutputStream {

        StringBuilder mBuf = new StringBuilder();

        public String getString() {
            return mBuf.toString();
        }

        @Override
        public void write(int b) throws IOException {
            mBuf.append((char) b);
        }
    }
}
