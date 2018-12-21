FROM openjdk:8-jre
MAINTAINER Jignesh Sheth <jigsheth@yahoo.com>
VOLUME /tmp
ADD ./turbine-executable-2.0.0-DP.3-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8000
CMD java -Djava.security.egd=file:/dev/./urandom -jar /app.jar --port 8000 --streams "$TURBINE_STREAMS"