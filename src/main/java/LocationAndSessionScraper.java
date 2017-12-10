import config.ConfigReader;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import reader.Provider;
import reader.ResultProviders;

import java.util.List;
import java.util.Set;


/**
 * Created by hojha on 21/08/17.
 */
public class LocationAndSessionScraper {

    final static Logger LOGGER = Logger.getLogger(LocationAndSessionScraper.class);

    public static void main(String[] args) throws Exception {
        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "8118");

        Set<Integer> providers = Provider.getProviders();
        List<Integer> resultProviders = ResultProviders.getResultProviders();

        System.out.println("Before remove size = " + providers.size());
        System.out.println("result has " + resultProviders.size() + " providers");
        for (Integer number : resultProviders) {
            providers.remove(number);
        }
        System.out.println("After remove size = " + providers.size());


        LOGGER.info("provider_id | city | Session Location");
        String cookie = ConfigReader.get("cookie");

        int count = 0;
        for (Integer provider : providers) {
            if (count++ == 500) {
                count = 0;
                Thread.sleep(30000);
            }
            new Thread(() -> {
                if (provider > 0) {
                    try {
                        getInformationFor(cookie, "" + provider);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        if (ex.getMessage().equals("null")) {
                            throw new RuntimeException("");
                        }
                    }
                }
            }).start();
        }
    }

    private static void getInformationFor(String cookie, String provider) throws Exception {
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
        printInformation(response.parse(), provider, cookie);
    }

    private static void printInformation(Document doc, String provider, String cookie) throws Exception {
        if (doc.text().contains("Verification Required")) {
            throw new Exception();
        }

        String result = provider;
        if (doc.select("a.basic-user-link").size() > 0 && !doc.select("a.basic-user-link").get(0).text().isEmpty()) {
            result += " | " + doc.select("a.basic-user-link").get(0).text();
        }

        //doc.select("div.ter-captcha-form").select("div").get(1).attr("data-sitekey")
        if (doc.select("a.td-link").size() > 0) {
            String link = "https://www.theeroticreview.com" + doc.select("a.td-link").get(0).attr("href");
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
