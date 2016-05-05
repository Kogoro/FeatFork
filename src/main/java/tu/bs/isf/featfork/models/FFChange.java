package tu.bs.isf.featfork.models;

/**
 * Created by Christopher Sontag
 */
public class FFChange {

    private String id;
    private String file;
    private String expression;

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
     * Returns the file
     *
     * @return String The file
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the file
     *
     * @param file The file
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Returns the expression
     * @return String The expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Sets the expression
     * @param expression The expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }
}
