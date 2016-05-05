package tu.bs.isf.featfork.lib;

import java.util.List;

/**
 * Created by Christopher Sontag
 */
public class FFWrapper {

    private FFVCSInterface ffvcs;
    private FFForkFetcherInterface ffForkFetcher;

    /**
     * Constructor
     *
     * @param ffvcs         The FFVCSInterface implementation
     * @param ffForkFetcher The FFForkFetcherInterface implementation
     */
    public FFWrapper(FFVCSInterface ffvcs, FFForkFetcherInterface ffForkFetcher) {
        this.ffvcs = ffvcs;
        this.ffForkFetcher = ffForkFetcher;
    }

    /**
     * Starts and manages the gathering process
     */
    public void start() {
        System.out.println("Start gathering the main repository ... ");
        String mainURL = ffForkFetcher.getMainURL();
        if (!mainURL.equals("")) {
            ffvcs.setMainRepository(mainURL);
            ffvcs.startMain(mainURL);
            System.out.println("Start gathering the fork repositories ... ");
            List<String> forkURLs = ffForkFetcher.getForkURLs();
            System.out.println(forkURLs.toString());
            ffvcs.startForks(forkURLs);
        } else {
            System.out.println("Error: Could not initiate main repository through a error before.");
        }
    }
}

