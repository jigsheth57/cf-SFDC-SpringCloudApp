FROM openjdk:8-jre
MAINTAINER Jignesh Sheth <jigsheth@yahoo.com>
VOLUME /tmp
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 9022
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
HEALTHCHECK CMD curl -f http://localhost:9022/manage/health || exit 1