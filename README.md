# Project: Scrapers

## TER Scapter

### Code

    locscraper.Scraper
    
Scrapes the site, scraping one URL per reviewer
Scraper is the main client, it requires delay configuration and
cookie from a browser and should be put into config.properties.

### Reviewers
The list of reviewers is present inside reviewer_only.csv

### Anonymization
For IP anonymization we have used Tor, but Tor does not allow
https proxy, so we use privoxy.

Privoxy https call is forwarded to forward-socks5 / 127.0.0.1:9050 .


### Report
Report is append only and should be present inside /tmp/ directory
under the name 'reviews.log'

## NYS Scraper

* NysDoc Scraper is for scraping data from http://nysdoccslookup.doccs.ny.gov/
* Build 
    
    mvn clean install 
    
* Run    
    
    java -cp target/scrapers-1.0-SNAPSHOT-jar-with-dependencies.jar nyscraper.NysDocScraper a output/html

    where 
    
* the first arg is alphabet letter to start with 
* the second arg is the path to the directory  where scraped docs are written to

Script to do it: ./scrape-letter.sh a &
When done, upload to s3, like so:
tar cf html_a.tar output/output_a/
s3cmd put a.tar s3://shmsoft/Greg/nys/
s3cmd put scraper_a.log s3://shmsoft/Greg/nys/
### Code
nyscraper.NysDocScraper
nyscraper.NysDocScraper
    
### Results
https://s3.console.aws.amazon.com/s3/buckets/nysdocs/?region=us-east-1&tab=overview
    
## PollyklaasScraper
This class scrapes second column data regarding missing children
from http://www.pollyklaas.org/

Simply run this class pollyklaas.PollyklaasScraper
or with two args
pollyklaas.PollyklaasScraper outputpath inputpath

###Results
https://s3.console.aws.amazon.com/s3/buckets/shmsoft/Greg/polyklaas

##Move files
This class copies files from one s3 and puts files on another s3 one by one and does cleanup
