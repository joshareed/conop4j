Dockerized CONOP4J
==================

This directory contains support for running CONOP4J in a docker container. Pre-built images are available in the public Docker Hub
in the repository `joshareed/conop4j`.  If you want to build a local image for testing, the build instructions are at the end.

Running CONOP4J
---------------

CONOP4J consists of several different tools all bundled together.  Examples for running the most common ones are outlined below:

To run a copy of the job tracker/coordinator:

	docker run -d -p 5050:5050 joshareed/conop4j tracker http://<ip of docker host>:5050
	
The URL can be omitted if both the tracker and computation agents will be run on the same host. You should then be able to connect
to the tracker api in your browser by visiting: `http://<ip of docker host>:5050/api/jobs`

To run a computation agent that grabs jobs from the tracker:

	docker run -d joshareed/conop4j agent http://<ip of docker host>:5050/api/jobs
	
Finally, you can add jobs to the tracker by POSTing to the api with curl:

	curl -X POST --data-binary @/path/to/simulation/file http://<ip of docker host>:5050/api/jobs
	
After a few seconds, the agent should notice there is a job to run and begin running it.  This should cause a noticeable spike in CPU usage.
Visiting `http://<ip of docker host>:5050/api/jobs` will show that the job JSON has updated `stats` and `solution` sections:

	...
	"stats" : {
		"created" : 1406842004317,
		"updated" : 1406842004317,
		"iterations" : 585,
		"score" : 38761848551.130005
	},
	"solution" : {
		"events" : [ {
			"name" : "Actinocyclus fasciculatus LAD"
	...


Building CONOP4J
----------------

All CONOP4J releases should get published simultaneously to GitHub and the Docker Hub.  If you want to experiment with building your own
images, you must first build a shadowJar of the CONOP4J code and then copy it into this `docker` folder.  From the top-level directory:

	./gradlew clean shadowJar
	cp build/libs/conop4j-all-* docker/conop4j-all.jar
	
Then building a new Docker image should be as easy as:

	docker build -t "youruser/conop4j" .
	
