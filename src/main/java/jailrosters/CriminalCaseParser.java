package jailrosters;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nehaojha on 23/05/18.
 */
public class CriminalCaseParser {

    private static final Logger LOGGER = Logger.getLogger(CriminalCaseParser.class);

    public static void main(String[] args) throws IOException {
        LOGGER.info("case number,case,case type,date filed,location,Cr.Trntrs,Defendant,Male,White,height,weight,Attorneys,Court Appointed,Charge 1,Charge 2,statute 1,statute 2,level 1,level 2,date 1,date 2,Disposition 1,judicial officer disposition 1,disposition charge 1,disposition 1 outcome,disposition 1 date,Sentence 1,judicial officer sentence 1,sentence charge 1,sentence 1 outcome 1,sentence 1 confinment,sentence 1 suspension,sentence 1 confinement date,sentence 1 provisions, request for counsel date, magistrate warning date");
        String rootFolder = "/Users/nehaojha/Documents/gillespie";
        File[] directories = new File(rootFolder).listFiles(File::isDirectory);
        Set<String> uniqueDefendants = new HashSet<>();
//        outer:
        for (int i = 0; i < directories.length; i++) {
            File[] files = new File(String.valueOf(directories[i])).listFiles();
            for (File file : files) {
                if (file.isFile() && !file.getName().equals(".DS_Store")) {
                    Document document = Jsoup.parse(file, "UTF-8");
                    System.out.println("file = " + file.getAbsolutePath());
//                    parseDocument(document);
                    uniqueDefendants.add(sanitize(getDefendant(document)));
//                    break outer;
                }
            }
        }
        System.out.println("total = " + uniqueDefendants);
    }

    private static void parseDocument(Document document) {
        try {
            StringBuilder info = new StringBuilder();
            info.append(sanitize(document.select("body > div.ssCaseDetailCaseNbr > span").text())).append(",")
                    .append(sanitize(document.select("body > table:nth-child(6) > tbody > tr > td:nth-child(1) > b").text())).append(",")
                    .append(sanitize(getCaseType(document))).append(",")
                    .append(sanitize(document.select("body > table:nth-child(6) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td > b").text())).append(",")
                    .append(sanitize(document.select("body > table:nth-child(6) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > table > tbody > tr:nth-child(3) > td > b").text())).append(",")
                    .append(sanitize(getTrntrs(document))).append(",")
                    .append(sanitize(getDefendant(document))).append(",")
                    .append(sanitize("" + (document.text().contains("Male") ? 1 : 0))).append(",")
                    .append(sanitize("" + (document.text().contains("Male") ? 1 : 0))).append(",")
                    .append(getHeightWeight(document)).append(",")
                    .append(sanitize(getAttorney(document))).append(",")
                    .append("" + (document.select("body > table:nth-child(7) > tbody > tr:nth-child(2) > td:nth-child(5) > i:nth-child(3)").text().toLowerCase().contains("appointed") ? 1 : 0)).append(",")
                    .append(sanitize(getCharge1(document))).append(",")
                    .append(sanitize(getCharge2(document))).append(",")
                    .append(sanitize(getStatue1(document))).append(",")
                    .append(sanitize(getStatue2(document))).append(",")
                    .append(sanitize(getLevel1(document))).append(",")
                    .append(sanitize(getLevel2(document))).append(",")
                    .append(sanitize(getDate1(document))).append(",")
                    .append(sanitize(getDate2(document))).append(",")
                    .append(sanitize(getDisposition1(document))).append(",")
                    .append(sanitize(getDisposition2(document))).append(",")
                    .append(sanitize(getDispositionCharge1(document))).append(",")
                    .append(sanitize(getDispositionOutcome(document))).append(",")
                    .append(sanitize(document.select("#RCDCD1").text())).append(",")
                    .append(sanitize(getSentence1(document))).append(",")
                    .append(sanitize(getJudicialOfficeSentence1(document))).append(",")
                    .append(sanitize(getSentencedCharge1(document))).append(",")
                    .append(sanitize(getSentence1outcome1(document))).append(",")
                    .append(sanitize(getSentence1Confinment(document))).append(",")
                    .append(sanitize(getSentence1Suspension(document))).append(",")
                    .append(sanitize(getSentence1ConfDate(document))).append(",")
                    .append(sanitize(getSentence1Prov(document))).append(",")
                    .append(getDateAbout(document, "Request for Counsel And Order Appointing")).append(",")
                    .append(getDateAbout(document, "Magistrate's Warning"));
            LOGGER.info(info);
        } catch (Exception ex) {
            System.err.println("could not parse ");
            ex.printStackTrace();
        }
    }

