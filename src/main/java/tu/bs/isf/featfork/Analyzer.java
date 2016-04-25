package tu.bs.isf.featfork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Christopher Sontag on 22.04.2016.
 */
public class Analyzer {

    int directivesSuccess, directivesAll;
    private HashMap<String, Integer> featureRatios;

    public List<String> analyse(String str) {
        List<String> strings = new ArrayList<>();

        if (str.contains("+") && str.contains("#")) {
            System.out.println(str);
            //Remove the plus sign, the comments and unimportant whitespaces
            str = str.replace("+", "").replace("defined", "").replaceFirst("\\s?\\/\\/.*", "").trim();
            //Remove Brackets
            //str = str.replace("(", "").replace(")", "");
            while (str.indexOf(" ") != str.lastIndexOf(" ") && str.indexOf(" ") != -1) {
                str = str.replaceFirst(" ", "");
            }

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
            } /*else if (str.contains("#define")) {
                //strings.addAll(getFeatures(str.replace("#define", "").trim()));
            } else if (str.contains("#undef")) {
                //strings.addAll(getFeatures(str.replace("#undef", "").trim()));
            } else if (str.contains("#else")) {
                //strings.addAll(getFeatures(str.replace("#else", "").trim()));
            } else if (str.contains("#include")) {
                //strings.addAll(getFeatures(str.replace("#include", "").trim()));
            } else if (str.contains("#error")) {
                //strings.addAll(getFeatures(str.replace("#error", "").trim()));
            } else {
                //UNKNOWN
            }*/
            directivesAll++;
        }
        System.out.println("Directivessize: " + strings.size());
        return strings;
    }

    private List<String> getFeatures(String str) {
        List<String> strings = new ArrayList<>();
        strings.addAll(Arrays.asList(str.split("(&&|\\|\\|)")));
        for (String feature : strings) {
            if (!featureRatios.containsKey(feature)) {
                featureRatios.put(feature, 1);
            } else {
                featureRatios.put(feature, featureRatios.get(feature) + 1);
            }
        }
        return strings;
    }

    public double getRatioOverall() {
        System.out.println("Important directives: " + directivesSuccess + " All directives: " + directivesAll);
        if (directivesAll > 0)
            return directivesSuccess / directivesAll;
        return 0;
    }

    public HashMap<String, Double> getRatiosSpecific() {
        HashMap<String, Double> temp = new HashMap<>();
        if (directivesAll > 0) {
            for (String key : featureRatios.keySet()) {
                temp.put(key, (double) featureRatios.get(key) / directivesAll);
                System.out.println("Feature: " + key + " occured " + directivesSuccess + "time. All directives: " + directivesAll);
            }
        }
        return temp;
    }

    public void resetRatio() {
        directivesSuccess = 0;
        directivesAll = 0;
    }
}
