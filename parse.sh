
cut -d ' ' -f1 pagecounts-20150317-190000  | uniq | sort -u | uniq > file

python2 -c 'import sys, urllib; print urllib.unquote(sys.stdin.read())'
