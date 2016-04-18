package tu.bs.isf.featfork;

import org.eclipse.jgit.diff.DiffEntry;

/**
 * Created by Christopher Sontag on 31.03.2016.
 */
public class FFChange {

    private String id;
    private String file;
    private String expression;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
