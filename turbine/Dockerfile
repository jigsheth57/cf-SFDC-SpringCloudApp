FROM java:8
VOLUME /tmp
ADD ./turbine-executable-2.0.0-DP.3-SNAPSHOT.jar /turbine.jar
RUN bash -c 'touch /turbine.jar'
EXPOSE 8989
CMD java -jar /turbine.jar --port 8000 --streams "$TURBINE_STREAMS"
