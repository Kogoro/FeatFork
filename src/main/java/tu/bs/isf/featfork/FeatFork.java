package tu.bs.isf.featfork;

import tu.bs.isf.featfork.exporter.FFExporter;
import tu.bs.isf.featfork.exporter.FFExporterCSV;
import tu.bs.isf.featfork.exporter.FFExporterHTML;
import tu.bs.isf.featfork.lib.Database;
import tu.bs.isf.featfork.lib.FFGit;
import tu.bs.isf.featfork.lib.FFGithubForkFetcher;
import tu.bs.isf.featfork.lib.FFWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Christopher Sontag on 31.03.2016.
 */
public class FeatFork {
    public static final String PATH = "repos/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Database database = new Database();

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
        blackList.add("font_data.c");
        blackList.add("LinuxAddons/bin");
        blackList.add("language");
        blackList.add("Documentation");

        // Ask for repository
        System.out.println("Please type in the username:");
        String username = scanner.nextLine();
        System.out.println("Please type in the repository:");
        String reponame = scanner.nextLine();

        if (!database.exists()) {
            System.out.println("How many forks of the first level should be analyzed?:");
            int maxrepo = scanner.nextInt();
            System.out.println("How many levels should be analyzed?:");
            int maxlevel = scanner.nextInt();
            start(username, reponame, maxrepo, maxlevel, fileEnding, blackList);
        } else {
            System.out.println("Should the database overwritten? (true/false):");
            boolean overwrite = scanner.nextBoolean();
            if (overwrite) {
                database.drop();
                database.createTables();
                System.out.println("How many forks of the first level should be analyzed?:");
                int maxrepo = scanner.nextInt();
                System.out.println("How many levels should be analyzed?:");
                int maxlevel = scanner.nextInt();
                start(username, reponame, maxrepo, maxlevel, fileEnding, blackList);
            }
        }
        System.out.println("Which file format should be used for the export? (1 -> CSV,2 -> HTML):");
        int exportType = scanner.nextInt();
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
        ffExporter.write(database);
    }

    public static void start(String username, String reponame, int maxrepo, int maxlevel, List<String> fileEnding, List<String> blackList) {
        FFGit ffGit = new FFGit(fileEnding, blackList);
        FFGithubForkFetcher ffGithubForkFetcher = new FFGithubForkFetcher("MarlinFirmware", "Marlin", maxrepo, maxlevel);
        FFWrapper wrapper = new FFWrapper(ffGit, ffGithubForkFetcher);
        wrapper.start();
        System.out.println("Searched Forks: " + ffGithubForkFetcher.searchedForks + " - Clean Forks: " + ffGithubForkFetcher.cleanForks);

    }
}
