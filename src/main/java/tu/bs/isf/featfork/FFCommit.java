package tu.bs.isf.featfork;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<FFChange> changes = new ArrayList<>();

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

    public List<FFChange> getChanges() {
        return changes;
    }

    public void setChanges(List<FFChange> changes) {
        this.changes = changes;
    }
}
