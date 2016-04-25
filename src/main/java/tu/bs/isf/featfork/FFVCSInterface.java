package tu.bs.isf.featfork;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Christopher Sontag on 22.04.2016.
 */
public abstract class FFVCSInterface {

    protected List<String> fileEndings, blackList;
    Database database;
    Analyzer analyzer;
    private String mainRepository;

    public FFVCSInterface(List<String> fileEndings, List<String> blackList) {
        this.fileEndings = fileEndings;
        this.blackList = blackList;
    }

    public abstract void start(List<String> urls);

    public abstract void start(String url);

    protected abstract void run(String url);

    protected abstract void downloadRepository(String cloneURL, File savePath);

    protected abstract void getCommitsForRepository(File file) throws IOException;

    protected abstract void getChangesForCommit(File file);

    public void setMainRepository(String mainRepository) {
        this.mainRepository = mainRepository;
    }

    public String getMainRepository() {
        return mainRepository;
    }
}
