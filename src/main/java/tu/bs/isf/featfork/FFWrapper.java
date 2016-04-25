package tu.bs.isf.featfork;

import java.util.List;

/**
 * Created by Christopher Sontag on 31.03.2016.
 */
public class FFWrapper {

    private FFVCSInterface ffvcs;
    private FFForkFetcherInterface ffForkFetcher;


    public FFWrapper(FFVCSInterface ffvcs, FFForkFetcherInterface ffForkFetcher) {
        this.ffvcs = ffvcs;
        this.ffForkFetcher = ffForkFetcher;
    }

    public void start() {
        System.out.println("Start gathering the main repository ... ");
        String mainURL = ffForkFetcher.getMainURL();
        if (mainURL != "") {
            ffvcs.setMainRepository(mainURL);
            ffvcs.start(mainURL);
            System.out.println("Start gathering the fork repositories ... ");
            List<String> forkURLs = ffForkFetcher.getForkURLs();
            ffvcs.start(forkURLs);
        } else {
            System.out.println("Error: Could not initiate main repository through a error before.");
        }
    }


}

