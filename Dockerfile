FROM openjdk:8-jdk
EXPOSE 9090:9090
RUN mkdir /app
COPY ./build/libs/ /app/
COPY ./resources /app/resources/
ADD ./build/libs/geotab-1.0-all.jar /app/geotab-1.0-all.jar
WORKDIR /app
CMD java -jar geotab-1.0-all.jar
