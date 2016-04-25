package tu.bs.isf.featfork;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher Sontag.
 * <p>
 * A class that interacts with the database with the git and github api.
 *
 * @version 1.0
 */
public class Database {

    /**
     * DATABASE INSTANCE
     **/
    private Sql2o sql2o;

    /**
     * SQL CREATE TABLE STATEMENTS
     **/
    private String sqlCreateRepos = "CREATE TABLE IF NOT EXISTS Repository (id INT NOT NULL primary key, name VARCHAR(255), owner VARCHAR(255), pullRequestChain VARCHAR(255) , isFork BOOLEAN, forks INT, watchers INT);";
    private String sqlCreateCommits = "CREATE TABLE IF NOT EXISTS Commit (id VARCHAR(255) NOT NULL, commitHash VARCHAR(255), branch VARCHAR (255), date DATE, author VARCHAR (255) NOT NULL);";
    private String sqlCreateChanges = "CREATE TABLE IF NOT EXISTS Change (id VARCHAR(255) NOT NULL, file VARCHAR (255), expression VARCHAR (512));";
    private String sqlCreateRepoCommits = "CREATE TABLE IF NOT EXISTS RepoCommit (idRepository INT NOT NULL, idCommit VARCHAR(255) NOT NULL);";
    private String sqlCreateCommitChanges = "CREATE TABLE IF NOT EXISTS CommitChange (idCommit VARCHAR(255) NOT NULL, idChange VARCHAR(255) NOT NULL);";
    private String sqlCreateCommitRatioOverall = "CREATE TABLE IF NOT EXISTS CommitRatioOverall (idCommit VARCHAR(255) NOT NULL, ratio DOUBLE NOT NULL);";
    private String sqlCreateCommitRatioSpecific = "CREATE TABLE IF NOT EXISTS CommitRatioSpecific (idCommit VARCHAR(255) NOT NULL, feature VARCHAR(255), ratio DOUBLE NOT NULL);";

    /**
     * SQL SELECT STATEMENTS
     **/
    private String sqlGetRepos = "SELECT id, name, owner, pullRequestChain, isFork, forks, watchers FROM Repository";
    private String sqlGetRepo = "SELECT id, name, pullRequestChain, isFork, forks, watchers FROM Repository Where id = :valId";
    private String sqlGetMainRepo = "SELECT id, name, pullRequestChain, isFork, forks, watchers FROM Repository Where isFork = FALSE";
    private String sqlGetCommit = "SELECT id, commitHash, branch, date, author FROM Commit Where commitHash = :valId";
    private String sqlGetChange = "SELECT id, file, expression FROM Change Where ID = :valId";
    private String sqlGetReposForCommit = "SELECT idRepository FROM RepoCommit Where idCommit = :valId";
    private String sqlGetChangesForCommit = "SELECT idChange FROM CommitChange Where idCommit = :valId";
    private String sqlGetCommitsLeaving = "SELECT idCommit FROM RepoCommit t1 WHERE idRepository = :valRepoId AND NOT EXISTS(SELECT idCommit FROM RepoCommit t2 where idCommit = t1.idCommit AND idRepository = :valRepoMainId);";
    private String sqlGetRatioOverallForCommit = "SELECT ratio FROM CommitRatioOverall Where idCommit = :valId";
    private String sqlGetRatioSpecificForCommit = "SELECT feature,ratio FROM CommitRatioSpecific Where idCommit = :valId";

    /**
     * SQL INSERT STATEMENTS
     **/
    private String sqlInsertRepo = "INSERT INTO Repository(id, name, owner, pullRequestChain, isFork, forks, watchers) values (:valId,:valName, :valOwner, :valChain, :valIsFork, :valForks, :valWatchers)";
    private String sqlInsertRepoCommit = "INSERT INTO RepoCommit(idRepository, idCommit) values (:valIdRepository, :valIdCommit)";
    private String sqlInsertCommit = "INSERT INTO Commit(id, commitHash, branch, date, author) values (:valId, :valCommitHash, :valBranch, :valDate, :valAuthor)";
    private String sqlInsertCommitChange = "INSERT INTO CommitChange(idCommit, idChange) values (:valIdCommit, :valIdChange)";
    private String sqlInsertChange = "INSERT INTO Change(id, file, expression) values (:valId, :valFile, :valExpression)";
    private String sqlInsertCommitRatioOverall = "INSERT INTO CommitRatioOverall(idCommit, ratio) values (:valIdCommit, :valRatio)";
    private String sqlInsertCommitRatioSpecific = "INSERT INTO CommitRatioSpecific(idCommit,feature, ratio) values (:valIdCommit, :valFeature, :valRatio)";

    /**
     * Constructor
     */
    public Database() {
        sql2o = new Sql2o("jdbc:h2:~/test", "SA", "");
        createTables();
    }

