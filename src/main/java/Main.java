import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.*;
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
    private static List<Entry> entries = new ArrayList<>();
    private static File file = new File("result.csv");

    public static void main(String[] args) {
        initRepos();
        getCommits();
        workWithDiffs();
        for (Entry entry : entries) {
            //System.out.println("File: " + entry.toString());
            analyseChangedFile(entry.getChanges());
        }
        writeToCSV();
    }

    private static void initRepos() {
        main = GitWrapper.init(new File("P:/Github/featfork/repos/marlin/.git"));
        repos.add(main);
        //repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/jetty840/.git")));
        repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/marlindev/.git")));
        //repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/pasalab/.git")));
        //repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/richcattell/.git")));
        //repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/thinkyheaddev/.git")));
        //repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/ultimaker2/.git")));
        //repos.add(GitWrapper.init(new File("P:/Github/featfork/repos/wurstnase/.git")));
    }

    private static void getCommits() {
        List<Repository> newRepos = new ArrayList<>();
        for (Repository repo : repos) {
            HashMap<String, RevCommit> commitList = GitWrapper.getCommitList(repo);
            int before = commitList.size();
            if (commits.get(main) != null)
                commitList = GitWrapper.removeUnesessaryCommits(commits.get(main), commitList);
            if (true) {
                commits.put(repo, commitList);
                newRepos.add(repo);
                System.out.println(repo.toString() + ": Before:" + before + " ,After:" + commitList.size());
            } else {
                System.out.println(repo.toString() + ": excluded");
            }

        }
        repos = newRepos;
    }

    private static void workWithDiffs() {
        for (Repository repo : repos) {
            if (repo != main) {
                entries = GitWrapper.getDiff(repo, commits.get(repo));
            }
        }
    }

    private static void analyseChangedFile(List<Change> changes) {
        for (Change change : changes) {
            switch (change.getExpressionType()) {
                case IFDEF:
                    break;
                case ENDIF:
                    break;
                case IF:
                    break;
                case DEF:
                    break;
                case UNDEF:
                    break;
                case ELSE:
                    break;
                case UNKNOWN:
                    break;
            }
        }
    }

    private static void writeToCSV() {
        System.out.print("Updating " + file.getName() + "... ");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Entry entry : entries) {
                out.append(entry.getCSVString());
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Not found " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("IOException for " + file.getAbsolutePath());
        }
        System.out.println("done.");
    }

}
