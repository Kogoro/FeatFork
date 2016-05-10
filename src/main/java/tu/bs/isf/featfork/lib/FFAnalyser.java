package tu.bs.isf.featfork.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Christopher Sontag
 */
public class FFAnalyser {

    private int directivesSuccess, directivesAll;
    private HashMap<String, Integer> featureRatios = new HashMap<>();

    /**
     * Analyses a given string for preprocessor-directives
     *
     * @param str The string, which should be analyzed
     * @return List<String> A list of features
     */
    public List<String> analyse(String str) {
        List<String> strings = new ArrayList<>();

        if (str.contains("+") && str.contains("#") && !str.contains("//")) {

            //Remove the plus sign, the comments and unimportant whitespaces
            str = str.replace("+", "").replace("defined", "").replace("ENABLED", "").replace("DISABLED", "").replaceFirst("\\s?\\/\\/.*", "");

            //Remove Brackets
            str = str.replace("(", "").replace(")", "");

            //Remove !
            str = str.replace("!", "");

            //Remove whitespaces
            while (str.indexOf(" ") != str.lastIndexOf(" ") && str.contains(" ")) {
                str = str.replaceFirst(" ", "");
            }

            str = str.trim();

            // Identify Type and extract Expression
            if (str.contains("#ifdef")) {
                strings.addAll(getFeatures(str.replace("#ifdef", "").replace("#ifndef", "").trim()));
                directivesSuccess++;
            } else if (str.contains("#ifndef")) {
                strings.addAll(getFeatures(str.replace("#ifndef", "").trim()));
                directivesSuccess++;
            } else if (str.contains("#endif")) {
                strings.addAll(getFeatures(str.replace("#endif", "").trim()));
                directivesSuccess++;
            } else if ((str.contains("#if") || str.contains("#elif")) && (str.contains("&") || str.contains("|"))) {
                strings.addAll(getFeatures(str.replace("#if", "").replace("#elif", "").trim()));
                directivesSuccess++;
            } else if ((str.contains("#if") || str.contains("#elif")) && !(str.contains("&") || str.contains("|"))) {
                strings.addAll(getFeatures(str.replace("#if", "").replace("#elif", "").trim()));
                directivesSuccess++;
            } else if (str.contains("#define")) {
                //strings.addAll(getFeatures(str.replace("#define", "").trim()));
            } else if (str.contains("#undef")) {
                //strings.addAll(getFeatures(str.replace("#undef", "").trim()));
            } else if (str.contains("#else")) {
                //strings.addAll(getFeatures(str.replace("#else", "").trim()));
                directivesSuccess++;
            } else if (str.contains("#include")) {
                //strings.addAll(getFeatures(str.replace("#include", "").trim()));
            } else if (str.contains("#error")) {
                //strings.addAll(getFeatures(str.replace("#error", "").trim()));
            } else {
                //UNKNOWN
            }
            directivesAll++;
        }
        System.out.println("Directivessize: " + strings.size());
        return strings;
    }

    /**
     * Splits a expression in different features
     *
     * @param str The Expression
     * @return List<String> The list of features
     */
    private List<String> getFeatures(String str) {
        List<String> strings = new ArrayList<>();
        strings.addAll(Arrays.asList(str.split("(&&|\\|\\|)")));
        strings.removeAll(Arrays.asList("", null));
        for (String feature : strings) {
            feature = feature.trim();
            if (!featureRatios.containsKey(feature)) {
                featureRatios.put(feature, 1);
            } else {
                featureRatios.put(feature, featureRatios.get(feature) + 1);
            }
        }
        return strings;
    }

    /**
     * Returns the overall ratio for a commit
     *
     * @return double The ratio overall
     */
    public double getRatioOverall() {
        System.out.println("Important directives: " + directivesSuccess + " All directives: " + directivesAll);
        if (directivesAll > 0)
            return directivesSuccess / directivesAll;
        return 0;
    }

    /**
     * Returns the specific ratio for a specific feature in a commit
     *
     * @return HashMap The map with the features and their ratio
     */
    public HashMap<String, Double> getRatiosSpecific() {
        HashMap<String, Double> temp = new HashMap<>();
        if (directivesAll > 0) {
            for (String key : featureRatios.keySet()) {
                temp.put(key, (double) featureRatios.get(key) / directivesAll);
                System.out.println("Feature: " + key + " occured " + directivesSuccess + " time. All directives: " + directivesAll);
            }
        }
        return temp;
    }

    /**
     * Resets all ratios
     */
    public void resetRatio() {
        directivesSuccess = 0;
        directivesAll = 0;
        featureRatios.clear();
    }
}
