FROM openjdk
VOLUME /tmp
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8761
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
HEALTHCHECK CMD curl -f http://localhost:8761/manage/health || exit 1