package tu.bs.isf.featfork.lib;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Christopher Sontag
 */
public abstract class FFVCSInterface {

    protected List<String> fileEndings, blackList;
    protected FFDatabase database;
    protected FFAnalyser analyzer;
    protected String mainRepository;

    /**
     * Constructor
     *
     * @param fileEndings The list of allowed file endings
     * @param blackList   The list of illegal files
     */
    public FFVCSInterface(List<String> fileEndings, List<String> blackList) {
        this.fileEndings = fileEndings;
        this.blackList = blackList;
    }

    /**
     * Starts the forking process for multiple urls
     *
     * @param urls The cloneURLs of the forks
     */
    public abstract void startForks(List<String> urls);

    /**
     * Start the gathering of the main repository
     *
     * @param url The URL of the main repository
     */
    public abstract void startMain(String url);

    /**
     * The generalized gathering method
     *
     * @param url The URL, which should be gathered
     */
    protected abstract void run(String url);

    /**
     * Downloads the repository under the given URL to the given path
     * @param cloneURL The cloneURL
     * @param savePath The path, where the repository should be saved
     */
    protected abstract void downloadRepository(String cloneURL, File savePath);

    /**
     * Extraction of the commits for a given repository
     * @param file The folder for the repository, which should be analyzed
     */
    protected abstract void getCommitsForRepository(File file) throws IOException;

    /**
     * Extract the changes from each commit
     * @param file The folder for the repository, which should be analyzed
     */
    protected abstract void getChangesForCommit(File file);

    /**
     * Returns the main repository URL
     * @return String The main repository URL
     */
    public String getMainRepository() {
        return mainRepository;
    }

    /**
     * Sets the main repository URL
     * @param mainRepository The main repository URL
     */
    public void setMainRepository(String mainRepository) {
        this.mainRepository = mainRepository;
    }
}
