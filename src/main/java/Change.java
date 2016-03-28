import org.eclipse.jgit.diff.DiffEntry;

/**
 * Created by Christopher Sontag on 27.03.2016.
 */
public class Change {

    public enum ExpressionType {IFDEF, IFNDEF, ENDIF, IF, DEF, UNDEF, ELSE, UNKNOWN}

    private String file;
    private String expression;
    private String rawExpression;
    private ExpressionType expressionType;
    private DiffEntry.ChangeType changeType;

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

    public String getRawExpression() {
        return rawExpression;
    }

    public void setRawExpression(String rawExpression) {
        this.rawExpression = rawExpression;
    }

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public String getExpressionTypeStr() {
        switch (getExpressionType()) {
            case IFDEF:
                return "#ifdef";
            case IFNDEF:
                return "#ifndef";
            case ENDIF:
                return "#endif";
            case IF:
                return "#if";
            case DEF:
                return "#define";
            case UNDEF:
                return "#undef";
            case ELSE:
                return "#else";
            case UNKNOWN:
                return "";
        }
        return "";
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public DiffEntry.ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(DiffEntry.ChangeType changeType) {
        this.changeType = changeType;
    }


}

