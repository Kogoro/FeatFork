package tu.bs.isf.featfork.exporter;

import tu.bs.isf.featfork.lib.FFDatabase;
import tu.bs.isf.featfork.models.FFChange;
import tu.bs.isf.featfork.models.FFCommit;
import tu.bs.isf.featfork.models.FFRepository;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Christopher Sontag
 */
public class FFExporterCSV extends FFExporter {

    public static final File file = new File("res.csv");
    private DecimalFormat df = new DecimalFormat("#.####");

    /**
     * Writes the database in a csv file
     *
     * @param database The database instance
     * @param ratio    the ratio that should be reached
     */
    @Override
    public void write(FFDatabase database, double ratio) {
        System.out.print("Updating " + file.getName() + "... ");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (FFRepository repos : database.getRepositories()) {
                out.append(repos.getOwner() + "/" + repos.getName() + ";\n");
                for (FFCommit commit : database.getCommitsForRepoLeaving(repos.getId(), database.getMainRepository().getId())) {
                    List<FFChange> changes = database.getChangesForCommit(commit.getId());
                    if (!changes.isEmpty() && database.existsImportantChangeSpecificForCommit(commit.getCommitHash(), ratio)) {
                        out.append(";" + commit.getCommitHash() + ";\n");
                        for (FFChange change : changes) {
                            double dRatio = database.getRatioSpecificForCommit(commit.getCommitHash(), change.getExpression());
                            if (dRatio > ratio) {
                                String strRatio = df.format(dRatio);
                                if (!change.getExpression().isEmpty())
                                    out.append(";;" + change.getFile() + ";" + change.getExpression() + ";" + strRatio + ";\n");
                            }
                        }
                    }
                }
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Not found " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("IOException for " + file.getAbsolutePath());
        }
        System.out.println("done.");
    }
}
