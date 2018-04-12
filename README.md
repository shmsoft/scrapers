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
NysDoc Scraper is for scraping data from http://nysdoccslookup.doccs.ny.gov/
build mvn clean install and use runnable jar as below
java -cp scrapers-1.0-SNAPSHOT-jar-with-dependencies.jar nyscraper.NysDocScraper a ~/htmls
where first arg is alphabet to start with and second arg is path where scraped docs are kept.


### Code
nyscraper.NysDocScraper
nyscraper.NysDocScraper
    
### Results
https://s3.console.aws.amazon.com/s3/buckets/nysdocs/?region=us-east-1&tab=overview
    
## PollyklaasScraper
This class scrapes second column data regarding missing children
from http://www.pollyklaas.org/

Simply run this class pollyklaas.PollyklaasScraper
or with one arg pollyklaas.PollyklaasScraper /tmp/htmls/myhtml to override raw data storage

###Results
https://s3.console.aws.amazon.com/s3/buckets/shmsoft/Greg/polyklaas