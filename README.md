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

### Code

    nyscraper.NysDocScraper


