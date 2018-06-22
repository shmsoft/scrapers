package jailrosters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by nehaojha on 21/05/18.
 */
public class CriminalCaseCrawler {

    private static final int TIMEOUT = 60000;
    //ask
    private static final String cookie = "ASP.NET_SessionId=3sihpk4535a35l55h03rus55; .ASPXFORMSPUBLICACCESS=6396C9259BBFF1BF6D00E2A647720D60191F728149FDD47150FA2F89512FF36C781C787E4A145F260160A13CD1274D821170EDD0CADE8175E1910708E3B66811BFE4E2962A821786A2C000D9E3BEA483DD8A4124933E1D2B4B223D82DE71799A390052E06D828517002B36D63B6BECE31F867944DAD91CE5F69148AFA8734EBC1FBE070DEC94827A4D6C93039B68C2DE8BD7310E";
    public static final String CASE_URL = "https://odysseypa.tylerhost.net/Gillespie/CaseDetail.aspx?CaseID=";
    public static final int MAX_LINKS_TO_VISIT = 100000;

    public static void main(String[] args) throws Exception {
        String rootFolder = "/Users/nehaojha/Documents/gillespie";//ask
        File attorneyDetails = new File(rootFolder);
        if (!attorneyDetails.exists()) {
            throw new IllegalArgumentException("there is no file inside root folder");
        }

        File[] files = attorneyDetails.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".htm")) {
                System.out.println("Parsing file " + file.getName());
                String caseFilePath = createCaseFolder(file);
                Document parsedHtml = Jsoup.parse(file, "UTF-8");
                for (int i = 4; i < MAX_LINKS_TO_VISIT; i++) {
                    Elements select = parsedHtml.select("body > table:nth-child(5) > tbody > tr:nth-child(" + i + ") > td:nth-child(1) > a");
                    if (select.text().isEmpty()) {
                        break;
                    }
                    String[] hrefs = select.attr("href").split("=");
                    if (hrefs.length > 1) {
                        System.out.println("Got link to visit " + hrefs[1]);
                        Document caseDoc = getCaseDocForCaseId(hrefs[1]);
                        if (caseDoc.text().contains("Public Access Error")) {
                            System.err.println("Got Error");
                            break;
                        }
                        storeCaseDoc(caseDoc, caseFilePath + File.separator + hrefs[1] + ".htm");
                    } else {
                        System.err.println("Could not read link properly..." + select.attr("href"));
                    }
                }
            } else {
                System.err.println("Ignoring file " + file.getName());
            }
        }
    }

    private static String createCaseFolder(File file) {
        String absPath = file.getAbsolutePath();
        String caseFilePath = absPath.substring(0, absPath.length() - 4) + "_cases";
        File caseFile = new File(caseFilePath);
        if (!caseFile.exists()) {
            caseFile.mkdir();
        }
        return caseFilePath;
    }

    private static Document getCaseDocForCaseId(String caseId) throws IOException {
        return Jsoup.connect(CASE_URL + caseId)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header("Cookie", cookie)
                .timeout(TIMEOUT)
                .get();
    }

    private static void storeCaseDoc(Document document, String htmlFileName) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFileName))) {
            writer.write(document.toString());
        }
    }
}
