package tu.bs.isf.featfork.models;

import java.util.Date;

/**
 * Created by Christopher Sontag on 31.03.2016.
 */
public class FFCommit {

    private String id;
    private String name = "";
    private String commitHash = "";
    private String branch = "";
    private Date date;
    private String author = "";

    public FFCommit(String id, String name, String commitHash, String branch, Date date, String author) {
        this.id = id;
        this.name = name;
        this.commitHash = commitHash;
        this.branch = branch;
        this.date = date;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
