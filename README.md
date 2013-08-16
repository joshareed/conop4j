conop4j
=======

conop4j is a Java implementation of the simulated annealing algorithm of CONOP9 and some post processing tools for CONOP9 data. Building the code should be as simple as:

	./gradlew clean release
	
This will generate a JAR file in `build/release`.  You can run the generated JAR file with:

	java -jar conop-tools-X.Y.Z.jar

It provides a barebones GUI for accessing the analysis and search tools. You can also perform all of the same search and analysis functions from the command line.

To run a simulation:

	java -jar conop-tools-X.Y.Z.jar run <simulation file>

To score a solution:

	java -jar conop-tools-X.Y.Z.jar score <run directory> <solution file>

To post-process the CONOP9 data:

	java -jar conop-tools-X.Y.Z.jar process <run directory> <output file>
	
More Info
---------

For more information about conop4j, check out the [wiki](https://github.com/joshareed/conop4j/wiki).