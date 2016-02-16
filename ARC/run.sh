#!/bin/bash
set -e

if [ $1 = "ARC" ]; then
	javac Trace.java
	javac ARC.java
	java ARC $2 $3
elif [ $1 = "LRU" ]; then
	javac Trace.java
	javac LRU.java
	java LRU $2 $3 
elif [ $1 = "ALL" ]; then
	rm -rf *.class
	rm -rf *~
	rm -rf *.txt
	javac Trace.java
	javac ARC.java
	javac LRU.java
	cacheSize=( 1024 2048 4096 8192 16384 32768 )
	progFileName=( ARC LRU )
	for pg in "${progFileName[@]}"
	do
		for fn in "${@:2}"
		do
			for cs in "${cacheSize[@]}"
			do
				echo "Doing for $pg $fn $cs"
				java -Xmx6g $pg $cs $fn >> output.txt
				echo "Done for $pg $fn $cs"
			done
			echo " "
		done
	done
elif [ $1 = "CLEAN" ]; then
	rm -rf *.class
	rm -rf *~
	rm -rf *.txt
else
	echo "Usage: "
	echo './run.sh ARC <cache_size> <input_file_name>'
	echo './run.sh LRU <cache_size> <input_file_name>'
	echo './run.sh ALL <space separated trace file names (excluding ".lis" extension) ...>'
	echo './run.sh CLEAN'
fi
