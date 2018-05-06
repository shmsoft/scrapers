package locscraper;

import config.ConfigReader;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import reader.Reviewers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Queue;


/**
 * Created by hojha on 21/08/17.
 */
public class Scraper {

    final static Logger LOGGER = Logger.getLogger(Scraper.class);

    public static void main(String[] args) {
        long delayInMillis = Long.parseLong(ConfigReader.get("delay"));
        System.out.println("delayInMillis " + delayInMillis);
        Queue<String> reviewers = Reviewers.getReviewers();
        try {
            LOGGER.info("Name | Total Reviews | Provider Created | Date Submitted | Reviewer's Name");
            //Add cookie from browser - should be refreshed on every call
            String cookie = ConfigReader.get("cookie");
            boolean useProxy = Boolean.parseBoolean(ConfigReader.get("useproxy"));
            System.out.println("useProxy = " + useProxy);
            while (!reviewers.isEmpty()) {
                String reviewerName = reviewers.remove();
                getInformationFor(cookie, reviewerName, useProxy);
                Thread.sleep(delayInMillis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getInformationFor(String cookie, String reviewerName, boolean userProxy) throws URISyntaxException, IOException {

        if (userProxy) {
            System.setProperty("https.proxyHost", "localhost");
            System.setProperty("https.proxyPort", "8118");
        }
        URIBuilder uriBuilder = new URIBuilder("https://www.theeroticreview.com/reviews/searchbyreviewerResults.asp");
        uriBuilder.addParameter("MemberName", reviewerName);
        Document doc = Jsoup.connect(uriBuilder.toString())
                .header("Origin", "https://www.theeroticreview.com")
                .header("Host", "www.theeroticreview.com")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header("Referer", "https://www.theeroticreview.com/reviews/index.asp")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Cookie", cookie)
                .get();
        printInformation(doc, reviewerName);
    }

    private static void printInformation(Document doc, String reviewerName) {
        StringBuilder message = new StringBuilder();
        if (doc.select("div.ter-table").size() < 1) {
            message.append("Not Valid " + reviewerName);
            return;
        }
        if (doc.select("div.ter-table").size() < 2) {
            message.append("No reviews for " + reviewerName);
            return;
        }
        Elements elements = doc.select("div.ter-table").get(1).getElementsByClass("tr");
        for (int i = 1; i < elements.size(); i++) {
            Elements rowData = elements.get(i).getAllElements();
            message.append(rowData.get(1).text() + " | " + rowData.get(3).text() + " | " + rowData.get(4).text() + " | " + rowData.get(5).text() + " | " + reviewerName + rowData.get(1).select("a").attr("href").substring(rowData.get(1).select("a").attr("href").lastIndexOf("-") + 1) + "\n");
        }
        LOGGER.info(message.toString());
    }

    private static StringBuilder getCookie() throws IOException {
        Connection.Response res = Jsoup
                .connect("https://www.theeroticreview.com/memberlaunch/login.asp?dest=/main.asp")
                .data("USERNAME", "hypcar51", "PASSWORD", "phipsi209")
                .method(Connection.Method.POST)
                .execute();
        final StringBuilder stringBuilder = new StringBuilder();
        res.cookies().forEach((k, v) -> {
            stringBuilder.append(k + "=" + v + "; ");
        });
        return stringBuilder;
    }
}
