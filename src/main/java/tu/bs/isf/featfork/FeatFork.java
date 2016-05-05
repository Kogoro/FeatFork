package tu.bs.isf.featfork;

import tu.bs.isf.featfork.exporter.FFExporter;
import tu.bs.isf.featfork.exporter.FFExporterCSV;
import tu.bs.isf.featfork.exporter.FFExporterHTML;
import tu.bs.isf.featfork.lib.FFDatabase;
import tu.bs.isf.featfork.lib.FFGit;
import tu.bs.isf.featfork.lib.FFGithubForkFetcher;
import tu.bs.isf.featfork.lib.FFWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Christopher Sontag
 */
public class FeatFork {
    public static final String PATH = "repos/";

    /**
     * Main method
     *
     * @param args The start parameter (not used right now)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FFDatabase FFDatabase = new FFDatabase();

        //CPP File Extensions
        List<String> fileEnding = new ArrayList<>();
        fileEnding.add(".c");
        fileEnding.add(".h");
        fileEnding.add(".i");
        fileEnding.add(".ii");
        fileEnding.add(".C");
        fileEnding.add(".c++");
        fileEnding.add(".cc");
        fileEnding.add(".cp");
        fileEnding.add(".cxx");
        fileEnding.add(".cpp");
        fileEnding.add(".CPP");
        fileEnding.add(".H");
        fileEnding.add(".h++");
        fileEnding.add(".hh");
        fileEnding.add(".hp");
        fileEnding.add(".hxx");
        fileEnding.add(".hpp");
        fileEnding.add(".HPP");
        fileEnding.add(".ixx");
        fileEnding.add(".ipp");
        fileEnding.add(".inl");
        fileEnding.add(".txx");
        fileEnding.add(".tpp");
        fileEnding.add(".tpl");
        fileEnding.add(".tcc");

        List<String> blackList = new ArrayList<>();
        blackList.add("LinuxAddons/bin");
        blackList.add("language");
        blackList.add("Documentation");

        // Ask for repository
        System.out.println("Please type in the username:");
        String username = scanner.nextLine();
        System.out.println("Please type in the repository:");
        String reponame = scanner.nextLine();

        if (!FFDatabase.exists()) {
            System.out.println("How many forks of the first level should be analyzed?:");
            int maxrepo = scanner.nextInt();
            System.out.println("How many levels should be analyzed?:");
            int maxlevel = scanner.nextInt();
            start(username, reponame, maxrepo, maxlevel, fileEnding, blackList);
        } else {
            System.out.println("Should the database overwritten? (true/false):");
            boolean overwrite = scanner.nextBoolean();
            if (overwrite) {
                FFDatabase.drop();
                FFDatabase.createTables();
                System.out.println("How many forks of the first level should be analyzed?:");
                int maxrepo = scanner.nextInt();
                System.out.println("How many levels should be analyzed?:");
                int maxlevel = scanner.nextInt();
                start(username, reponame, maxrepo, maxlevel, fileEnding, blackList);
            }
        }
        System.out.println("Which file format should be used for the export? (1 -> CSV,2 -> HTML):");
        int exportType = scanner.nextInt();
        System.out.println("Which ratio for important changes should be used? (0-1):");
        double ratio = scanner.nextDouble();
        FFExporter ffExporter;
        switch (exportType) {
            case 1:
                ffExporter = new FFExporterCSV();
                break;
            case 2:
                ffExporter = new FFExporterHTML();
                break;
            default:
                ffExporter = new FFExporterCSV();
        }
        ffExporter.write(FFDatabase, ratio);
    }

    /**
     * Starts the gathering process
     *
     * @param username   The username of the main repository owner
     * @param reponame   The repositoryname of the main repository
     * @param maxrepo    The number of maximal forks gathered
     * @param maxlevel   The number of maximal levels searched
     * @param fileEnding The list of file endings that are allowed
     * @param blackList  The list of illegal files
     */
    public static void start(String username, String reponame, int maxrepo, int maxlevel, List<String> fileEnding, List<String> blackList) {
        FFGit ffGit = new FFGit(fileEnding, blackList);
        FFGithubForkFetcher ffGithubForkFetcher = new FFGithubForkFetcher("MarlinFirmware", "Marlin", maxrepo, maxlevel);
        FFWrapper wrapper = new FFWrapper(ffGit, ffGithubForkFetcher);
        wrapper.start();
        System.out.println("Searched Forks: " + ffGithubForkFetcher.searchedForks + " - Clean Forks: " + ffGithubForkFetcher.cleanForks);

    }
}
