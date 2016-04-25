package tu.bs.isf.featfork;

import java.util.List;

/**
 * Created by Christopher Sontag on 22.04.2016.
 */
public abstract class FFForkFetcherInterface {

    protected String username, repositoryName;
    protected int maxCount = 0, maxLevel = 0;
    protected Database database;
    public int searchedForks = 0, cleanForks = 0;

    public FFForkFetcherInterface(String username, String repositoryName, int maxCount, int maxLevel) {
        this.username = username;
        this.repositoryName = repositoryName;
        this.maxCount = maxCount;
        this.maxLevel = maxLevel;
        this.database = new Database();
    }

    public abstract List<String> getForkURLs();

    public abstract String getMainURL();

}