    /**
     * Creates tables for the data
     */
    public void createTables() {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlCreateRepos).executeUpdate();
            con.createQuery(sqlCreateCommits).executeUpdate();
            con.createQuery(sqlCreateChanges).executeUpdate();
            con.createQuery(sqlCreateRepoCommits).executeUpdate();
            con.createQuery(sqlCreateCommitChanges).executeUpdate();
            con.createQuery(sqlCreateCommitRatioOverall).executeUpdate();
            con.createQuery(sqlCreateCommitRatioSpecific).executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Checks, whether or not the database exists already
     *
     * @return boolean true, when database exists
     */
    public boolean exists() {
        try (Connection con = sql2o.open()) {
            return con.createQuery(sqlGetRepos).executeAndFetch(FFRepository.class).size() > 0;
        }
    }

    /**
     * Drops all tables
     */
    public void drop() {
        try (Connection con = sql2o.open()) {
            con.createQuery("DROP ALL OBJECTS DELETE FILES").executeUpdate();
        }
    }

    /**
     * Insert a Repository into the database
     *
     * @param repository The repository
     */
    public void insertRepository(FFRepository repository) {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlInsertRepo)
                    .addParameter("valId", repository.getId())
                    .addParameter("valName", repository.getName())
                    .addParameter("valOwner", repository.getName())
                    .addParameter("valChain", "0")
                    .addParameter("valIsFork", repository.isFork())
                    .addParameter("valForks", repository.getForks())
                    .addParameter("valWatchers", repository.getWatchers())
                    .executeUpdate();
        }
    }

    /**
     * Insert a Repository into the database
     *
     * @param repository The repository
     */
    public void insertRepository(Repository repository) {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlInsertRepo)
                    .addParameter("valId", repository.getId())
                    .addParameter("valName", repository.getName())
                    .addParameter("valOwner", repository.getName())
                    .addParameter("valChain", "0")
                    .addParameter("valIsFork", repository.isFork())
                    .addParameter("valForks", repository.getForks())
                    .addParameter("valWatchers", repository.getWatchers())
                    .executeUpdate();
        }
    }

    /**
     * Insert a Repository into the database
     *
     * @param repository The repository
     */
    public void insertRepository(Repository repository, int id) {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlInsertRepo)
                    .addParameter("valId", id)
                    .addParameter("valName", repository.getName())
                    .addParameter("valOwner", repository.getOwner().getLogin())
                    .addParameter("valChain", "0")
                    .addParameter("valIsFork", repository.isFork())
                    .addParameter("valForks", repository.getForks())
                    .addParameter("valWatchers", repository.getWatchers())
                    .executeUpdate();
        }
    }

    /**
     * Insert a commit into the database
     *
     * @param revCommit The commit
     * @param branch    The branch from the commit
     */
    public void insertCommit(RevCommit revCommit, String branch) {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlInsertCommit)
                    .addParameter("valId", revCommit.getName())
                    .addParameter("valCommitHash", revCommit.getName())
                    .addParameter("valBranch", branch)
                    .addParameter("valDate", new java.sql.Date(revCommit.getCommitTime() * 1000L))
                    .addParameter("valAuthor", revCommit.getAuthorIdent().getName())
                    .executeUpdate();
        }
    }

    /**
     * Insert a change into the database
     *
     * @param id         The changeId
     * @param file       The file of the change
     * @param expression The expression, which is used
     */
    public void insertChange(String id, String file, String expression) {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlInsertChange)
                    .addParameter("valId", id)
                    .addParameter("valFile", file)
                    .addParameter("valExpression", expression)
                    .executeUpdate();


        }
    }

    /**
     * Creates a connection between a repository and a commit
     *
     * @param repoId The repository id
     * @param sha    The commit sha
     */
    public void insertRepoCommit(int repoId, String sha) {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlInsertRepoCommit)
                    .addParameter("valIdRepository", repoId)
                    .addParameter("valIdCommit", sha)
                    .executeUpdate();
        }
    }

    /**
     * Creates a connection between a commit and a change
     *
     * @param commitId The commit id
     * @param changeId The change id
     */
    public void insertCommitChange(String commitId, String changeId) {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlInsertCommitChange)
                    .addParameter("valIdCommit", commitId)
                    .addParameter("valIdChange", changeId)
                    .executeUpdate();
        }
    }

    /**
     * Creates a connection between a commit and a change
     *
     * @param commitId The commit id
     * @param ratio The ratio
     */
    public void insertCommitRatioOverall(String commitId, double ratio) {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlInsertCommitRatioOverall)
                    .addParameter("valIdCommit", commitId)
                    .addParameter("valRatio", ratio)
                    .executeUpdate();
        }
    }

    /**
     * Creates a connection between a commit and a change
     *
     * @param commitId The commit id
     * @param feature  The feature name
     * @param ratio    The ratio
     */
    public void insertCommitRatioSpecific(String commitId, String feature, double ratio) {
        try (Connection con = sql2o.open()) {
            con.createQuery(sqlInsertCommitRatioSpecific)
                    .addParameter("valIdCommit", commitId)
                    .addParameter("valFeature", feature)
                    .addParameter("valRatio", ratio)
                    .executeUpdate();
        }
    }

    /**
     * Checks, whether a commit is contained in the main repository
     *
     * @param hash   The commit hash
     * @param mainId The repository id
     * @return boolean true, when the commit is contained
     */
    public boolean isCommitInMain(String hash, int mainId) {
        List<FFRepository> repos = getReposForCommit(hash);
        for (FFRepository repo : repos) {
            if (repo.getId() == mainId) {
                System.out.println(hash + " is in main repository");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks, if a repository already exists
     *
     * @param id The repository id
     * @return boolean true, when the repository exists
     */
    public boolean existsRepository(int id) {
        for (FFRepository repo : getRepositories()) {
            if (repo.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all repositories
     *
     * @return List<FFRepository> A list of all Repositories
     */
    public List<FFRepository> getRepositories() {
        try (Connection con = sql2o.open()) {
            return con.createQuery(sqlGetRepos).executeAndFetch(FFRepository.class);
        }
    }

    /**
     * Gets a specific repository
     *
     * @param id The repository id
     * @return FFRepository The Repository, when it exists
     */
    public FFRepository getRepository(int id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery(sqlGetRepo).addParameter("valId", id).executeAndFetchFirst(FFRepository.class);
        }
    }

    /**
     * Gets a specific repository
     *
     * @return FFRepository The Repository, when it exists
     */
    public FFRepository getMainRepository() {
        try (Connection con = sql2o.open()) {
            return con.createQuery(sqlGetMainRepo).executeAndFetchFirst(FFRepository.class);
        }
    }

    /**
     * Gets a specific commit
     *
     * @param id The commit id
     * @return FFCommit The commit, when it exists
     */
    public FFCommit getCommit(String id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery(sqlGetCommit).addParameter("valId", id).executeAndFetchFirst(FFCommit.class);
        }
    }

    /**
     * Gets a specific change
     *
     * @param id The change id
     * @return FFChange The change, when it exists
     */
    public FFChange getChange(String id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery(sqlGetChange).addParameter("valId", id).executeAndFetchFirst(FFChange.class);
        }
    }

    /**
     * Gets all repositories for a specific commit
     *
     * @param id The commit id
     * @return List<FFRepository> The list of repositories, which contain the commit
     */
    public List<FFRepository> getReposForCommit(String id) {
        List<FFRepository> repos = new ArrayList<>();
        try (Connection con = sql2o.open()) {
            List<Integer> ids = con.createQuery(sqlGetReposForCommit).addParameter("valId", id).executeAndFetch(Integer.class);
            for (Integer repoId : ids) {
                repos.addAll(con.createQuery(sqlGetRepo).addParameter("valId", repoId).executeAndFetch(FFRepository.class));
            }
            return repos;
        }
    }

    /**
     * Gets all commits, which are shared between two repository
     *
     * @param repoId The repository id
     * @param mainId The main repository id
     * @return List<FFCommit> The list of commits, which are shared between the repositories
     */
    public List<FFCommit> getCommitsForRepoLeaving(int repoId, int mainId) {
        List<FFCommit> commits = new ArrayList<>();
        try (Connection con = sql2o.open()) {
            List<String> ids = con.createQuery(sqlGetCommitsLeaving).addParameter("valRepoId", repoId).addParameter("valRepoMainId", mainId).executeAndFetch(String.class);
            for (String commitId : ids) {
                commits.addAll(con.createQuery(sqlGetCommit).addParameter("valId", commitId).executeAndFetch(FFCommit.class));
            }
            return commits;
        }
    }

    /**
     * Gets all changes for a specific commit
     *
     * @param id The commit id
     * @return List<FFChange> The list of changes for a specific commit
     */
    public List<FFChange> getChangesForCommit(String id) {
        List<FFChange> changes = new ArrayList<>();
        try (Connection con = sql2o.open()) {
            List<String> ids = con.createQuery(sqlGetChangesForCommit).addParameter("valId", id).executeAndFetch(String.class);
            for (String changeID : ids) {
                changes.addAll(con.createQuery(sqlGetChange).addParameter("valId", changeID).executeAndFetch(FFChange.class));
            }
            return changes;
        }
    }

    /**
     * Gets the ratio for a specific commit
     *
     * @param id The commit id
     * @return double The ratio
     */
    public double getRatioOverallForCommit(String id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery(sqlGetRatioOverallForCommit).addParameter("valId", id).executeAndFetchFirst(Double.class);
        }
    }

    /**
     * Gets the ratio for a specific commit
     *
     * @param id The commit id
     * @return FFRatioSpecific The ratio
     */
    public FFRatioSpecific getRatioSpecificForCommit(String id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery(sqlGetRatioSpecificForCommit).addParameter("valId", id).executeAndFetchFirst(FFRatioSpecific.class);
        }
    }

    /**
     * Checks, if a commit has changes
     *
     * @param id The commit id
     * @return boolean true, when the repository exists
     */
    public boolean existsChangesForCommit(String id) {
        int changes = getChangesForCommit(id).size();
        return changes > 0;
    }

}
