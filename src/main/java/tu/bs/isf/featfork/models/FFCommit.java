package tu.bs.isf.featfork.models;

import java.util.Date;

/**
 * Created by Christopher Sontag
 */
public class FFCommit {

    private String id;
    private String name = "";
    private String commitHash = "";
    private String branch = "";
    private Date date;
    private String author = "";

    /**
     * Constructor
     *
     * @param id         The commit id
     * @param name       The commit name
     * @param commitHash The commit hash
     * @param branch     The commit branch
     * @param date       The commit date
     * @param author     The commit author
     */
    public FFCommit(String id, String name, String commitHash, String branch, Date date, String author) {
        this.id = id;
        this.name = name;
        this.commitHash = commitHash;
        this.branch = branch;
        this.date = date;
        this.author = author;
    }

    /**
     * Returns the ID
     *
     * @return String The id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID
     *
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the name
     *
     * @return String The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the commit hash
     * @return The commit hash
     */
    public String getCommitHash() {
        return commitHash;
    }

    /**
     * Sets the commit hash
     * @param commitHash The commit hash
     */
    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    /**
     * Returns the branch name
     * @return String The branch name
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Sets the branch name
     * @param branch The branch name
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * Returns the date
     * @return Date The date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date
     * @param date The date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Returns the author
     * @return String The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author
     * @param author The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

}
