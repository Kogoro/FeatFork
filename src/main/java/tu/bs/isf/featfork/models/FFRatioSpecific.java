package tu.bs.isf.featfork.models;

/**
 * Created by Christopher Sontag
 */
public class FFRatioSpecific {

    private String commitID;
    private String feature;
    private double ratio;

    /**
     * Returns the ID
     *
     * @return String The id
     */
    public String getCommitID() {
        return commitID;
    }

    /**
     * Sets the ID
     *
     * @param commitID The id
     */
    public void setCommitID(String commitID) {
        this.commitID = commitID;
    }

    /**
     * Returns the feature name
     *
     * @return String The feature name
     */
    public String getFeature() {
        return feature;
    }

    /**
     * Sets the feature name
     *
     * @param feature The feature name
     */
    public void setFeature(String feature) {
        this.feature = feature;
    }

    /**
     * Returns the ratio
     * @return double The ratio
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * Sets the ratio
     * @param ratio The ratio
     */
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
