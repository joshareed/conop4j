conop4j [![Build Status](https://travis-ci.org/joshareed/conop4j.png?branch=master)](https://travis-ci.org/joshareed/conop4j)
=======

conop4j is a Java implementation of the simulated annealing algorithm of CONOP9 and some post processing tools for CONOP9 data. Building the code should be as simple as:

	./gradlew clean release
	
This will generate a JAR file in `build/release`.  You can run the generated JAR file with:

	java -jar conop-tools-X.Y.Z.jar

It provides a barebones GUI for accessing the analysis and search tools. You can also perform all of the same search and analysis functions from the command line.

To run a simulation using the standard CONOP solver:

	java -jar conop-tools-X.Y.Z.jar conop <simulation file>
	
To run a simulation using the experimental QNOP solver:

	java -jar conop-tools-X.Y.Z.jar qnop <simulation file>

To post-process the CONOP9 data:

	java -jar conop-tools-X.Y.Z.jar process <run directory> <output file>
	
More Info
---------

For more information about conop4j, check out the [wiki](https://github.com/joshareed/conop4j/wiki).
