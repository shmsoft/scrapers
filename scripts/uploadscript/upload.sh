#!/bin/bash
file=0000.jsonl
while IFS= read -r line
do
        # display $line or do somthing with $line
	#printf '%s\n' "$line"
	curl -XPOST "http://35.170.164.252:9200/memex/escorts" -H "Content-Type:application/json" -d "$line"
done <"$file"
