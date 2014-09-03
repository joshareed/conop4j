FROM dockerfile/java

MAINTAINER Josh Reed, jareed@andrill.org

EXPOSE 5050

ENTRYPOINT ["/app/conop4j.sh"]
CMD ["help"]

ADD conop4j.sh /app/conop4j.sh

ADD conop4j-all.jar /app/conop4j-all.jar