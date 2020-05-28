FROM java:8
VOLUME /tmp
COPY web/target/web-0.0.1-SNAPSHOT.jar demo.jar
COPY doc/Map/lanes /doc/Map/lanes
RUN bash -c "touch /demo.jar"
EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]