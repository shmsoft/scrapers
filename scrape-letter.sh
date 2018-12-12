mkdir output
java -cp target/scrapers-1.0-SNAPSHOT-jar-with-dependencies.jar nyscraper.NysDocScraper $1 output/output_$1/html > output/scraper_$1.log
