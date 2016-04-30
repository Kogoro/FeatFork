package tu.bs.isf.featfork.exporter;

import tu.bs.isf.featfork.lib.Database;
import tu.bs.isf.featfork.models.FFChange;
import tu.bs.isf.featfork.models.FFCommit;
import tu.bs.isf.featfork.models.FFRepository;

import java.io.*;
import java.util.List;

/**
 * Created by Christopher Sontag on 30.04.2016.
 */
public class FFExporterCSV extends FFExporter {

    public static final File file = new File("res.csv");

    @Override
    public void write(Database database) {
        System.out.print("Updating " + file.getName() + "... ");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (FFRepository repos : database.getRepositories()) {
                out.append(repos.getOwner() + "/" + repos.getName() + ";\n");
                for (FFCommit commit : database.getCommitsForRepoLeaving(repos.getId(), (int) database.getMainRepository().getId())) {
                    List<FFChange> changes = database.getChangesForCommit(commit.getId());
                    if (!changes.isEmpty()) {
                        out.append(";" + commit.getCommitHash() + ";\n");
                        for (FFChange change : changes) {
                            if (!change.getExpression().isEmpty())
                                out.append(";;" + change.getFile() + ";" + change.getExpression() + ";\n");
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