    private static String getAttorney(Document document) {
        for (int j = 7; j < 9; j++) {
            for (int i = 2; i < 15; i++) {
                String attorney = document.select("body > table:nth-child(" + j + ") > tbody > tr:nth-child(" + i + ") > td:nth-child(5) > b").text();
                if (!attorney.isEmpty())
                    return attorney;
            }
        }

        return "";
    }

    private static String getCaseType(Document document) {
        String caseType = document.select("body > table:nth-child(6) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > table > tbody > tr:nth-child(1) > td > b").text();
        caseType = caseType.contains("- Filed by Information") ? caseType.replace("- Filed by Information", "") : caseType;
        return caseType;
    }

    private static String getTrntrs(Document document) {
        if (document.select("body > table:nth-child(6) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > table > tbody > tr:nth-child(4) > th").text().equals("Cr.Trntrs:")) {
            return document.select("body > table:nth-child(6) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > table > tbody > tr:nth-child(4) > td > b").text();
        }
        return "";
    }

    private static String getDefendant(Document document) {
        for (int i = 1; i < 5; i++) {
            String text = document.select("#PIr0" + i).text();
            if (text.toLowerCase().equals("defendant")) {
                return document.select("#PIr1" + i).text().replaceAll("\"", "");
            }
        }
        return "";
    }

    private static String getHeightWeight(Document document) {
        String text = document.text();
        int lbs = text.indexOf("lbs");
        if (lbs != -1) {
            return text.substring(lbs - 11, lbs + 3).replaceAll("&nbsp", "").replaceAll(";", "").replaceAll("\"", "");
        }
        return ",";
    }

    private static String sanitize(String text) {
        text = text.contains(",") ? ("\"" + text + "\"") : text;
        text = text.replaceAll("&nbsp", " ");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        return text;
    }

    private static String getCharge1(Document document) {
        for (int i = 8; i < 10; i++) {
            String charge = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(2) > td:nth-child(2)").text();
            if (!charge.isEmpty())
                return charge;
        }
        return "";
    }

    private static String getCharge2(Document document) {
        for (int i = 8; i < 10; i++) {
            String value = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(4) > td:nth-child(2)").text();
            if (!value.isEmpty())
                return value;
        }
        return "";
    }

    private static String getStatue2(Document document) {
        for (int i = 8; i < 10; i++) {
            String value = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(4) > td:nth-child(4)").text();
            if (!value.isEmpty())
                return value;
        }
        return "";
    }

    private static String getStatue1(Document document) {
        for (int i = 8; i < 10; i++) {
            String value = document.select("body > table:nth-child(" + 8 + ") > tbody > tr:nth-child(2) > td:nth-child(4)").text();
            if (!value.isEmpty())
                return value;
        }
        return "";
    }

    private static String getLevel1(Document document) {
        for (int i = 8; i < 10; i++) {
            String value = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(2) > td:nth-child(5)").text();
            if (!value.isEmpty())
                return value;
        }
        return "";
    }

    private static String getLevel2(Document document) {
        for (int i = 8; i < 10; i++) {
            String value = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(4) > td:nth-child(5)").text();
            if (!value.isEmpty())
                return value;
        }
        return "";
    }

    private static String getDate1(Document document) {
        for (int i = 8; i < 10; i++) {
            String value = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(2) > td:nth-child(6)").text();
            if (!value.isEmpty())
                return value;
        }
        return "";
    }

    private static String getDate2(Document document) {
        for (int i = 8; i < 10; i++) {
            String value = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(4) > td:nth-child(6)").text();
            if (!value.isEmpty())
                return value;
        }
        return "";
    }

    private static String getDisposition1(Document document) {
        String text = document.select("body > table:nth-child(9) > tbody > tr:nth-child(2) > td:nth-child(4) > div > div > div").text();
        if (text.length() > 1) {
            return text.substring(0, 1);
        }
        return "";
    }

    private static String getDisposition2(Document document) {
        for (int i = 9; i < 11; i++) {
            String text = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(2) > td:nth-child(4) > div").text();
            if (!text.isEmpty()) {
                String[] split = text.split(":");
                if (split.length > 1) {
                    String desp2 = split[1];
                    return desp2.substring(1, desp2.indexOf(")"));
                }
            }
        }
        return "";
    }

    private static String getDispositionCharge1(Document document) {
        for (int i = 9; i < 11; i++) {
            Elements select = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(2) > td:nth-child(4) > div > div > div");
            if (select.size() > 0) {
                if (select.get(0).childNodeSize() > 0) {
                    String string = select.get(0).childNode(0).toString();
                    if (string.length() > 10)
                        return string.substring(10);
                }
            }
        }
        return "";
    }

