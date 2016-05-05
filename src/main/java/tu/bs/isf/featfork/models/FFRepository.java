package tu.bs.isf.featfork.models;

import org.eclipse.egit.github.core.Repository;

/**
 * Created by Christopher Sontag
 */
public class FFRepository implements Comparable<FFRepository> {

    private int id;
    private String name = "";
    private String owner = "";
    private String pullRequestChain = "";
    private boolean isFork = false;
    private int forks = 0;
    private int watchers = 0;

    /**
     * Constructor
     *
     * @param id               The repository id
     * @param name             The repositoryname
     * @param owner            The owner
     * @param pullRequestChain The pullRequestChain
     * @param isFork           Is Fork?
     * @param forks            Number of forks
     * @param watchers         Number of watchers
     */
    public FFRepository(int id, String name, String owner, String pullRequestChain, boolean isFork, int forks, int watchers) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.pullRequestChain = pullRequestChain;
        this.isFork = isFork;
        this.forks = forks;
        this.watchers = watchers;
    }

    /**
     * Constructor
     *
     * @param repository The repository
     */
    public FFRepository(Repository repository) {
        this.isFork = repository.isFork();
        this.name = repository.getName();
        this.forks = repository.getForks();
        this.id = (int) repository.getId();
        this.name = repository.getName();
        this.owner = repository.getOwner().getLogin();
        this.pullRequestChain = "MAIN -> THIS";
    }

    /**
     * Returns the ID
     *
     * @return int The id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID
     *
     * @param id The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the name
     * @return String The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     * @param repositoryName The name
     */
    public void setName(String repositoryName) {
        this.name = repositoryName;
    }

    /**
     * Returns the owner
     * @return String The owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner
     * @param owner The owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Returns the pullRequestChain
     * @return String The pullRequestChain
     */
    public String getPullRequestChain() {
        return pullRequestChain;
    }

    /**
     * Sets the pullRequestChain
     * @param pullRequestChain The pullRequestChain
     */
    public void setPullRequestChain(String pullRequestChain) {
        this.pullRequestChain = pullRequestChain;
    }

    /**
     * Returns, whether it is a fork or not
     * @return boolean True, if it is a fork
     */
    public boolean isFork() {
        return isFork;
    }

    /**
     * Sets the isFork
     * @param fork The isFork
     */
    public void setFork(boolean fork) {
        isFork = fork;
    }

    /**
     * Returns the number of forks
     * @return int The number of forks
     */
    public int getForks() {
        return forks;
    }

    /**
     * Sets the number of forks
     * @param forks The number of forks
     */
    public void setForks(int forks) {
        this.forks = forks;
    }

    /**
     * Returns the number of watchers
     * @return int The number of watchers
     */
    public int getWatchers() {
        return watchers;
    }

    /**
     * Sets the number of watchers
     * @param watchers The number of watchers
     */
    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    /**
     * Comparator for another Repository
     * @param o Another Repository
     * @return int The comparator value
     */
    @Override
    public int compareTo(FFRepository o) {
        return ((Integer) this.getForks()).compareTo(o.getForks());
    }
}