# scrapers
Prepares report for all reviewers.
Scrapper is main client, it requires delay configuration and
cookie from browser and should be put into config.properties.

# Reviewers
All reviewer list is present inside reviewer_only.csv

# Anonymization
For IP anonymization we have used tor, but tor does not allow
https proxy for which we have used privoxy.

Privoxy https call is forwarded to forward-socks5 / 127.0.0.1:9050 .


# Report
Report is append only and should be present inside /tmp/ directory
by name reviews.log


