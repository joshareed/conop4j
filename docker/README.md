Dockerized CONOP4J
==================

This directory contains initial support for Dockerizing CONOP4J.

To build the docker image:

	docker build -t conop4j .
	
The Dockerfile is configured to pull the latest release from Github.  The image is parameterized so it can run any of the CONOP4J commands. It does this via the `C4J_CMD` and `C4J_CMD` environment variables.

To run a copy of the job tracker/coordinator:

	docker run -d -e "C4J_CMD=tracker" -e "JAVA_OPTS=-Dratpack.publicAddress=http://<ip of docker host>:5050" -p 5050:5050 conop4j

You should then be able to connect to the tracker api in your browser by visiting: `http://<ip of docker host>:5050/api/jobs`

To run a computation agent to that grabs jobs from the tracker:

	docker run -d -e "C4J_CMD=agent" -e "C4J_ARGS=http://<ip of docker host>:5050/api/jobs" conop4j

Finally, you can add jobs to the tracker by POSTing to the api with curl:

	curl -X POST --data-binary @/path/to/simulation/file http:<ip of docker host>:5050/api/jobs
	

