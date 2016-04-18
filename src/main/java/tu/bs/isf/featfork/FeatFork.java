package tu.bs.isf.featfork;

import org.sql2o.*;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Christopher Sontag on 31.03.2016.
 */
public class FeatFork {
    public static final String PATH = "repos/";
    public static final File file = new File("res.csv");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FFWrapper wrapper = new FFWrapper();

        System.out.println("Please type in the username:");
        String username = scanner.nextLine();
        System.out.println("Please type in the repository:");
        String reponame = scanner.nextLine();
        wrapper.setMain("MarlinFirmware", "Marlin");

        if (!wrapper.database.exists()) {
            System.out.println("How many forks of the first level should be analyzed?:");
            int maxrepo = scanner.nextInt();
            start(wrapper, maxrepo);
        } else {
            System.out.println("Should the database overwritten? (true/false):");
            boolean overwrite = scanner.nextBoolean();
            if (overwrite) {
                System.out.println("How many forks of the first level should be analyzed?:");
                int maxrepo = scanner.nextInt();
                wrapper.database.drop();
                wrapper.database.createTables();
                start(wrapper, maxrepo);
            }
        }
        System.out.println("Searched Forks: " + wrapper.searchedForks + " - Clean Forks: " + wrapper.cleanForks);
        //writeToCSV(wrapper);
    }

    public static void start(FFWrapper wrapper, int maxrepo) {
        try {
            wrapper.getRepositoryAndForks(maxrepo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToCSV(FFWrapper wrapper) {
        System.out.print("Updating " + file.getName() + "... ");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (FFRepository repos : wrapper.database.getRepositories()) {
                out.append(repos.getOwner() + "/" + repos.getName() + ";\n");
                for (FFCommit commit : wrapper.database.getCommitsForRepoLeaving(repos.getId(), (int) wrapper.getMain().getId())) {
                    List<FFChange> changes = wrapper.database.getChangesForCommit(commit.getId());
                    if (!changes.isEmpty()) {
                        out.append(";" + commit.getCommitHash() + ";\n");
                        for (FFChange change : changes) {
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
