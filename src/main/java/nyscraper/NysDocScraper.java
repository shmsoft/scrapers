package nyscraper;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by nehaojha on 27/02/18.
 */
public class NysDocScraper {

    private static final Logger LOGGER = Logger.getLogger(NysDocScraper.class);
    public static final int TIMEOUT = 600_000;
    public static String OUTPUT_DIR;
    public static final Set<String> DIN = new HashSet<>();
    public static final String NYS_DOCS_LOOKUP_URL = "http://nysdoccslookup.doccs.ny.gov/";

    static {
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "8118");
    }


    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.out.println("Please provide two arguments for letter to start with and outputdirectory");
            return;
        }

        String alphabetString = args[0];
        String outputDir = args[1];
        OUTPUT_DIR = outputDir;

        LOGGER.info("DIN, Inmate name, Sex, Birth date, Race/Ethnicity, Status, Facility, Date Received (Original), Date Received (Current), Admission Type, County of Commitment, " +
                "Latest Release Date, Crime/class1, Crime/class2, Crime/class3, Crime/class4, Aggregate Min Sentence, Aggregate Max Sentence, Earliest Release Date, " +
                "Earliest Release Type, Parole Hearing Date, Parole Hearing Type, Parole Eligibility Date, Conditional Release Date," +
                "Maximum Expiration Date, Maximum Expiration Date for Parole Supervision, Post Release Supervision Maximum Expiration Date, Parole Board Discharge Date");

        for (int i = alphabetString.charAt(0); i < 'z'; i++) {
            File file = new File(OUTPUT_DIR);
            if (!file.exists()) {
                file.mkdirs();
            }
            startScraping(alphabetString);
        }
    }

    private static void startScraping(String alphabet) throws Exception {
        getDocumentByURL(alphabet);
    }

    private static void getDocumentByURL(String alphabet) throws Exception {
        Document doc = null;
        do {
            doc = getNextDoc(doc, alphabet);
            if (Objects.nonNull(doc)) {
                for (int i = 2; i < 6; i++) {
                    Elements element = doc.select("#dinlist > tbody > tr:nth-child(" + i + ") > td:nth-child(1) > form");
                    getFormData(element, true);
                }
            }
        } while (hasNextLink(doc));
    }

    private static Document getNextDoc(Document doc, String alphabet) throws IOException {
        try {
            if (Objects.isNull(doc)) {
                return Jsoup.connect(NYS_DOCS_LOOKUP_URL)
                        .data("M00_LAST_NAMEI", alphabet)
                        .data("K01", "WINQ000")
                        .timeout(TIMEOUT)
                        .post();
            }
            return doc.select("#content > form").forms().get(0).submit().timeout(TIMEOUT).execute().parse();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private static boolean hasNextLink(Document doc) {
        try {
            return Objects.nonNull(doc) && Objects.nonNull(doc.select("#content > form"));
        } catch (Exception e) {
            return false;
        }

    }

    private static void getFormData(Elements element, boolean recurse) throws Exception {

        if (Objects.nonNull(element)) {
            List<FormElement> forms = element.forms();
            try {
                if (!forms.isEmpty()) {
                    FormElement form = forms.get(0);
                    Document document = form.submit().timeout(TIMEOUT).execute().parse();

                    String htmlFileName = document.select("#ii > table:nth-child(4) > tbody > tr:nth-child(1) > td:nth-child(2)").text();

                    if (Objects.isNull(htmlFileName) || htmlFileName.isEmpty() || htmlFileName.trim() == "") {
                        if (recurse) {
                            for (int i = 2; i < 6; i++) {
                                try {
                                    Elements innerElement = document.select("#content > table > tbody > tr:nth-child(" + i + ") > td:nth-child(1) > form");
                                    getFormData(innerElement, false);
                                } catch (Exception e) {
                                    System.err.println(e.getMessage());
                                }
                            }
                        }
                    } else {
                        htmlFileName = htmlFileName.substring(0, htmlFileName.length() - 1);
                        parseAndStoreHtmlPage(document, htmlFileName);
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }


    private static void parseAndStoreHtmlPage(Document document, String fileName) throws Exception {
        if (!DIN.contains(fileName)) {
            storeHTMLPage(document, fileName);
            storeRequiredDataInCSV(document);
            DIN.add(fileName);
        }
    }

    private static void storeHTMLPage(Document document, String htmlFileName) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_DIR + "/" + htmlFileName.trim() + ".html"))) {
            writer.write(document.toString());
        }
    }

    private static void storeRequiredDataInCSV(Document document) throws Exception {
        StringBuilder info = new StringBuilder();
        for (int i = 1; i < 26; i++) {
            String text = document.select("#ii > table:nth-child(4) > tbody > tr:nth-child(" + i + ") > td:nth-child(2)").text();
            if (text.length() > 0) {
                text = text.substring(0, text.length() - 1);
            }
            info.append(text).append(",");
        }
        LOGGER.info(info);
    }
}
