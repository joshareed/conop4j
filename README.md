[![Stories in Ready](https://badge.waffle.io/joshareed/conop4j.png?label=ready&title=Ready)](https://waffle.io/joshareed/conop4j)
CONOP4J [![Build Status](https://travis-ci.org/joshareed/conop4j.png?branch=master)](https://travis-ci.org/joshareed/conop4j)
=======

CONOP4J is a Java implementation of the simulated annealing algorithm of CONOP9 and some post processing tools for CONOP9 data. Building the code should be as simple as:

	./gradlew clean shadowJar
	
This will generate a JAR file in `build/libs`.  You can run the generated JAR file with:

	java -jar conop-all-X.Y.Z.jar

To run a simulation:

	java -jar conop-all-X.Y.Z.jar run <simulation file>
	
