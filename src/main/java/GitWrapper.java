import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
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
}
