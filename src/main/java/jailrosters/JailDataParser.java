package jailrosters;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by nehaojha on 01/06/18.
 */
public class JailDataParser {

    private static final Logger LOGGER = Logger.getLogger(CriminalCaseParser.class);

    public static void main(String[] args) throws IOException {

        LOGGER.info("booking number, defendant name, booked date, released date");

        String rootFolder = "/Users/nehaojha/Documents/jail_records";
        File records = new File(rootFolder);
        File[] jail_records = records.listFiles();
        for (File file : jail_records) {
            if (file.isFile() && !file.getName().equals(".DS_Store")) {
                Document document = Jsoup.parse(file, "UTF-8");
                System.out.println("file = " + file.getAbsolutePath());
                String recordCount = getRecordCount(document);

                int record_count = Integer.parseInt(recordCount);
                int count = 3;
                for (int i = 0; i < record_count; i++) {
                    parseDocument(document, count);
                    count++;
                }
            }
        }
    }

    private static void parseDocument(Document document, int count) {
        try {
            StringBuilder info = new StringBuilder();
            info.append(getBookingNumber(document, count)).append(",")
                    .append(getName(document,count)).append(",")
                    .append(getBookingDate(document, count)).append(",")
                    .append(getReleaseDate(document, count));
            LOGGER.info(info);
        } catch (
                Exception ex)

        {
            System.err.println("could not parse ");
            ex.printStackTrace();
        }

    }

    private static String getBookingNumber(Document document, int count) {
        Elements bookingDate = document.select("body > table:nth-child(5) > tbody > tr:nth-child(" + count + ") > td:nth-child(1) > a");
        if (Objects.nonNull(bookingDate)) {
            return bookingDate.text();
        }
        return "";
    }

    private static String getRecordCount(Document document) {
        Elements count = document.select("body > table:nth-child(4) > tbody > tr:nth-child(1) > td:nth-child(2) > b");
        if (Objects.nonNull(count)) {
            return count.text();
        }
        return "";
    }

    private static String getName(Document document, int count) {
        Elements defendant = document.select("body > table:nth-child(5) > tbody > tr:nth-child(" + count + ") > td:nth-child(2) > div:nth-child(1)");
        if (Objects.nonNull(defendant)) {
            String name = defendant.text();
            String[] defendantName = name.split(",");
            StringBuilder nameBuilder = new StringBuilder();
            for (String s : defendantName) {
                nameBuilder.append(s);
            }
            return nameBuilder.toString();
        }
        return "";
    }

    private static String getBookingDate(Document document, int bookingCount) {
        Elements bookingDate = document.select("body > table:nth-child(5) > tbody > tr:nth-child(" + bookingCount + ") > td:nth-child(3) > div");
        if (Objects.nonNull(bookingDate)) {
            return bookingDate.text();
        }
        return "";
    }

    private static String getReleaseDate(Document document, int releaseCount) {
        Elements releaseDate = document.select("body > table:nth-child(5) > tbody > tr:nth-child(" + releaseCount + ") > td:nth-child(4) > div");
        if (Objects.nonNull(releaseDate)) {
            return releaseDate.text();
        }
        return "";
    }

}
