package tu.bs.isf.featfork.exporter;

import tu.bs.isf.featfork.lib.FFDatabase;
import tu.bs.isf.featfork.models.FFCommit;
import tu.bs.isf.featfork.models.FFRepository;

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Christopher Sontag
 */
public class FFExporterHTML extends FFExporter {

    public static final File file = new File("res.html");
    private DecimalFormat df = new DecimalFormat("#.####");

    /**
     * Writes the database in a html file
     *
     * @param database The database instance
     * @param ratio    the ratio that should be reached
     */
    @Override
    public void write(FFDatabase database, double ratio) {
        System.out.print("Updating " + file.getName() + "... ");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>FeatFork</title></head><body>");
            for (FFRepository repos : database.getRepositories()) {
                out.append("<div style=\"border: 2px double black; background-color:lightgrey; padding: 5px;margin: 10px;\"><div id=\"namebar\"><b><a href=\"https://github.com/" + repos.getOwner() + "/" + repos.getName() + "\">" + repos.getName() + "</a></b> by " + repos.getOwner() + "</div>");
                for (FFCommit commit : database.getCommitsForRepoLeaving(repos.getId(), database.getMainRepository().getId())) {
                    HashMap<String, List<String>> changes = database.getChangesHashForCommit(commit.getId());
                    if (!changes.isEmpty() && database.existsImportantChangeSpecificForCommit(commit.getCommitHash(), ratio)) {
                        out.append("<div class=\"commit\" style=\"border: 2px solid black;background-color:white; margin-top: 10px;padding: 5px;\"><div style=\"width: 49%;float: left;\"><h4 style=\"margin-top: auto;\">Commit: <a href=\"https://github.com/" + repos.getOwner() + "/" + repos.getName() + "/commit/" + commit.getCommitHash() + "\">" + commit.getCommitHash() + "</a></h4></div><div style=\"width: 49%;float: right;text-align: right;\"><b>Committer: " + commit.getAuthor() + "</b></br>" + commit.getDate().toString() + "</div><div class=\"list\" style=\"clear: both\">");
                        for (String feature : changes.keySet()) {
                            double dRatio = database.getRatioSpecificForCommit(commit.getCommitHash(), feature);
                            if (dRatio > ratio) {
                                String strRatio = df.format(dRatio);
                                out.append("<div class=\"list-item\" style=\"border: 2px solid black;margin: 10px;\"><div style=\"border-bottom: 2px dashed black; padding: 5px;\"><b>" + feature + "</b><div style=\"float:right;text-align:right;\">Ratio: " + strRatio + "</div></div><div><ul>");
                                for (String change : changes.get(feature)) {
                                    if (change != null && !change.isEmpty())
                                        out.append("<li><a href=\"https://github.com/" + repos.getOwner() + "/" + repos.getName() + "/blob/" + commit.getBranch().replace("refs/heads/", "") + "/" + change + "\">" + change + "</a></li>");
                                }
                                out.append("</ul></div></div>");
                            }

                        }
                        out.append("</div></div>");
                    }
                }
                out.append("</div></div></body></html>");
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
