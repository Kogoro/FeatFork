package tu.bs.isf.featfork;

/**
 * Created by Christopher Sontag on 24.04.2016.
 */
public class FFRatioSpecific {

    private String commitID;
    private String feature;
    private double ratio;

    public String getCommitID() {
        return commitID;
    }

    public void setCommitID(String commitID) {
        this.commitID = commitID;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
