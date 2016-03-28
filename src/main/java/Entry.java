import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Christopher Sontag on 24.03.2016.
 */
public class Entry {

    private String repositoryName;
    private Date date;
    private List<Change> changes;
    private String pullRequestChain;
    private String commitHash;
    private int forks;
    private String author;

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    public void addChange(Change changes) {
        this.changes.add(changes);
    }

    public void addAllChanges(List<Change> changes) {
        this.changes.addAll(changes);
    }

    public String getPullRequestChain() {
        return pullRequestChain;
    }

    public void setPullRequestChain(String pullRequestChain) {
        this.pullRequestChain = pullRequestChain;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Entry{" +
                repositoryName + '\'' +
                ", date=" + date +
                ", pullRequestChain='" + pullRequestChain + '\'' +
                ", forks=" + forks +
                ", author='" + author + '\'' +
                '}';
    }

    public CharSequence getCSVString() {
        StringBuilder builder = new StringBuilder();
        builder.append(repositoryName + ";\n");
        for (Change change : changes) {
            //System.out.println(change.getExpressionTypeStr() + ": " + change.getExpression());
            //System.out.println(!(change.getExpressionType().equals(Change.ExpressionType.ELSE) || change.getExpressionType().equals(Change.ExpressionType.ENDIF) || change.getExpressionType().equals(Change.ExpressionType.UNKNOWN)));
            if (!(change.getExpressionType().equals(Change.ExpressionType.ELSE) || change.getExpressionType().equals(Change.ExpressionType.ENDIF) || change.getExpressionType().equals(Change.ExpressionType.UNKNOWN))) {
                builder.append(date + ";" + change.getExpressionTypeStr() + ": " + change.getExpression() + ";" + forks + ";" + pullRequestChain + ";" + commitHash + ";" + author + ";\n");
            }
        }
        return builder.toString();
    }
}
