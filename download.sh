#!/bin/bash


for day in 14 15 16 17 18 19;
do
  for time in `seq -f%02g 0 23`;
  do
     f="pagecounts-201503${day}-${time}0000.gz"
     wget http://dumps.wikimedia.org/other/pagecounts-raw/2015/2015-03/$f -O - | gunzip |\
        egrep "^(en|pl|uk|ru) " | cut -d ' ' -f1,2,3 | gzip > $f
  done
done
