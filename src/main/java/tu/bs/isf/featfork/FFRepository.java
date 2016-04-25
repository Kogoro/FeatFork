package tu.bs.isf.featfork;

import org.eclipse.egit.github.core.Repository;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Christopher Sontag on 31.03.2016.
 */
public class FFRepository implements Comparable<FFRepository> {

    private int id;
    private String name = "";
    private String owner = "";
    private String pullRequestChain = "";
    private boolean isFork = false;
    private int forks = 0;
    private int watchers = 0;

    public FFRepository(int id, String name, String owner, String pullRequestChain, boolean isFork, int forks, int watchers) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.pullRequestChain = pullRequestChain;
        this.isFork = isFork;
        this.forks = forks;
        this.watchers = watchers;
    }

    public FFRepository(Repository repository) {
        this.isFork = repository.isFork();
        this.name = repository.getName();
        this.forks = repository.getForks();
        this.id = (int) repository.getId();
        this.name = repository.getName();
        this.owner = repository.getOwner().getLogin();
        this.pullRequestChain = "MAIN -> THIS";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String repositoryName) {
        this.name = repositoryName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPullRequestChain() {
        return pullRequestChain;
    }

    public void setPullRequestChain(String pullRequestChain) {
        this.pullRequestChain = pullRequestChain;
    }

    public boolean isFork() {
        return isFork;
    }

    public void setFork(boolean fork) {
        isFork = fork;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    @Override
    public int compareTo(FFRepository o) {
        return ((Integer) this.getForks()).compareTo(o.getForks());
    }
}