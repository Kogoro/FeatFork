package tu.bs.isf.featfork.lib;

import java.util.List;

/**
 * Created by Christopher Sontag
 */
public abstract class FFForkFetcherInterface {

    public int searchedForks = 0, cleanForks = 0;
    protected String username, repositoryName;
    protected int maxCount = 0, tempCount = 0, maxLevel = 0;
    protected FFDatabase database;

    /**
     * Constructor
     *
     * @param username       The username of the main repository
     * @param repositoryName The repositoryname of the main repository
     * @param maxCount       The number of maximal forks gathered
     * @param maxLevel       The number of maximal levels searched
     */
    public FFForkFetcherInterface(String username, String repositoryName, int maxCount, int maxLevel) {
        this.username = username;
        this.repositoryName = repositoryName;
        this.maxCount = maxCount;
        this.tempCount = maxCount;
        this.maxLevel = maxLevel;
        this.database = new FFDatabase();
    }

    /**
     * Returns the list of forks URLs
     *
     * @return List<String> The fork URLs
     */
    public abstract List<String> getForkURLs();

    /**
     * Returns the main URL
     *
     * @return String The main URL
     */
    public abstract String getMainURL();

}
