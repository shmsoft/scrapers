package locscraper;

import config.ConfigReader;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import reader.Provider;

import java.util.List;


/**
 * Created by hojha on 21/08/17.
 */
public class LocationAndSessionScraper {

    final static Logger LOGGER = Logger.getLogger(LocationAndSessionScraper.class);

    public static void main(String[] args) throws Exception {
        long delayInMillis = Long.parseLong(ConfigReader.get("delay"));
        System.out.println("delayInMillis " + delayInMillis);
        List<Integer> providers = Provider.getProviders();
        System.out.println("total size = " + providers.size());

        LOGGER.info("provider_id | city | Session Location");
        String cookie = ConfigReader.get("cookie");
        boolean useProxy = Boolean.parseBoolean(ConfigReader.get("useproxy"));
        System.out.println("useProxy = " + useProxy);

        for (int i = 0; i < providers.size(); i++) {
            Integer provider = providers.get(i);
            if (provider < 1) {
                continue;
            }
            try {
                getInformationFor(cookie, "" + provider, useProxy);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                if (ex.getMessage().equals("null")) {
                    throw new RuntimeException("");
                }
            }
            Thread.sleep(delayInMillis);
        }
    }

    private static void getInformationFor(String cookie, String provider, boolean userProxy) throws Exception {

        if (userProxy) {
            System.setProperty("https.proxyHost", "localhost");
            System.setProperty("https.proxyPort", "8118");
        }

        URIBuilder uriBuilder = new URIBuilder("https://www.theeroticreview.com/reviews/show.asp");
        uriBuilder.addParameter("id", provider);
        Connection.Response response = Jsoup.connect(uriBuilder.toString())
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
                .execute();
        printInformation(response.parse(), provider, cookie, userProxy);
    }

    private static void printInformation(Document doc, String provider, String cookie, boolean userProxy) throws Exception {
        if (doc.text().contains("Verification Required")) {
            throw new Exception();
        }

        if (userProxy) {
            System.setProperty("https.proxyHost", "localhost");
            System.setProperty("https.proxyPort", "8118");
        }

        String result = provider;
        if (!doc.select("a.basic-user-link").text().isEmpty()) {
            result += " | " + doc.select("a.basic-user-link").text();
        }

        //doc.select("div.ter-captcha-form").select("div").get(1).attr("data-sitekey")
        if (doc.select("a.td-link").size() > 0) {
            String link = "https://www.theeroticreview.com" + doc.select("a.td-link").get(0).attr("href");
            Thread.sleep(1000);
            Document childDoc = Jsoup.connect(link)
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
                result += " | " + sessionLocation.get(4).text();
            }
        }
        LOGGER.info(result);
    }
}
