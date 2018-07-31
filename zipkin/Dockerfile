FROM java:8
VOLUME /tmp
ADD ./zipkin-server-2.7.1-exec.jar zipkin.jar
RUN bash -c 'touch /zipkin.jar'
EXPOSE 9411
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/zipkin.jar"]
HEALTHCHECK CMD curl -f http://localhost:9411/health || exit 1