FROM dockerfile/java

MAINTAINER Josh Reed, jareed@andrill.org

ADD conop4j.sh /app/conop4j.sh
ADD https://github.com/joshareed/conop4j/releases/download/0.10.3/conop4j-all-0.10.3.jar /app/conop4j-all.jar

EXPOSE 5050

ENTRYPOINT "/app/conop4j.sh"