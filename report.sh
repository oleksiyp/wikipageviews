#!/bin/bash

rm -f memory.txt
touch memory.txt
for lang in ru uk pl en;
do
echo $lang
for file in page*.gz;
do
  echo $file
  cat $file | gunzip | grep "^$lang" |\
    sort -k3 -n -r | uniq | head -n 1000 |\
    awk '$3 >= 100 { print }' |\
    python2 -c 'import sys, urllib; print urllib.unquote(sys.stdin.read())' |\
    grep -v '^$' > result.txt
    cut -d ' ' -f 2 result.txt | grep -v -F -f memory.txt | head -n 10 | grep -v '^$'  > rfilt.txt
    grep -F -f rfilt.txt result.txt
    cat rfilt.txt >> memory.txt
done
echo
done
