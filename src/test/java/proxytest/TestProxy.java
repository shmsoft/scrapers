package proxytest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by hojha on 24/08/17.
 * Simple test to see privoxy is working fine, this will keep changing location and languages accordingly
 */
public class TestProxy {

    @Test
    public void testLocationChange() throws IOException {
        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "8118");
        Document doc = Jsoup.connect("https://www.google.com/")
                .get();
        System.out.println(doc.body().text());
    }
}
