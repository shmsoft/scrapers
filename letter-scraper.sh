mkdir -p output
#java -cp target/scrapers-1.0-SNAPSHOT-jar-with-dependencies.jar nyscraper.NysDocScraper $1 output/output_$1/html > output/scraper_$1.log
tar cf html_$1.tar output/output_$1/
s3cmd put html_$1.tar s3://shmsoft/Greg/nys/
s3cmd setacl s3://shmsoft/Greg/nys/html_$1.tar --acl-public
s3cmd put output/scraper_$1.log s3://shmsoft/Greg/nys/
s3cmd setacl s3://shmsoft/Greg/nys/scraper_$1.log --acl-public
