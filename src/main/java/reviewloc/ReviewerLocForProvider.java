package reviewloc;

import config.ConfigReader;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import reader.Provider;

import java.util.Scanner;


/**
 * This class scrapes reviewer location without scraping provider information
 * Created by hojha on 21/08/17.
 */
public class ReviewerLocForProvider {

    private static final Logger LOGGER = Logger.getLogger(ReviewerLocForProvider.class);
    public static final String URL = "https://www.theeroticreview.com/reviews/detail/review-by-";

    static {
        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "8118");
    }

    public static void main(String[] args) throws Exception {
        String cookie = ConfigReader.get("cookie");
        boolean skipHeader = true;
        try (Scanner scanner = new Scanner(Provider.class.getClassLoader().getResourceAsStream("provider_id.csv"))) {
            while (scanner.hasNext()) {
                String nextLine = scanner.nextLine();
                if (skipHeader) {
                    skipHeader = false;
                    LOGGER.info(nextLine + ", Session Location");
                } else {
                    String[] strings = nextLine.split(",");
                    String reviewUrl = URL + strings[2] + "-" + strings[1];
                    printInformation(nextLine, reviewUrl, cookie);
                }
            }
        }
    }

    private static void printInformation(String data, String reviewUrl, String cookie) throws Exception {
        try {
            Document childDoc = Jsoup.connect(reviewUrl)
                    .header("Origin", "https://www.theeroticreview.com")
                    .header("Host", "www.theeroticreview.com")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:48.0) Gecko/20100101 Firefox/48.0")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Referer", "https://www.theeroticreview.com/reviews/index.asp")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "en-US,en;q=0.8")
                    .header("Cookie", cookie)
                    .header("Upgrade-Insecure-Requests", "1")
                    .timeout(60000)
                    .get();
            Elements sessionLocation = childDoc.select("p.person-review-mark");
            if (sessionLocation != null && sessionLocation.size() >= 4) {
                LOGGER.info(data + "," + sessionLocation.get(4).text());
            }
        } catch (Exception ex) {
            System.out.println("Error...." + ex.getMessage());
        }
    }
}