    private static String getDispositionOutcome(Document document) {
        for (int i = 9; i < 11; i++) {
            Elements select = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(2) > td:nth-child(4) > div > div > div");
            if (select.size() > 0) {
                String text = select.get(0).text();
                String[] split = text.split("-");
                if (split.length <= 1) {
                    split = text.split(" ");
                }
                return split.length > 1 ? split[split.length - 1] : "";
            }
        }
        return "";
    }

    private static String getSentence1(Document document) {
        for (int i = 9; i < 11; i++) {
            String value = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(3) > td:nth-child(4) > div > div:nth-child(3) > div").text();
            if (value.length() > 1) {
                return value.substring(0, 1);
            }
        }
        return "";
    }

    private static String getJudicialOfficeSentence1(Document document) {
        for (int j = 9; j < 11; j++) {
            Elements select = document.select("body > table:nth-child(" + j + ") > tbody > tr:nth-child(4) > td:nth-child(4) > div");
            if (select.size() > 0) {
                if (select.get(0).childNodeSize() > 4) {
                    for (int i = 0; i < select.get(0).childNodeSize(); i++) {
                        String string = select.get(0).childNode(i).toString();
                        if (string.contains("Judicial Officer")) {
                            String[] split = string.split(":");
                            if (split.length > 1) {
                                return split[1].replace(')', ' ');
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    private static String getSentencedCharge1(Document document) {
        for (int i = 9; i < 11; i++) {
            Elements select = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(3) > td:nth-child(4) > div > div:nth-child(3) > div");
            if (select.size() > 0) {
                Element element = select.get(0);
                if (element.childNodeSize() > 0) {
                    String string = element.childNode(0).toString();
                    if (string.length() > 10)
                        return string.substring(10);
                }
            }
        }
        return "";
    }

    private static String getSentence1outcome1(Document document) {
        for (int i = 9; i < 11; i++) {
            for (int j = 3; j < 5; j++) {
                String outcome = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(" + j + ") > td:nth-child(4) > div > div:nth-child(3) > div > div > table:nth-child(1) > tbody > tr:nth-child(1) > td").text();
                if (!outcome.isEmpty()) {
                    return outcome;
                }
            }
        }
        return "";
    }

    private static String getSentence1Confinment(Document document) {
        for (int i = 9; i < 11; i++) {
            for (int j = 3; j < 5; j++) {
                String confinement = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(" + j + ") > td:nth-child(4) > div > div:nth-child(3) > div > div > table:nth-child(2) > tbody > tr:nth-child(2) > td.ssMenuText.ssSmallText").text();
                if (!confinement.isEmpty()) {
                    return confinement;
                }
            }
        }
        return "";
    }

    private static String getSentence1Suspension(Document document) {
        for (int i = 9; i < 11; i++) {
            String val = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(3) > td:nth-child(4) > div > div:nth-child(3) > div > div > table:nth-child(2) > tbody > tr:nth-child(3) > td.ssMenuText.ssSmallText > nobr > span").text();
            if (!val.isEmpty())
                return val;
        }
        return "";
    }

    private static String getSentence1ConfDate(Document document) {
        return document.select("#RCDCD2").text();
    }

    private static String getSentence1Prov(Document document) {
        for (int i = 9; i < 11; i++) {
            String provision = document.select("body > table:nth-child(" + i + ") > tbody > tr:nth-child(3) > td:nth-child(4) > div > div:nth-child(3) > div > div > table:nth-child(4) > tbody > tr:nth-child(1) > td > table > tbody > tr > td").text();
            if (!provision.isEmpty() && provision.indexOf("(") != -1 && provision.indexOf(")") != -1) {
                return provision.substring(provision.indexOf("(") + 1, provision.indexOf(")"));
            }
        }
        return "";
    }

    private static String getDateAbout(Document document, String about) {
        StringBuilder allDates = new StringBuilder();
        String text = document.text();
        String[] split = text.split(about);

        for (int i = 0; i < split.length - 1; i++) {
            String str = split[i];
            if (str.length() > 15) {
                for (int j = 4; j < 6; j++) {
                    String date = str.substring(str.length() - (j + 10), str.length() - j);
                    if (isValidDateFormat(date)) {
                        allDates.append(date).append("|");
                        break;
                    }
                }
            }
        }

        if (allDates.length() > 0) {
            allDates.setLength(allDates.length() - 1);
        }
        return allDates.toString();
    }

    private static boolean isValidDateFormat(String strDate) {
        SimpleDateFormat sdfrmt = new SimpleDateFormat("MM/dd/yyyy");
        try {
            sdfrmt.parse(strDate);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
