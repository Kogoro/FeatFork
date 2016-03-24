import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Christopher Sontag on 24.03.2016.
 */
public class Main {

    private static List<Repository> repos = new ArrayList();
    private static HashMap<Repository, HashMap<String, RevCommit>> commits = new HashMap();
    private static Repository main = null;

    public static void main(String[] args) {
        initRepos();
        getCommits();
    }

    private static void initRepos() {
        main = GitWrapper.init(new File("P:/Github/featfork/repos/marlin/.git"));
        repos.add(main);
        repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/marlindev/.git")));
        repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/pasalab/.git")));
        repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/richcattell/.git")));
        repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/ultimaker2/.git")));
        repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/wurstnase/.git")));
    }

    private static void getCommits() {
        for (Repository repo : repos) {
            HashMap<String, RevCommit> commitList = GitWrapper.getCommitList(repo);
            int before = commitList.size();
            if (commits.get(main) != null)
                commitList = GitWrapper.removeUnesessaryCommits(commits.get(main), commitList);
            commits.put(repo, commitList);
            System.out.println(repo.toString() + ": Before:" + before + " ,After:" + commitList.size());
        }
    }


}
