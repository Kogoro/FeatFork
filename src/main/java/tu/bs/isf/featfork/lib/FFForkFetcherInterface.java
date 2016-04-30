package tu.bs.isf.featfork.lib;

import java.util.List;

/**
 * Created by Christopher Sontag on 22.04.2016.
 */
public abstract class FFForkFetcherInterface {

    public int searchedForks = 0, cleanForks = 0;
    protected String username, repositoryName;
    protected int maxCount = 0, tempCount = 0, maxLevel = 0;
    protected Database database;

    public FFForkFetcherInterface(String username, String repositoryName, int maxCount, int maxLevel) {
        this.username = username;
        this.repositoryName = repositoryName;
        this.maxCount = maxCount;
        this.tempCount = maxCount;
        this.maxLevel = maxLevel;
        this.database = new Database();
    }

    public abstract List<String> getForkURLs();

    public abstract String getMainURL();

}
